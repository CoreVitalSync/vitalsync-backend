package com.vitalsync.user.dto;

import com.vitalsync.shared.enums.Role;
import java.time.LocalDate;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String fullName,
        String email,
        Role role,
        LocalDate birthDate
) {}