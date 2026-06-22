package com.pnae.application.dto;

import com.pnae.domain.model.DiaSemana;
import com.pnae.domain.model.TipoRefeicao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record ItemCardapioRequestDTO(
        @NotNull(message = "Alimento é obrigatório")
        Long alimentoId,

        @NotNull(message = "Dia da semana é obrigatório")
        DiaSemana diaSemana,

        @NotNull(message = "Tipo de refeição é obrigatório")
        TipoRefeicao tipoRefeicao,

        @NotNull(message = "Quantidade em gramas é obrigatória")
        @DecimalMin(value = "1.0", message = "Quantidade deve ser no mínimo 1 grama")
        Double quantidadeGramas
) {}
