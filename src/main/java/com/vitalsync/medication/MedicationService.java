package com.vitalsync.medication;

import com.vitalsync.medication.dto.ChecklistLogDTO;
import com.vitalsync.medication.dto.DailyScheduleDTO;
import com.vitalsync.medication.dto.MedicationRequestDTO;
import com.vitalsync.medication.dto.MedicationResponseDTO;
import com.vitalsync.medication.mapper.MedicationMapper;
import com.vitalsync.shared.enums.LogStatus;
import com.vitalsync.sharing.SharingService;
import com.vitalsync.user.UserEntity;
import org.eclipse.microprofile.jwt.JsonWebToken;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class MedicationService {

    private final MedicationMapper mapper;
    private final JsonWebToken jwt;
    private final SharingService sharingService;

    public MedicationService(MedicationMapper mapper, JsonWebToken jwt, SharingService sharingService) {
        this.mapper = mapper;
        this.jwt = jwt;
        this.sharingService = sharingService;
    }

    @Transactional
    public MedicationResponseDTO create(MedicationRequestDTO dto) {
        // 1. Descobrir quem é o usuário logado
        // O "jwt.getSubject()" retorna o ID (UUID) configurado no "sub" do Token
        String userId = jwt.getSubject();

        UserEntity patient = UserEntity.findById(UUID.fromString(userId));
        if (patient == null) {
            throw new WebApplicationException("Paciente não encontrado. O ID do token não existe no banco: " + userId, Response.Status.NOT_FOUND);
        }

        // 2. Converter DTO -> Entity
        MedicationEntity entity = mapper.toEntity(dto);

        // 3. Completar dados que faltaram no Mapper
        entity.setPatient(patient);
        if (entity.getStartDate() == null) {
            entity.setStartDate(LocalDate.now());
        }

        // 4. Vínculo Bidirecional
        // O Mapper cria a lista de schedules, mas o campo 'medication' dentro delas está null.
        if (entity.getSchedules() != null) {
            entity.getSchedules().forEach(schedule -> schedule.setMedication(entity));
        }

        // 5. Salvar
        entity.persist();

        return mapper.toResponse(entity);
    }

    public List<MedicationResponseDTO> listMyMedications() {
        String userId = jwt.getSubject();

        // Busca apenas as medicações DO USUÁRIO LOGADO
        // E ordena por nome para ficar bonito na tela
        List<MedicationEntity> entities = MedicationEntity
                .find("patient.id = ?1 ORDER BY name", UUID.fromString(userId))
                .list();

        return mapper.toResponseList(entities);
    }

    @Transactional
    public void delete(UUID id) {
        String userId = jwt.getSubject();

        // Verificação de Segurança: O remédio existe E pertence a quem está tentando apagar?
        MedicationEntity entity = MedicationEntity.findById(id);

        if (entity == null) {
            throw new WebApplicationException("Medicamento não encontrado", Response.Status.NOT_FOUND);
        }

        // Se o ID do dono do remédio for diferente do ID do token -> PROIBIDO
        if (!entity.getPatient().getId().toString().equals(userId)) {
            throw new WebApplicationException("Você não tem permissão para excluir este medicamento", Response.Status.FORBIDDEN);
        }

        entity.delete();
    }

    @Transactional
    public MedicationResponseDTO update(UUID id, MedicationRequestDTO dto) {
        String userId = jwt.getSubject();

        // 1. Busca e verifica propriedade (Segurança)
        MedicationEntity entity = MedicationEntity.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Medicamento não encontrado", Response.Status.NOT_FOUND);
        }
        if (!entity.getPatient().getId().toString().equals(userId)) {
            throw new WebApplicationException("Você não tem permissão para alterar este medicamento", Response.Status.FORBIDDEN);
        }

        // 2. Atualiza os campos simples
        mapper.updateEntityFromDto(dto, entity);

        // 3. Atualiza os Horários (Estratégia: Limpar e Recriar)
        // Como foi usado orphanRemoval=true na Entity, o clear() vai deletar os registros antigos do banco.
        if (entity.getSchedules() != null) {
            entity.getSchedules().clear();
        }

        // Criar os novos horários
        if (dto.schedules() != null) {
            List<MedicationScheduleEntity> newSchedules = mapper.mapSchedules(dto.schedules());

            // Re-vincular o pai (Bidirecional)
            newSchedules.forEach(s -> s.setMedication(entity));

            // Adicionar à lista existente
            entity.getSchedules().addAll(newSchedules);
        }

        entity.persist();

        return mapper.toResponse(entity);
    }

    public List<DailyScheduleDTO> getDailySchedule() {
        String userId = jwt.getSubject();
        LocalDate today = LocalDate.now();

        // 1. Buscar todas as medicações ativas do usuário
        List<MedicationEntity> medications = MedicationEntity
                .find("patient.id = ?1 and active = true", UUID.fromString(userId))
                .list();

        // 2. Buscar logs de hoje para evitar N+1 queries (performance)
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        // Query: Logs das medicações deste paciente, feitos hoje
        List<MedicationLogEntity> todaysLogs = MedicationLogEntity
                .find("medication.patient.id = ?1 and takenAt between ?2 and ?3",
                        UUID.fromString(userId), startOfDay, endOfDay)
                .list();

        List<DailyScheduleDTO> dailyList = new ArrayList<>();

        // 3. Cruzamento: Horários Agendados vs Logs Realizados
        for (MedicationEntity med : medications) {
            for (MedicationScheduleEntity schedule : med.getSchedules()) {

                // Verifica se existe log para este remédio próximo deste horário
                // (Lógica simplificada para o MVP: se tem log hoje, conta como tomado)
                // Num app real, verificaríamos se o log foi feito dentro de uma janela de tempo (ex: +/- 2 horas)
                boolean taken = todaysLogs.stream()
                        .anyMatch(log ->
                                log.getMedication().getId().equals(med.getId()) &&
                                        isSameTime(log.getExpectedAt(), schedule.getScheduledTime()) // Comparação de hora
                        );

                dailyList.add(new DailyScheduleDTO(
                        schedule.getId(),
                        med.getId(),
                        med.getName(),
                        med.getDosage(),
                        med.getInstructions(),
                        schedule.getScheduledTime(),
                        taken ? LogStatus.TAKEN : null, // Se não tomou, manda null (Pendente)
                        taken
                ));
            }
        }

        // Ordenar por horário (08:00 antes de 20:00)
        dailyList.sort((a, b) -> a.scheduledTime().compareTo(b.scheduledTime()));

        return dailyList;
    }

    @Transactional
    public void logIntake(UUID medicationId, ChecklistLogDTO dto) {
        String userId = jwt.getSubject();

        // 1. Validar existência e posse
        MedicationEntity medication = MedicationEntity.findById(medicationId);
        if (medication == null) {
            throw new WebApplicationException("Medicamento não encontrado", Response.Status.NOT_FOUND);
        }
        if (!medication.getPatient().getId().toString().equals(userId)) {
            throw new WebApplicationException("Acesso negado", Response.Status.FORBIDDEN);
        }

        MedicationLogEntity log = new MedicationLogEntity();
        log.setMedication(medication);
        log.setTakenAt(dto.takenAt().toLocalDateTime());
        log.setStatus(dto.status());


        // Precisamos definir o expectedAt corretamente.
        // O Front deve enviar o horário real da tomada.
        // Para o MVP, vamos procurar qual é o horário agendado mais próximo da hora que ele tomou.

        LocalTime takenTime = dto.takenAt().toLocalTime();

        // Procura o horário agendado mais próximo (Ex: tomou 08:05, o agendado era 08:00)
        if (medication.getSchedules() == null || medication.getSchedules().isEmpty()) {
             // Fallback se não tiver horários: usa a hora da tomada como "esperado"
             log.setExpectedAt(dto.takenAt().toLocalDateTime());
        } else {
        LocalTime closestScheduledTime = findClosestSchedule(medication.getSchedules(), takenTime);

        // Salva a data de hoje com a hora agendada correta
        log.setExpectedAt(dto.takenAt().toLocalDate().atTime(closestScheduledTime));
        }

        log.persist();
    }

    @Transactional
    public void checkSchedule(UUID scheduleId, ChecklistLogDTO dto) {
        String userId = jwt.getSubject();

        // 1. Buscar o Agendamento (Schedule) pelo ID que veio do front
        MedicationScheduleEntity schedule = MedicationScheduleEntity.findById(scheduleId);
        if (schedule == null) {
            throw new WebApplicationException("Agendamento não encontrado", Response.Status.NOT_FOUND);
        }

        // 2. Recuperar o Medicamento através do Agendamento
        MedicationEntity medication = schedule.getMedication();

        // 3. Validar Segurança (O remédio pertence ao usuário logado?)
        if (!medication.getPatient().getId().toString().equals(userId)) {
            throw new WebApplicationException("Acesso negado", Response.Status.FORBIDDEN);
        }

        // 4. Criar o Log
        MedicationLogEntity log = new MedicationLogEntity();
        log.setMedication(medication);
        log.setTakenAt(dto.takenAt().toLocalDateTime());
        log.setStatus(dto.status());

        // 5. Definir o Horário Esperado (ExpectedAt)
        // Usamos a data da tomada (hoje) combinada com a hora exata do agendamento
        log.setExpectedAt(dto.takenAt().toLocalDate().atTime(schedule.getScheduledTime()));

        log.persist();
    }

    public List<MedicationResponseDTO> listByPatientId(UUID patientId) {
        // 1. O Guardrail verifica se o médico tem permissão
        sharingService.validateDoctorAccess(patientId);

        // 2. Se passou, busca os dados daquele ID específico
        List<MedicationEntity> entities = MedicationEntity
                .find("patient.id = ?1 ORDER BY name", patientId)
                .list();

        return mapper.toResponseList(entities);
    }

    private boolean isSameTime(LocalDateTime logExpectedAt, LocalTime scheduleTime) {
        if (logExpectedAt == null) return false;
        // Compara apenas Hora e Minuto (ignorando segundos para segurança)
        LocalTime logTime = logExpectedAt.toLocalTime();
        return logTime.getHour() == scheduleTime.getHour() &&
                logTime.getMinute() == scheduleTime.getMinute();
    }

    private LocalTime findClosestSchedule(List<MedicationScheduleEntity> schedules, LocalTime takenTime) {
        LocalTime closest = schedules.get(0).getScheduledTime();
        long minDiff = Math.abs(java.time.Duration.between(takenTime, closest).toMinutes());

        for (MedicationScheduleEntity s : schedules) {
            long diff = Math.abs(java.time.Duration.between(takenTime, s.getScheduledTime()).toMinutes());
            if (diff < minDiff) {
                minDiff = diff;
                closest = s.getScheduledTime();
            }
        }
        return closest;
    }
}