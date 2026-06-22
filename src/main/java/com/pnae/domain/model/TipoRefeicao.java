package com.pnae.domain.model;

public enum TipoRefeicao {
    DESJEJUM("Desjejum"),
    COLACAO("Colação"),
    ALMOCO("Almoço"),
    LANCHE_TARDE("Lanche da Tarde"),
    JANTAR("Jantar");

    private final String descricao;

    TipoRefeicao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() { return descricao; }
}
