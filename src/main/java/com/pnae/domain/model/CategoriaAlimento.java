package com.pnae.domain.model;

public enum CategoriaAlimento {
    CEREAL("Cereais e Derivados"),
    LEGUMINOSA("Leguminosas"),
    FRUTA("Frutas"),
    HORTALICA("Hortaliças"),
    LEITE_DERIVADO("Leite e Derivados"),
    CARNE("Carnes e Aves"),
    OVOS("Ovos"),
    GORDURA("Óleos e Gorduras"),
    ACUCAR("Açúcares e Doces"),
    OUTROS("Outros");

    private final String descricao;

    CategoriaAlimento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() { return descricao; }
}
