package com.vitalsync.sharing;

import com.vitalsync.shared.enums.LinkStatus;
import com.vitalsync.sharing.dto.AcceptLinkDTO;
import com.vitalsync.sharing.dto.SharingResponseDTO;
import com.vitalsync.sharing.mapper.SharingMapper;
import com.vitalsync.user.UserEntity;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class SharingService {

    private final SharingMapper mapper;
    private final JsonWebToken jwt;

    public SharingService(SharingMapper mapper, JsonWebToken jwt) {
        this.mapper = mapper;
        this.jwt = jwt;
    }

    // --- PACIENTE GERA O CONVITE ---
    @Transactional
    public SharingResponseDTO createInvite() {
        String patientId = jwt.getSubject();
        UserEntity patient = UserEntity.findById(UUID.fromString(patientId));

        // Regra: Se já existe um convite PENDENTE, retorna ele mesmo (não gera lixo)
        DoctorPatientLinkEntity existing = DoctorPatientLinkEntity
                .find("patient.id = ?1 and status = ?2", patient.getId(), LinkStatus.PENDING)
                .firstResult();

        if (existing != null) {
            return mapper.toResponse(existing);
        }

        // Gera novo convite
        DoctorPatientLinkEntity link = new DoctorPatientLinkEntity();
        link.setPatient(patient);
        link.setStatus(LinkStatus.PENDING);
        // Gera um token curto (8 chars) para facilitar a digitação, ou UUID completo se preferir segurança total
        link.setInviteToken(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        link.persist();

        return mapper.toResponse(link);
    }

    // --- MÉDICO ACEITA O CONVITE ---
    @Transactional
    public SharingResponseDTO acceptInvite(AcceptLinkDTO dto) {
        String doctorId = jwt.getSubject();
        UserEntity doctor = UserEntity.findById(UUID.fromString(doctorId));

        // 1. Achar o convite pelo token
        DoctorPatientLinkEntity link = DoctorPatientLinkEntity
                .find("inviteToken = ?1", dto.token().toUpperCase())
                .firstResult();

        if (link == null) {
            throw new WebApplicationException("Convite inválido ou não encontrado", Response.Status.NOT_FOUND);
        }

        // 2. Verificar se já não foi usado
        if (link.getStatus() != LinkStatus.PENDING) {
            throw new WebApplicationException("Este convite já foi utilizado ou revogado", Response.Status.CONFLICT);
        }

        // 3. Vincular (Casamento)
        link.setDoctor(doctor);
        link.setStatus(LinkStatus.ACTIVE);
        link.setLinkedAt(LocalDateTime.now());

        return mapper.toResponse(link);
    }
}