package com.vitalsync.user;

import com.vitalsync.user.dto.UserLoginDTO;
import com.vitalsync.user.dto.UserRegistrationDTO;
import com.vitalsync.user.dto.UserResponseDTO;
import com.vitalsync.user.dto.UserResponseTokenDTO;
import com.vitalsync.user.mapper.UserMapper;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.mapstruct.factory.Mappers;

import java.time.Duration;

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

    public UserResponseTokenDTO login(UserLoginDTO dto) {
        UserEntity user = UserEntity.find("email", dto.email()).firstResult();

        if (user == null) {
            throw new WebApplicationException(
                    "Usuário não encontrado.",
                    Response.Status.NOT_FOUND
            );
        }

        if (!BcryptUtil.matches(dto.password(), user.getPasswordHash())) {
            throw new WebApplicationException(
                    "Senha incorreta.",
                    Response.Status.UNAUTHORIZED
            );
        }

        String token = Jwt.issuer("https://vitalsync.api/issuer")
                .upn(user.getEmail())
                .subject(String.valueOf(user.getId()))
                .groups(user.getRole().name())
                .expiresIn(Duration.ofHours(24))
                .sign();

        return new UserResponseTokenDTO(token);

    }

}