package com.pnae.application.dto;

import com.pnae.domain.model.MovimentacaoEstoque;
import com.pnae.domain.model.TipoMovimentacao;

import java.time.LocalDateTime;

public record MovimentacaoResponseDTO(
        Long id,
        Long estoqueItemId,
        String nomeAlimento,
        TipoMovimentacao tipo,
        Double quantidadeKg,
        String motivo,
        LocalDateTime dataMovimentacao,
        String responsavel
) {
    public static MovimentacaoResponseDTO from(MovimentacaoEstoque m) {
        return new MovimentacaoResponseDTO(
                m.getId(),
                m.getEstoqueItem().getId(),
                m.getEstoqueItem().getAlimento().getNome(),
                m.getTipo(),
                m.getQuantidadeKg(),
                m.getMotivo(),
                m.getDataMovimentacao(),
                m.getResponsavel() != null ? m.getResponsavel().getNome() : null
        );
    }
}
