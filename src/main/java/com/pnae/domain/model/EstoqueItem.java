package com.pnae.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tb_estoque",
        uniqueConstraints = @UniqueConstraint(name = "uk_estoque_alimento_escola_lote",
                columnNames = {"alimento_id", "escola_id", "lote"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstoqueItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alimento_id", nullable = false)
    private Alimento alimento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escola_id", nullable = false)
    private Escola escola;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @DecimalMin("0.0")
    @Column(name = "quantidade_kg", nullable = false)
    private Double quantidadeKg;

    @FutureOrPresent
    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @Builder.Default
    @Column(name = "lote", nullable = false, length = 50)
    private String lote = "SEM_LOTE";

    @Column(name = "data_entrada", nullable = false, updatable = false)
    private LocalDateTime dataEntrada;

    @PrePersist
    private void prePersist() {
        this.dataEntrada = LocalDateTime.now();
        if (this.lote == null || this.lote.isBlank()) {
            this.lote = "SEM_LOTE";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EstoqueItem e)) return false;
        return Objects.equals(id, e.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
