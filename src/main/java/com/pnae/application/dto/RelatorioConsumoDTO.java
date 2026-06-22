package com.pnae.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record RelatorioConsumoDTO(
        EscolaResumoDTO escola,
        int mes,
        int ano,
        List<ItemConsumoDTO> alimentosConsumidos,
        BigDecimal custoTotal
) {
    public record ItemConsumoDTO(String alimento, double quantidadeKg, BigDecimal custo) {}
}
