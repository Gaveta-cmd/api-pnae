package com.pnae.application.dto;

import com.pnae.domain.model.EstoqueItem;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EstoqueItemResponseDTO(
        Long id,
        Long alimentoId,
        String nomeAlimento,
        Long escolaId,
        String nomeEscola,
        Long fornecedorId,
        String razaoSocialFornecedor,
        Double quantidadeKg,
        LocalDate dataValidade,
        String lote,
        LocalDateTime dataEntrada
) {
    public static EstoqueItemResponseDTO from(EstoqueItem item) {
        return new EstoqueItemResponseDTO(
                item.getId(),
                item.getAlimento().getId(),
                item.getAlimento().getNome(),
                item.getEscola().getId(),
                item.getEscola().getNome(),
                item.getFornecedor() != null ? item.getFornecedor().getId() : null,
                item.getFornecedor() != null ? item.getFornecedor().getRazaoSocial() : null,
                item.getQuantidadeKg(),
                item.getDataValidade(),
                item.getLote(),
                item.getDataEntrada()
        );
    }
}
