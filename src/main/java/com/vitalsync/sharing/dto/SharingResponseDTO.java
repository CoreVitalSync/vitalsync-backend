package com.vitalsync.sharing.dto;

import com.vitalsync.shared.enums.LinkStatus;
import java.time.LocalDateTime;

public record SharingResponseDTO(
        String inviteToken,  // O código para copiar e colar
        LinkStatus status,   // PENDING, ACTIVE
        LocalDateTime createdAt,
        String doctorName    // Nome do médico (se já estiver vinculado)
) {}