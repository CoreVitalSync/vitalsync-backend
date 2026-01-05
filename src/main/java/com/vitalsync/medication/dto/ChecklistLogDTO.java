package com.vitalsync.medication.dto;

import com.vitalsync.shared.enums.LogStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;

public record ChecklistLogDTO(
        @NotNull(message = "A data/hora da tomada é obrigatória")
        @PastOrPresent(message = "A data não pode ser futura")
        LocalDateTime takenAt,

        @NotNull(message = "O status é obrigatório")
        LogStatus status // TAKEN, SKIPPED
) {}