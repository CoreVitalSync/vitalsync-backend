package com.vitalsync.user.mapper;

import com.vitalsync.user.UserEntity;
import com.vitalsync.user.dto.UserRegistrationDTO;
import com.vitalsync.user.dto.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    UserEntity toEntity(UserRegistrationDTO dto);

    UserResponseDTO toResponse(UserEntity entity);
}