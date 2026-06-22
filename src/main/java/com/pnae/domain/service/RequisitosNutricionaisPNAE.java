package com.pnae.domain.service;

import com.pnae.domain.model.FaixaEtaria;

import java.util.Map;

public final class RequisitosNutricionaisPNAE {

    public record RequisitosDiarios(
            double calorias,
            double proteinas,
            double carboidratos,
            double gorduras
    ) {}

    private static final Map<FaixaEtaria, RequisitosDiarios> TABELA = Map.of(
            FaixaEtaria.CRECHE_1,      new RequisitosDiarios(500, 16, 81, 14),
            FaixaEtaria.CRECHE_2,      new RequisitosDiarios(700, 22, 113, 19),
            FaixaEtaria.PRE_ESCOLA,    new RequisitosDiarios(450, 14, 73, 13),
            FaixaEtaria.FUNDAMENTAL_1, new RequisitosDiarios(450, 14, 73, 13),
            FaixaEtaria.FUNDAMENTAL_2, new RequisitosDiarios(500, 16, 81, 14),
            FaixaEtaria.MEDIO,         new RequisitosDiarios(560, 18, 91, 16),
            FaixaEtaria.EJA,           new RequisitosDiarios(560, 18, 91, 16)
    );

    private RequisitosNutricionaisPNAE() {}

    public static RequisitosDiarios getRequisitos(FaixaEtaria faixa) {
        return TABELA.get(faixa);
    }
}
