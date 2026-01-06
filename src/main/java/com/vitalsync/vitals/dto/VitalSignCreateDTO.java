package com.vitalsync.vitals.dto;

import com.vitalsync.shared.enums.VitalSignType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VitalSignCreateDTO(
        @NotNull(message = "O tipo de sinal vital é obrigatório")
        VitalSignType type, // BLOOD_PRESSURE, GLUCOSE...

        @NotNull(message = "O valor principal é obrigatório")
        BigDecimal valueMajor, // Ex: 120 (Sistólica) ou 90 (Glicemia)

        BigDecimal valueMinor, // Ex: 80 (Diastólica). Opcional para outros tipos.

        @PastOrPresent(message = "A data da medição não pode ser futura")
        LocalDateTime measuredAt, // Se null, assumimos "agora"

        String notes // "Estava me sentindo tonto"
) {}