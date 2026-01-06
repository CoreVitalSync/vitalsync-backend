package com.vitalsync.sharing.mapper;

import com.vitalsync.sharing.DoctorPatientLinkEntity;
import com.vitalsync.sharing.dto.SharingResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface SharingMapper {

    @Mapping(target = "doctorName", source = "doctor.fullName")
    SharingResponseDTO toResponse(DoctorPatientLinkEntity entity);
}