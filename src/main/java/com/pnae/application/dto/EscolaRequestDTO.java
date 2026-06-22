package com.pnae.application.dto;

import com.pnae.domain.model.TipoEscola;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EscolaRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
        String nome,

        @NotNull(message = "Endereço é obrigatório")
        @Valid
        EnderecoDTO endereco,

        @NotNull(message = "Tipo é obrigatório")
        TipoEscola tipo,

        @NotNull(message = "Capacidade de alunos é obrigatória")
        @Min(value = 1, message = "Capacidade deve ser no mínimo 1")
        Integer capacidadeAlunos
) {}
