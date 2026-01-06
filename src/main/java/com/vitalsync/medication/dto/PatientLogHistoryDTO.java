package com.vitalsync.medication.dto;

import com.vitalsync.shared.enums.LogStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record PatientLogHistoryDTO(
    UUID id,
    String medicationName,
    String dosage,
    LocalDateTime scheduledTime,
    LocalDateTime takenAt,
    LogStatus status
) {}