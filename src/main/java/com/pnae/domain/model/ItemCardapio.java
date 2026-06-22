package com.pnae.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "tb_item_cardapio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCardapio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cardapio_id", nullable = false)
    private Cardapio cardapio;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alimento_id", nullable = false)
    private Alimento alimento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 10)
    private DiaSemana diaSemana;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_refeicao", nullable = false, length = 20)
    private TipoRefeicao tipoRefeicao;

    @NotNull
    @DecimalMin("1.0")
    @Column(name = "quantidade_gramas", nullable = false)
    private Double quantidadeGramas;

    public double getCaloriasPorcao() {
        return (alimento.getCalorias() / 100.0) * quantidadeGramas;
    }

    public double getProteinasPorcao() {
        return (alimento.getProteinas() / 100.0) * quantidadeGramas;
    }

    public double getCarboidratosPorcao() {
        return (alimento.getCarboidratos() / 100.0) * quantidadeGramas;
    }

    public double getGordurasPorcao() {
        return (alimento.getGorduras() / 100.0) * quantidadeGramas;
    }

    public double getFibrasPorcao() {
        return (alimento.getFibras() / 100.0) * quantidadeGramas;
    }

    public double getCalcioPorcao() {
        return (alimento.getCalcio() / 100.0) * quantidadeGramas;
    }

    public double getFerroPorcao() {
        return (alimento.getFerro() / 100.0) * quantidadeGramas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemCardapio item)) return false;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
