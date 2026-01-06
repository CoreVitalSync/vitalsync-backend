package com.vitalsync.sharing.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PatientSummaryDTO(
        UUID id,
        String fullName,
        String email,
        LocalDate birthDate,
        String inviteToken // Útil se o médico precisar reenviar
) {}