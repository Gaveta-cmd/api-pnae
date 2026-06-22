package com.pnae.application.dto;

import com.pnae.domain.model.DiaSemana;
import com.pnae.domain.model.FaixaEtaria;

import java.util.List;

public record ValidacaoNutricionalDTO(
        FaixaEtaria faixaEtaria,
        DiaSemana diaSemana,
        double caloriasNecessarias,
        double caloriasCardapio,
        double percentualCalorias,
        double proteinasNecessarias,
        double proteinasCardapio,
        double percentualProteinas,
        double carboidratosNecessarios,
        double carboidratosCardapio,
        double percentualCarboidratos,
        double gordurasNecessarias,
        double gordurasCardapio,
        double percentualGorduras,
        boolean aprovado,
        List<String> alertas
) {}
