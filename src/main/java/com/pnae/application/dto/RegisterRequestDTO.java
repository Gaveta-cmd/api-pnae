package com.pnae.application.dto;

import com.pnae.domain.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDTO(
        @NotBlank String nome,
        @Email @NotBlank String email,
        @NotBlank String senha,
        @NotNull Role role
) {}
