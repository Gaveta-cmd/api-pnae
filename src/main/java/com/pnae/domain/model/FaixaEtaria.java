package com.pnae.domain.model;

import java.time.LocalDate;
import java.time.Period;

public enum FaixaEtaria {
    CRECHE_1("Creche I (0-1 ano)", 0, 1),
    CRECHE_2("Creche II (1-3 anos)", 1, 3),
    PRE_ESCOLA("Pré-Escola (4-5 anos)", 4, 5),
    FUNDAMENTAL_1("Ensino Fundamental I (6-10 anos)", 6, 10),
    FUNDAMENTAL_2("Ensino Fundamental II (11-14 anos)", 11, 14),
    MEDIO("Ensino Médio (15-17 anos)", 15, 17),
    EJA("EJA (18+ anos)", 18, Integer.MAX_VALUE);

    private final String descricao;
    private final int idadeMinima;
    private final int idadeMaxima;

    FaixaEtaria(String descricao, int idadeMinima, int idadeMaxima) {
        this.descricao = descricao;
        this.idadeMinima = idadeMinima;
        this.idadeMaxima = idadeMaxima;
    }

    public String getDescricao() { return descricao; }
    public int getIdadeMinima() { return idadeMinima; }
    public int getIdadeMaxima() { return idadeMaxima; }

    public static FaixaEtaria calcularPorIdade(LocalDate dataNascimento) {
        int idade = Period.between(dataNascimento, LocalDate.now()).getYears();
        for (FaixaEtaria faixa : values()) {
            if (idade >= faixa.idadeMinima && idade <= faixa.idadeMaxima) {
                return faixa;
            }
        }
        return EJA;
    }
}
