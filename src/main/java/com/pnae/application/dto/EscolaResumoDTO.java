package com.pnae.application.dto;

import com.pnae.domain.model.Escola;

public record EscolaResumoDTO(Long id, String nome) {
    public static EscolaResumoDTO from(Escola escola) {
        return new EscolaResumoDTO(escola.getId(), escola.getNome());
    }
}
