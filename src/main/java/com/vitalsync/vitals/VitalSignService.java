package com.vitalsync.vitals;

import com.vitalsync.user.UserEntity;
import com.vitalsync.vitals.dto.VitalSignCreateDTO;
import com.vitalsync.vitals.dto.VitalSignResponseDTO;
import com.vitalsync.vitals.mapper.VitalMapper;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class VitalSignService {

    private final VitalMapper mapper;
    private final JsonWebToken jwt;

    public VitalSignService(VitalMapper mapper, JsonWebToken jwt) {
        this.mapper = mapper;
        this.jwt = jwt;
    }

    @Transactional
    public VitalSignResponseDTO create(VitalSignCreateDTO dto) {
        String userId = jwt.getSubject();
        UserEntity patient = UserEntity.findById(UUID.fromString(userId));

        if (patient == null) {
            throw new WebApplicationException("Paciente não encontrado", Response.Status.NOT_FOUND);
        }

        VitalSignEntity entity = mapper.toEntity(dto);
        entity.setPatient(patient);

        if (entity.getMeasuredAt() == null) {
            entity.setMeasuredAt(LocalDateTime.now());
        }

        entity.persist();

        return mapper.toResponse(entity);
    }

    public List<VitalSignResponseDTO> listMyHistory() {
        String userId = jwt.getSubject();

        // Busca ordenando do mais recente para o mais antigo
        List<VitalSignEntity> list = VitalSignEntity
                .find("patient.id = ?1 ORDER BY measuredAt DESC", UUID.fromString(userId))
                .list();

        return mapper.toResponseList(list);
    }
}