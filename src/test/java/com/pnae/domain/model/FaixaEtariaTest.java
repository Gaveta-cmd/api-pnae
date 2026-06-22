package com.pnae.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static com.pnae.domain.model.FaixaEtaria.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FaixaEtariaTest {

    @Test
    void calcularPorIdade_bebe6Meses_deveRetornarCreche1() {
        assertEquals(CRECHE_1, FaixaEtaria.calcularPorIdade(LocalDate.now().minusMonths(6)));
    }

    @Test
    void calcularPorIdade_nascidoHoje_deveRetornarCreche1() {
        assertEquals(CRECHE_1, FaixaEtaria.calcularPorIdade(LocalDate.now()));
    }

    @Test
    void calcularPorIdade_1Ano_deveRetornarCreche1() {
        // 1 ano cai em CRECHE_1 (0-1) por ordem de verificação no enum
        assertEquals(CRECHE_1, FaixaEtaria.calcularPorIdade(LocalDate.now().minusYears(1)));
    }

    @Test
    void calcularPorIdade_2Anos_deveRetornarCreche2() {
        assertEquals(CRECHE_2, FaixaEtaria.calcularPorIdade(LocalDate.now().minusYears(2)));
    }

    @Test
    void calcularPorIdade_3Anos_deveRetornarCreche2() {
        assertEquals(CRECHE_2, FaixaEtaria.calcularPorIdade(LocalDate.now().minusYears(3)));
    }

    @Test
    void calcularPorIdade_4Anos_deveRetornarPreEscola() {
        assertEquals(PRE_ESCOLA, FaixaEtaria.calcularPorIdade(LocalDate.now().minusYears(4)));
    }

    @Test
    void calcularPorIdade_5Anos_deveRetornarPreEscola() {
        assertEquals(PRE_ESCOLA, FaixaEtaria.calcularPorIdade(LocalDate.now().minusYears(5)));
    }

    @Test
    void calcularPorIdade_6Anos_deveRetornarFundamental1() {
        assertEquals(FUNDAMENTAL_1, FaixaEtaria.calcularPorIdade(LocalDate.now().minusYears(6)));
    }

    @Test
    void calcularPorIdade_10Anos_deveRetornarFundamental1() {
        assertEquals(FUNDAMENTAL_1, FaixaEtaria.calcularPorIdade(LocalDate.now().minusYears(10)));
    }

    @Test
    void calcularPorIdade_11Anos_deveRetornarFundamental2() {
        assertEquals(FUNDAMENTAL_2, FaixaEtaria.calcularPorIdade(LocalDate.now().minusYears(11)));
    }

    @Test
    void calcularPorIdade_14Anos_deveRetornarFundamental2() {
        assertEquals(FUNDAMENTAL_2, FaixaEtaria.calcularPorIdade(LocalDate.now().minusYears(14)));
    }

    @Test
    void calcularPorIdade_15Anos_deveRetornarMedio() {
        assertEquals(MEDIO, FaixaEtaria.calcularPorIdade(LocalDate.now().minusYears(15)));
    }

    @Test
    void calcularPorIdade_17Anos_deveRetornarMedio() {
        assertEquals(MEDIO, FaixaEtaria.calcularPorIdade(LocalDate.now().minusYears(17)));
    }

    @Test
    void calcularPorIdade_18Anos_deveRetornarEja() {
        assertEquals(EJA, FaixaEtaria.calcularPorIdade(LocalDate.now().minusYears(18)));
    }

    @Test
    void calcularPorIdade_adulto_deveRetornarEja() {
        assertEquals(EJA, FaixaEtaria.calcularPorIdade(LocalDate.now().minusYears(35)));
    }

    @ParameterizedTest
    @CsvSource({
        "CRECHE_1,     Creche I (0-1 ano)",
        "CRECHE_2,     Creche II (1-3 anos)",
        "PRE_ESCOLA,   Pré-Escola (4-5 anos)",
        "FUNDAMENTAL_1, Ensino Fundamental I (6-10 anos)",
        "FUNDAMENTAL_2, Ensino Fundamental II (11-14 anos)",
        "MEDIO,        Ensino Médio (15-17 anos)",
        "EJA,          EJA (18+ anos)"
    })
    void getDescricao_deveRetornarDescricaoCorreta(String nomeFaixa, String descricaoEsperada) {
        FaixaEtaria faixa = FaixaEtaria.valueOf(nomeFaixa.trim());
        assertEquals(descricaoEsperada.trim(), faixa.getDescricao());
    }
}
