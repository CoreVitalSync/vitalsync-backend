package com.vitalsync.medication;

import com.vitalsync.medication.dto.MedicationRequestDTO;
import com.vitalsync.medication.dto.MedicationResponseDTO;
import com.vitalsync.medication.mapper.MedicationMapper;
import com.vitalsync.user.UserEntity;
import org.eclipse.microprofile.jwt.JsonWebToken;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class MedicationService {

    private final MedicationMapper mapper;
    private final JsonWebToken jwt;

    public MedicationService(MedicationMapper mapper, JsonWebToken jwt) {
        this.mapper = mapper;
        this.jwt = jwt;
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
}