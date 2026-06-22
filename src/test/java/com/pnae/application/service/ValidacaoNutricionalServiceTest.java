package com.pnae.application.service;

import com.pnae.application.dto.ValidacaoNutricionalDTO;
import com.pnae.domain.exception.ResourceNotFoundException;
import com.pnae.domain.model.Alimento;
import com.pnae.domain.model.CategoriaAlimento;
import com.pnae.domain.model.Cardapio;
import com.pnae.domain.model.DiaSemana;
import com.pnae.domain.model.Endereco;
import com.pnae.domain.model.Escola;
import com.pnae.domain.model.FaixaEtaria;
import com.pnae.domain.model.ItemCardapio;
import com.pnae.domain.model.StatusCardapio;
import com.pnae.domain.model.TipoEscola;
import com.pnae.domain.model.TipoRefeicao;
import com.pnae.domain.model.UF;
import com.pnae.domain.repository.AlunoRepository;
import com.pnae.domain.repository.CardapioRepository;
import com.pnae.domain.service.ValidacaoNutricionalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidacaoNutricionalServiceTest {

    @Mock private CardapioRepository cardapioRepository;
    @Mock private AlunoRepository alunoRepository;

    @InjectMocks
    private ValidacaoNutricionalService validacaoNutricionalService;

    // FUNDAMENTAL_1: calorias=450, proteinas=14, carb=73, gorduras=13 por dia
    // Alimento rico: 200 kcal/100g, 10g prot, 35g carb, 7g gord por 100g
    // Porção de 300g: calorias=600, proteinas=30, carb=105, gorduras=21 → acima do requisito
    private Alimento buildAlimentoRico() {
        Alimento a = new Alimento();
        a.setId(1L); a.setNome("Alimento Teste"); a.setCategoria(CategoriaAlimento.CEREAL);
        a.setCalorias(200.0); a.setProteinas(10.0); a.setCarboidratos(35.0);
        a.setGorduras(7.0); a.setFibras(2.0); a.setCalcio(50.0); a.setFerro(2.0);
        a.setCustoPorKg(new BigDecimal("5.00")); a.setAtivo(true);
        return a;
    }

    // Alimento pobre: 50 kcal/100g, 2g prot, 10g carb, 1g gord por 100g
    // Porção de 100g: calorias=50, proteinas=2, carb=10, gorduras=1 → bem abaixo do requisito
    private Alimento buildAlimentoPobre() {
        Alimento a = new Alimento();
        a.setId(2L); a.setNome("Alimento Fraco"); a.setCategoria(CategoriaAlimento.HORTALICA);
        a.setCalorias(50.0); a.setProteinas(2.0); a.setCarboidratos(10.0);
        a.setGorduras(1.0); a.setFibras(1.0); a.setCalcio(10.0); a.setFerro(0.5);
        a.setCustoPorKg(new BigDecimal("2.00")); a.setAtivo(true);
        return a;
    }

    private Cardapio buildCardapioComItensRicos() {
        Escola escola = Escola.builder()
                .id(1L).nome("EMEF Teste").tipo(TipoEscola.MUNICIPAL)
                .capacidadeAlunos(100).ativa(true)
                .endereco(Endereco.builder()
                        .logradouro("Rua A").cidade("SP").estado(UF.SP).cep("01310-100").build())
                .build();

        Alimento alimento = buildAlimentoRico();
        List<ItemCardapio> itens = new ArrayList<>();

        // Adiciona 300g para cada dia da semana
        Arrays.stream(DiaSemana.values()).forEach(dia ->
            itens.add(ItemCardapio.builder()
                    .alimento(alimento).diaSemana(dia)
                    .tipoRefeicao(TipoRefeicao.ALMOCO).quantidadeGramas(300.0)
                    .build())
        );

        return Cardapio.builder()
                .id(1L).escola(escola).nome("Cardápio Semanal")
                .semana(1).ano(2024).status(StatusCardapio.RASCUNHO)
                .itens(itens).build();
    }

    private Cardapio buildCardapioComItensPobres() {
        Escola escola = Escola.builder()
                .id(1L).nome("EMEF Teste").tipo(TipoEscola.MUNICIPAL)
                .capacidadeAlunos(100).ativa(true)
                .endereco(Endereco.builder()
                        .logradouro("Rua A").cidade("SP").estado(UF.SP).cep("01310-100").build())
                .build();

        Alimento alimento = buildAlimentoPobre();
        List<ItemCardapio> itens = new ArrayList<>();

        Arrays.stream(DiaSemana.values()).forEach(dia ->
            itens.add(ItemCardapio.builder()
                    .alimento(alimento).diaSemana(dia)
                    .tipoRefeicao(TipoRefeicao.ALMOCO).quantidadeGramas(100.0)
                    .build())
        );

        return Cardapio.builder()
                .id(1L).escola(escola).nome("Cardápio Fraco")
                .semana(1).ano(2024).status(StatusCardapio.RASCUNHO)
                .itens(itens).build();
    }

    @Test
    void validarCardapioCompleto_requisitosAtendidos_deveRetornarAprovado() {
        Cardapio cardapio = buildCardapioComItensRicos();
        when(cardapioRepository.findById(1L)).thenReturn(Optional.of(cardapio));

        List<ValidacaoNutricionalDTO> resultado =
                validacaoNutricionalService.validarCardapioCompleto(1L, FaixaEtaria.FUNDAMENTAL_1);

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.stream().allMatch(ValidacaoNutricionalDTO::aprovado),
                "Todos os dias devem estar aprovados com alimento rico em nutrientes");
    }

    @Test
    void validarCardapioCompleto_requisitosNaoAtendidos_deveRetornarReprovado() {
        Cardapio cardapio = buildCardapioComItensPobres();
        when(cardapioRepository.findById(1L)).thenReturn(Optional.of(cardapio));

        List<ValidacaoNutricionalDTO> resultado =
                validacaoNutricionalService.validarCardapioCompleto(1L, FaixaEtaria.FUNDAMENTAL_1);

        assertTrue(resultado.stream().anyMatch(v -> !v.aprovado()),
                "Deve haver dias reprovados com alimento pobre em nutrientes");
    }

    @Test
    void validarCardapioCompleto_resultadoComAlertas_deveConterMensagens() {
        Cardapio cardapio = buildCardapioComItensPobres();
        when(cardapioRepository.findById(1L)).thenReturn(Optional.of(cardapio));

        List<ValidacaoNutricionalDTO> resultado =
                validacaoNutricionalService.validarCardapioCompleto(1L, FaixaEtaria.FUNDAMENTAL_1);

        assertTrue(resultado.stream().anyMatch(v -> !v.alertas().isEmpty()),
                "Dias reprovados devem conter alertas");
    }

    @Test
    void validarCardapioCompleto_retornaUmResultadoPorDia() {
        Cardapio cardapio = buildCardapioComItensRicos();
        when(cardapioRepository.findById(1L)).thenReturn(Optional.of(cardapio));

        List<ValidacaoNutricionalDTO> resultado =
                validacaoNutricionalService.validarCardapioCompleto(1L, FaixaEtaria.FUNDAMENTAL_1);

        assertEquals(DiaSemana.values().length, resultado.size());
    }

    @Test
    void validarCardapioCompleto_percentuaisCalculadosCorretamente() {
        Cardapio cardapio = buildCardapioComItensRicos();
        when(cardapioRepository.findById(1L)).thenReturn(Optional.of(cardapio));

        List<ValidacaoNutricionalDTO> resultado =
                validacaoNutricionalService.validarCardapioCompleto(1L, FaixaEtaria.FUNDAMENTAL_1);

        // Com 600 kcal numa necessidade de 450, o percentual deve ser >= 100%
        resultado.forEach(v -> assertTrue(v.percentualCalorias() >= 100.0,
                "Percentual de calorias deve ser >= 100% com alimento rico"));
    }

    @Test
    void validarCardapioCompleto_cardapioInexistente_lancaResourceNotFoundException() {
        when(cardapioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> validacaoNutricionalService.validarCardapioCompleto(99L, FaixaEtaria.FUNDAMENTAL_1));
    }
}
