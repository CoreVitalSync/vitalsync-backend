package com.vitalsync.user.dto;

import com.vitalsync.shared.enums.Role;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserRegistrationDTO(
        @NotBlank(message = "O nome completo é obrigatório")
        String fullName,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String password,

        @NotNull(message = "A data de nascimento é obrigatória")
        @Past(message = "Data de nascimento deve ser no passado")
        LocalDate birthDate,

        @NotNull(message = "O perfil (Role) é obrigatório")
        Role role
) {}