package com.pnae.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CardapioRequestDTO(
        @NotNull(message = "Escola é obrigatória")
        Long escolaId,

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
        String nome,

        @NotNull(message = "Semana é obrigatória")
        @Min(value = 1, message = "Semana deve ser entre 1 e 53")
        @Max(value = 53, message = "Semana deve ser entre 1 e 53")
        Integer semana,

        @NotNull(message = "Ano é obrigatório")
        Integer ano,

        @Size(max = 1000, message = "Observações devem ter no máximo 1000 caracteres")
        String observacoes
) {}
