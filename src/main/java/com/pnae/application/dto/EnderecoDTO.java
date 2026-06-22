package com.pnae.application.dto;

import com.pnae.domain.model.UF;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record EnderecoDTO(
        @NotBlank(message = "Logradouro é obrigatório")
        String logradouro,

        String numero,

        String complemento,

        String bairro,

        @NotBlank(message = "Cidade é obrigatória")
        String cidade,

        @NotNull(message = "Estado é obrigatório")
        UF estado,

        @NotBlank(message = "CEP é obrigatório")
        @Pattern(regexp = "\\d{5}-\\d{3}", message = "CEP deve estar no formato 00000-000")
        String cep
) {}
