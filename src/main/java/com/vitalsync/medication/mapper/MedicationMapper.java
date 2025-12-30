package com.vitalsync.medication.mapper;

import com.vitalsync.medication.MedicationEntity;
import com.vitalsync.medication.MedicationScheduleEntity;
import com.vitalsync.medication.dto.MedicationRequestDTO;
import com.vitalsync.medication.dto.MedicationResponseDTO;
import org.mapstruct.*;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface MedicationMapper {

    // --- Create: DTO -> Entity ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true) 
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "schedules", source = "schedules", qualifiedByName = "mapSchedules")
    MedicationEntity toEntity(MedicationRequestDTO dto);

    // Método auxiliar que transforma LocalTime -> Entity
    @Named("mapSchedules")
    default List<MedicationScheduleEntity> mapSchedules(List<LocalTime> times) {
        if (times == null) return Collections.emptyList();

        return times.stream()
                .map(time -> MedicationScheduleEntity.builder()
                        .scheduledTime(time)
                        .build())
                .toList();
    }

    // --- Response: Entity -> DTO ---
    @Mapping(target = "schedules", source = "schedules", qualifiedByName = "extractTimes")
    MedicationResponseDTO toResponse(MedicationEntity entity);

    // Método auxiliar que transforma Entity -> LocalTime (para o JSON ficar limpo)
    @Named("extractTimes")
    default List<LocalTime> extractTimes(List<MedicationScheduleEntity> schedules) {
        if (schedules == null) return Collections.emptyList();
        return schedules.stream()
                .map(MedicationScheduleEntity::getScheduledTime)
                .toList();
    }

    List<MedicationResponseDTO> toResponseList(List<MedicationEntity> entities);

    // Método para Atualizar (PUT)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    void updateEntityFromDto(MedicationRequestDTO dto, @MappingTarget MedicationEntity entity);
}