package com.pnae.application.dto;

import com.pnae.domain.model.Fornecedor;

public record FornecedorResponseDTO(
        Long id,
        String razaoSocial,
        String cnpj,
        String email,
        String telefone,
        boolean ativo
) {
    public static FornecedorResponseDTO from(Fornecedor f) {
        return new FornecedorResponseDTO(
                f.getId(),
                f.getRazaoSocial(),
                f.getCnpj(),
                f.getEmail(),
                f.getTelefone(),
                f.isAtivo()
        );
    }
}
