package com.vitalsync.medication.dto;

import com.vitalsync.shared.enums.MedicationFrequency;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record MedicationResponseDTO(
        UUID id,
        String name,
        String dosage,
        String instructions,
        MedicationFrequency frequencyType,
        LocalDate startDate,
        boolean active,
        List<LocalTime> schedules
) {}
