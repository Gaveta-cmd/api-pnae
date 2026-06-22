package com.pnae.application.dto;

import com.pnae.domain.model.TipoEscola;

import java.util.List;
import java.util.Map;

public record RelatorioEscolasDTO(
        long totalEscolas,
        long totalAlunos,
        Map<TipoEscola, Long> escolasPorTipo,
        List<EscolaResumoAlunosDTO> listaEscolas
) {
    public record EscolaResumoAlunosDTO(Long id, String nome, TipoEscola tipo, long totalAlunos) {}
}
