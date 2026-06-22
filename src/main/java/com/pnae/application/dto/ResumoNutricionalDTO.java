package com.pnae.application.dto;

import com.pnae.domain.model.DiaSemana;
import com.pnae.domain.model.ItemCardapio;

import java.util.List;

public record ResumoNutricionalDTO(
        DiaSemana diaSemana,
        double totalCalorias,
        double totalProteinas,
        double totalCarboidratos,
        double totalGorduras,
        double totalFibras,
        double totalCalcio,
        double totalFerro
) {
    public static ResumoNutricionalDTO from(DiaSemana dia, List<ItemCardapio> itens) {
        return new ResumoNutricionalDTO(
                dia,
                round(itens.stream().mapToDouble(ItemCardapio::getCaloriasPorcao).sum()),
                round(itens.stream().mapToDouble(ItemCardapio::getProteinasPorcao).sum()),
                round(itens.stream().mapToDouble(ItemCardapio::getCarboidratosPorcao).sum()),
                round(itens.stream().mapToDouble(ItemCardapio::getGordurasPorcao).sum()),
                round(itens.stream().mapToDouble(ItemCardapio::getFibrasPorcao).sum()),
                round(itens.stream().mapToDouble(ItemCardapio::getCalcioPorcao).sum()),
                round(itens.stream().mapToDouble(ItemCardapio::getFerroPorcao).sum())
        );
    }

    public static ResumoNutricionalDTO total(List<ItemCardapio> itens) {
        return from(null, itens);
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
