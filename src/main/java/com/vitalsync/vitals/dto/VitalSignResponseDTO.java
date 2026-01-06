package com.vitalsync.vitals.dto;

import com.vitalsync.shared.enums.VitalSignType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record VitalSignResponseDTO(
        UUID id,
        VitalSignType type,
        BigDecimal valueMajor,
        BigDecimal valueMinor,
        LocalDateTime measuredAt,
        String notes,
        String statusMessage, // Ex: "Normal", "Alterado" (Lógica calculada no backend)
        String unit // Ex: "mmHg", "mg/dL" (Para facilitar pro front)
) {}