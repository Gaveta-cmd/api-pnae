package com.pnae.application.dto;

import java.math.BigDecimal;

public record RelatorioCustoAlunoDTO(
        EscolaResumoDTO escola,
        int mes,
        int ano,
        long totalAlunos,
        BigDecimal custoTotal,
        BigDecimal custoPorAluno
) {}
