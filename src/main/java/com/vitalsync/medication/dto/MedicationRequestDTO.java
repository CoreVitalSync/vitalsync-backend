package com.vitalsync.medication.dto;

import com.vitalsync.shared.enums.MedicationFrequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record MedicationRequestDTO(
        @NotBlank(message = "O nome do medicamento é obrigatório")
        String name,

        @NotBlank(message = "A dosagem é obrigatória")
        String dosage, // Ex: "50mg" ou "1 comprimido"

        String instructions, // Opcional: "Tomar após o almoço"

        @NotNull(message = "O tipo de frequência é obrigatório")
        MedicationFrequency frequencyType,

        LocalDate startDate, // Opcional (se null, o Service assume "hoje")

        @NotEmpty(message = "Informe pelo menos um horário de tomada")
        List<LocalTime> schedules // Ex: ["08:00", "20:00"]
) {}
