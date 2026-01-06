package com.vitalsync.sharing.mapper;

import com.vitalsync.sharing.DoctorPatientLinkEntity;
import com.vitalsync.sharing.dto.PatientSummaryDTO;
import com.vitalsync.sharing.dto.SharingResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface SharingMapper {

    @Mapping(target = "doctorName", source = "doctor.fullName")
    SharingResponseDTO toResponse(DoctorPatientLinkEntity entity);

    @Mapping(target = "id", source = "patient.id")
    @Mapping(target = "fullName", source = "patient.fullName")
    @Mapping(target = "email", source = "patient.email")
    @Mapping(target = "birthDate", source = "patient.birthDate")
    @Mapping(target = "inviteToken", source = "inviteToken")
    PatientSummaryDTO toPatientSummary(DoctorPatientLinkEntity entity);
}