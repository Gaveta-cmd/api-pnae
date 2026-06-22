package com.pnae.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "tb_alimento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false, length = 30)
    private CategoriaAlimento categoria;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "calorias", nullable = false)
    private Double calorias;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "proteinas", nullable = false)
    private Double proteinas;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "carboidratos", nullable = false)
    private Double carboidratos;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "gorduras", nullable = false)
    private Double gorduras;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "fibras", nullable = false)
    private Double fibras;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "calcio", nullable = false)
    private Double calcio;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "ferro", nullable = false)
    private Double ferro;

    @NotNull
    @DecimalMin("0.01")
    @Column(name = "custo_por_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal custoPorKg;

    @Builder.Default
    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Alimento alimento)) return false;
        return Objects.equals(id, alimento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
