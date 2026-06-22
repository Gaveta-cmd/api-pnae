package com.pnae.application.dto;

import com.pnae.domain.model.DiaSemana;
import com.pnae.domain.model.ItemCardapio;
import com.pnae.domain.model.TipoRefeicao;

public record ItemCardapioResponseDTO(
        Long id,
        Long alimentoId,
        String nomeAlimento,
        String categoriaAlimento,
        DiaSemana diaSemana,
        String descricaoDia,
        TipoRefeicao tipoRefeicao,
        String descricaoRefeicao,
        Double quantidadeGramas,
        Double calorias,
        Double proteinas,
        Double carboidratos,
        Double gorduras,
        Double fibras,
        Double calcio,
        Double ferro
) {
    public static ItemCardapioResponseDTO from(ItemCardapio item) {
        return new ItemCardapioResponseDTO(
                item.getId(),
                item.getAlimento().getId(),
                item.getAlimento().getNome(),
                item.getAlimento().getCategoria().getDescricao(),
                item.getDiaSemana(),
                item.getDiaSemana().getDescricao(),
                item.getTipoRefeicao(),
                item.getTipoRefeicao().getDescricao(),
                item.getQuantidadeGramas(),
                round(item.getCaloriasPorcao()),
                round(item.getProteinasPorcao()),
                round(item.getCarboidratosPorcao()),
                round(item.getGordurasPorcao()),
                round(item.getFibrasPorcao()),
                round(item.getCalcioPorcao()),
                round(item.getFerroPorcao())
        );
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
