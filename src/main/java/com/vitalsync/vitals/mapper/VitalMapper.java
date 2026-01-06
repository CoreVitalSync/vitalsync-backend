package com.vitalsync.vitals.mapper;

import com.vitalsync.vitals.VitalSignEntity;
import com.vitalsync.vitals.dto.VitalSignCreateDTO;
import com.vitalsync.vitals.dto.VitalSignResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface VitalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    VitalSignEntity toEntity(VitalSignCreateDTO dto);

    @Mapping(target = "unit", source = "type", qualifiedByName = "getUnit")
    @Mapping(target = "statusMessage", source = ".", qualifiedByName = "calculateStatus")
    VitalSignResponseDTO toResponse(VitalSignEntity entity);

    List<VitalSignResponseDTO> toResponseList(List<VitalSignEntity> entities);

    @Named("getUnit")
    default String getUnit(com.vitalsync.shared.enums.VitalSignType type) {
        return switch (type) {
            case BLOOD_PRESSURE -> "mmHg";
            case GLUCOSE -> "mg/dL";
            case HEART_RATE -> "bpm";
            case WEIGHT -> "kg";
            case TEMPERATURE -> "°C";
        };
    }

    // Regra de negócio simples para o MVP (Status)
    @Named("calculateStatus")
    default String calculateStatus(VitalSignEntity entity) {
        // Exemplo simples para Pressão Arterial
        if (entity.getType() == com.vitalsync.shared.enums.VitalSignType.BLOOD_PRESSURE) {
            if (entity.getValueMajor().doubleValue() > 140 || entity.getValueMinor().doubleValue() > 90) {
                return "Elevada";
            }
            return "Normal";
        }
        return "-"; // Outros tipos implementamos depois
    }
}