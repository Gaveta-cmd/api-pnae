package com.pnae.application.dto;

import com.pnae.domain.model.Alimento;
import com.pnae.domain.model.CategoriaAlimento;

import java.math.BigDecimal;

public record AlimentoResponseDTO(
        Long id,
        String nome,
        CategoriaAlimento categoria,
        String descricaoCategoria,
        Double calorias,
        Double proteinas,
        Double carboidratos,
        Double gorduras,
        Double fibras,
        Double calcio,
        Double ferro,
        BigDecimal custoPorKg,
        boolean ativo
) {
    public static AlimentoResponseDTO from(Alimento a) {
        return new AlimentoResponseDTO(
                a.getId(),
                a.getNome(),
                a.getCategoria(),
                a.getCategoria().getDescricao(),
                a.getCalorias(),
                a.getProteinas(),
                a.getCarboidratos(),
                a.getGorduras(),
                a.getFibras(),
                a.getCalcio(),
                a.getFerro(),
                a.getCustoPorKg(),
                a.isAtivo()
        );
    }
}
