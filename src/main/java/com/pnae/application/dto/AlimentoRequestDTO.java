package com.pnae.application.dto;

import com.pnae.domain.model.CategoriaAlimento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AlimentoRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
        String nome,

        @NotNull(message = "Categoria é obrigatória")
        CategoriaAlimento categoria,

        @NotNull(message = "Calorias são obrigatórias")
        @DecimalMin(value = "0.0", message = "Calorias não podem ser negativas")
        Double calorias,

        @NotNull(message = "Proteínas são obrigatórias")
        @DecimalMin(value = "0.0", message = "Proteínas não podem ser negativas")
        Double proteinas,

        @NotNull(message = "Carboidratos são obrigatórios")
        @DecimalMin(value = "0.0", message = "Carboidratos não podem ser negativos")
        Double carboidratos,

        @NotNull(message = "Gorduras são obrigatórias")
        @DecimalMin(value = "0.0", message = "Gorduras não podem ser negativas")
        Double gorduras,

        @NotNull(message = "Fibras são obrigatórias")
        @DecimalMin(value = "0.0", message = "Fibras não podem ser negativas")
        Double fibras,

        @NotNull(message = "Cálcio é obrigatório")
        @DecimalMin(value = "0.0", message = "Cálcio não pode ser negativo")
        Double calcio,

        @NotNull(message = "Ferro é obrigatório")
        @DecimalMin(value = "0.0", message = "Ferro não pode ser negativo")
        Double ferro,

        @NotNull(message = "Custo por kg é obrigatório")
        @DecimalMin(value = "0.01", message = "Custo por kg deve ser maior que zero")
        BigDecimal custoPorKg
) {}
