package com.pnae.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record FornecedorRequestDTO(
        @NotBlank String razaoSocial,
        @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos numéricos") String cnpj,
        @Email String email,
        String telefone,
        @Valid EnderecoDTO endereco
) {}
