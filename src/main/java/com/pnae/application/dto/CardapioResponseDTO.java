package com.pnae.application.dto;

import com.pnae.domain.model.Cardapio;
import com.pnae.domain.model.DiaSemana;
import com.pnae.domain.model.StatusCardapio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record CardapioResponseDTO(
        Long id,
        EscolaResumoDTO escola,
        String nome,
        Integer semana,
        Integer ano,
        StatusCardapio status,
        String observacoes,
        LocalDateTime dataCriacao,
        Map<DiaSemana, List<ItemCardapioResponseDTO>> itensPorDia
) {
    public static CardapioResponseDTO from(Cardapio cardapio) {
        Map<DiaSemana, List<ItemCardapioResponseDTO>> itensPorDia = cardapio.getItens().stream()
                .map(ItemCardapioResponseDTO::from)
                .collect(Collectors.groupingBy(ItemCardapioResponseDTO::diaSemana));

        return new CardapioResponseDTO(
                cardapio.getId(),
                EscolaResumoDTO.from(cardapio.getEscola()),
                cardapio.getNome(),
                cardapio.getSemana(),
                cardapio.getAno(),
                cardapio.getStatus(),
                cardapio.getObservacoes(),
                cardapio.getDataCriacao(),
                itensPorDia
        );
    }
}
