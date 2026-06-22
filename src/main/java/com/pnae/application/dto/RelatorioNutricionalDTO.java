package com.pnae.application.dto;

import com.pnae.domain.model.DiaSemana;

import java.util.List;
import java.util.Map;

public record RelatorioNutricionalDTO(
        CardapioResponseDTO cardapio,
        Map<DiaSemana, ResumoNutricionalDTO> detalhamentoPorDia,
        ResumoNutricionalDTO mediasSemana,
        List<MediaNutricionalDiaDTO> mediasNutricionaisPorDia
) {
    public record MediaNutricionalDiaDTO(
            String diaSemana,
            double mediaCalorias,
            double mediaProteinas,
            double mediaCarboidratos,
            double mediaGorduras
    ) {}
}
