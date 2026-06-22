package com.pnae.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record EstoqueMovimentacaoDTO(
        @NotNull @DecimalMin("0.01") Double quantidadeKg,
        String motivo
) {}
