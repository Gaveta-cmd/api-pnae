package com.pnae.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EstoqueEntradaDTO(
        @NotNull Long alimentoId,
        @NotNull Long escolaId,
        Long fornecedorId,
        @NotNull @DecimalMin("0.01") Double quantidadeKg,
        @FutureOrPresent LocalDate dataValidade,
        String lote
) {}
