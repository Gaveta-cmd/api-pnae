package com.pnae.domain.service;

import com.pnae.application.dto.ValidacaoNutricionalDTO;
import com.pnae.domain.exception.ResourceNotFoundException;
import com.pnae.domain.model.Aluno;
import com.pnae.domain.model.Cardapio;
import com.pnae.domain.model.DiaSemana;
import com.pnae.domain.model.FaixaEtaria;
import com.pnae.domain.model.ItemCardapio;
import com.pnae.domain.repository.AlunoRepository;
import com.pnae.domain.repository.CardapioRepository;
import com.pnae.domain.service.RequisitosNutricionaisPNAE.RequisitosDiarios;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValidacaoNutricionalService {

    private final CardapioRepository cardapioRepository;
    private final AlunoRepository alunoRepository;

    @Transactional(readOnly = true)
    public List<ValidacaoNutricionalDTO> validarCardapioCompleto(Long cardapioId, FaixaEtaria faixa) {
        Cardapio cardapio = buscarCardapio(cardapioId);
        return Arrays.stream(DiaSemana.values())
                .map(dia -> validarDia(cardapio, dia, faixa))
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<FaixaEtaria, List<ValidacaoNutricionalDTO>> validarCardapioParaEscola(Long cardapioId) {
        Cardapio cardapio = buscarCardapio(cardapioId);
        Long escolaId = cardapio.getEscola().getId();

        Set<FaixaEtaria> faixasPresentes = alunoRepository.findByEscolaId(escolaId).stream()
                .filter(Aluno::isAtivo)
                .map(Aluno::getFaixaEtaria)
                .collect(Collectors.toSet());

        if (faixasPresentes.isEmpty()) {
            faixasPresentes = EnumSet.allOf(FaixaEtaria.class);
        }

        return faixasPresentes.stream()
                .collect(Collectors.toMap(
                        faixa -> faixa,
                        faixa -> Arrays.stream(DiaSemana.values())
                                .map(dia -> validarDia(cardapio, dia, faixa))
                                .toList()
                ));
    }

    private ValidacaoNutricionalDTO validarDia(Cardapio cardapio, DiaSemana dia, FaixaEtaria faixa) {
        List<ItemCardapio> itensDia = cardapio.getItens().stream()
                .filter(i -> i.getDiaSemana() == dia)
                .toList();

        double calorias = round(itensDia.stream().mapToDouble(ItemCardapio::getCaloriasPorcao).sum());
        double proteinas = round(itensDia.stream().mapToDouble(ItemCardapio::getProteinasPorcao).sum());
        double carboidratos = round(itensDia.stream().mapToDouble(ItemCardapio::getCarboidratosPorcao).sum());
        double gorduras = round(itensDia.stream().mapToDouble(ItemCardapio::getGordurasPorcao).sum());

        RequisitosDiarios req = RequisitosNutricionaisPNAE.getRequisitos(faixa);

        double pctCal  = calcularPercentual(calorias, req.calorias());
        double pctProt = calcularPercentual(proteinas, req.proteinas());
        double pctCarb = calcularPercentual(carboidratos, req.carboidratos());
        double pctGord = calcularPercentual(gorduras, req.gorduras());

        List<String> alertas = new ArrayList<>();
        if (pctCal < 100) alertas.add(String.format(
                "Calorias abaixo do mínimo: %.0f kcal de %.0f kcal necessárias (%.0f%%)",
                calorias, req.calorias(), pctCal));
        if (pctProt < 100) alertas.add(String.format(
                "Proteínas abaixo do mínimo: %.1fg de %.1fg necessárias (%.0f%%)",
                proteinas, req.proteinas(), pctProt));
        if (pctCarb < 100) alertas.add(String.format(
                "Carboidratos abaixo do mínimo: %.1fg de %.1fg necessários (%.0f%%)",
                carboidratos, req.carboidratos(), pctCarb));
        if (pctGord < 100) alertas.add(String.format(
                "Gorduras abaixo do mínimo: %.1fg de %.1fg necessárias (%.0f%%)",
                gorduras, req.gorduras(), pctGord));

        return new ValidacaoNutricionalDTO(
                faixa, dia,
                req.calorias(), calorias, round(pctCal),
                req.proteinas(), proteinas, round(pctProt),
                req.carboidratos(), carboidratos, round(pctCarb),
                req.gorduras(), gorduras, round(pctGord),
                alertas.isEmpty(),
                alertas
        );
    }

    private double calcularPercentual(double obtido, double necessario) {
        if (necessario == 0) return 100.0;
        return (obtido / necessario) * 100.0;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private Cardapio buscarCardapio(Long id) {
        return cardapioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cardápio", id));
    }
}
