package com.vitalsync.sharing.dto;

import jakarta.validation.constraints.NotBlank;

public record AcceptLinkDTO(
        @NotBlank(message = "O token de convite é obrigatório")
        String token
) {}