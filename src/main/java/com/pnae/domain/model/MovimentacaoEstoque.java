package com.pnae.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tb_movimentacao_estoque")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estoque_item_id", nullable = false)
    private EstoqueItem estoqueItem;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoMovimentacao tipo;

    @NotNull
    @DecimalMin("0.01")
    @Column(name = "quantidade_kg", nullable = false)
    private Double quantidadeKg;

    @Size(max = 500)
    @Column(name = "motivo", length = 500)
    private String motivo;

    @Column(name = "data_movimentacao", nullable = false, updatable = false)
    private LocalDateTime dataMovimentacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private Usuario responsavel;

    @PrePersist
    private void prePersist() {
        this.dataMovimentacao = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MovimentacaoEstoque m)) return false;
        return Objects.equals(id, m.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
