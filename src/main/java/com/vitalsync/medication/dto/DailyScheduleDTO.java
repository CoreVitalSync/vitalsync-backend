package com.vitalsync.medication.dto;

import com.vitalsync.shared.enums.LogStatus;
import java.time.LocalTime;
import java.util.UUID;

public record DailyScheduleDTO(
        UUID scheduleId,
        UUID medicationId,
        String medicationName,
        String dosage,
        String instructions,
        LocalTime scheduledTime, // Hora programada (ex: 08:00)
        LogStatus status,        // Status atual (ex: PENDING, TAKEN)
        boolean isTaken          // Booleano auxiliar para o frontend pintar de verde/cinza
) {}