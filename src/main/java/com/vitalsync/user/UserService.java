package com.vitalsync.user;

import com.vitalsync.user.dto.UserRegistrationDTO;
import com.vitalsync.user.dto.UserResponseDTO;
import com.vitalsync.user.mapper.UserMapper;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.mapstruct.factory.Mappers;

@ApplicationScoped
public class UserService {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Transactional
    public UserResponseDTO register(UserRegistrationDTO dto) {

        if (UserEntity.find("email", dto.email()).firstResult() != null) {
            throw new WebApplicationException(
                    "E-mail já cadastrado.",
                    Response.Status.CONFLICT
            );
        }

        UserEntity entity = mapper.toEntity(dto);

        entity.setPasswordHash(
                BcryptUtil.bcryptHash(dto.password())
        );

        entity.persist();

        return mapper.toResponse(entity);
    }
}