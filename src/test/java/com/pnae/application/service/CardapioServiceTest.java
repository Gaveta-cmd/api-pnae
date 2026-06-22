package com.pnae.application.service;

import com.pnae.application.dto.CardapioRequestDTO;
import com.pnae.application.dto.CardapioResponseDTO;
import com.pnae.application.dto.ItemCardapioRequestDTO;
import com.pnae.application.dto.ResumoNutricionalDTO;
import com.pnae.domain.exception.BusinessException;
import com.pnae.domain.exception.ResourceNotFoundException;
import com.pnae.domain.model.Alimento;
import com.pnae.domain.model.CategoriaAlimento;
import com.pnae.domain.model.Cardapio;
import com.pnae.domain.model.DiaSemana;
import com.pnae.domain.model.Endereco;
import com.pnae.domain.model.Escola;
import com.pnae.domain.model.ItemCardapio;
import com.pnae.domain.model.StatusCardapio;
import com.pnae.domain.model.TipoEscola;
import com.pnae.domain.model.TipoRefeicao;
import com.pnae.domain.model.UF;
import com.pnae.domain.repository.AlimentoRepository;
import com.pnae.domain.repository.CardapioRepository;
import com.pnae.domain.repository.EscolaRepository;
import com.pnae.domain.repository.ItemCardapioRepository;
import com.pnae.domain.service.ValidacaoNutricionalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardapioServiceTest {

    @Mock private CardapioRepository cardapioRepository;
    @Mock private ItemCardapioRepository itemCardapioRepository;
    @Mock private EscolaRepository escolaRepository;
    @Mock private AlimentoRepository alimentoRepository;
    @Mock private ValidacaoNutricionalService validacaoNutricionalService;

    @InjectMocks
    private CardapioService cardapioService;

    private Escola buildEscola(boolean ativa) {
        return Escola.builder()
                .id(1L).nome("EMEF Teste").tipo(TipoEscola.MUNICIPAL)
                .capacidadeAlunos(100).ativa(ativa)
                .endereco(Endereco.builder()
                        .logradouro("Rua A").cidade("SP").estado(UF.SP).cep("01310-100").build())
                .build();
    }

    private Cardapio buildCardapio(StatusCardapio status) {
        return Cardapio.builder()
                .id(1L).escola(buildEscola(true)).nome("Cardápio Semana 1")
                .semana(1).ano(2024).status(status)
                .itens(new ArrayList<>())
                .build();
    }

    private Alimento buildAlimento() {
        Alimento a = new Alimento();
        a.setId(1L); a.setNome("Arroz"); a.setCategoria(CategoriaAlimento.CEREAL);
        a.setCalorias(130.0); a.setProteinas(2.7); a.setCarboidratos(28.0);
        a.setGorduras(0.3); a.setFibras(0.4); a.setCalcio(3.0); a.setFerro(0.2);
        a.setCustoPorKg(new BigDecimal("5.00")); a.setAtivo(true);
        return a;
    }

    private CardapioRequestDTO buildCardapioRequest() {
        return new CardapioRequestDTO(1L, "Cardápio Semana 1", 1, 2024, null);
    }

    @Test
    void criarCardapio_escolaAtiva_criaSucesso() {
        Escola escola = buildEscola(true);
        Cardapio salvo = buildCardapio(StatusCardapio.RASCUNHO);
        when(escolaRepository.findById(1L)).thenReturn(Optional.of(escola));
        when(cardapioRepository.findByEscolaIdAndSemanaAndAno(1L, 1, 2024)).thenReturn(Optional.empty());
        when(cardapioRepository.save(any())).thenReturn(salvo);

        CardapioResponseDTO resultado = cardapioService.criarCardapio(buildCardapioRequest());

        assertNotNull(resultado);
        assertEquals(StatusCardapio.RASCUNHO, resultado.status());
        verify(cardapioRepository).save(any());
    }

    @Test
    void criarCardapio_escolaInativa_lancaBusinessException() {
        when(escolaRepository.findById(1L)).thenReturn(Optional.of(buildEscola(false)));

        assertThrows(BusinessException.class, () -> cardapioService.criarCardapio(buildCardapioRequest()));
        verify(cardapioRepository, never()).save(any());
    }

    @Test
    void criarCardapio_duplicado_lancaBusinessException() {
        Escola escola = buildEscola(true);
        Cardapio existente = buildCardapio(StatusCardapio.RASCUNHO);
        when(escolaRepository.findById(1L)).thenReturn(Optional.of(escola));
        when(cardapioRepository.findByEscolaIdAndSemanaAndAno(1L, 1, 2024))
                .thenReturn(Optional.of(existente));

        assertThrows(BusinessException.class, () -> cardapioService.criarCardapio(buildCardapioRequest()));
        verify(cardapioRepository, never()).save(any());
    }

    @Test
    void adicionarItem_cardapioRascunho_adicionaSucesso() {
        Cardapio cardapio = buildCardapio(StatusCardapio.RASCUNHO);
        Alimento alimento = buildAlimento();
        ItemCardapio itemSalvo = ItemCardapio.builder()
                .id(1L).cardapio(cardapio).alimento(alimento)
                .diaSemana(DiaSemana.SEGUNDA).tipoRefeicao(TipoRefeicao.ALMOCO)
                .quantidadeGramas(200.0).build();

        when(cardapioRepository.findById(1L)).thenReturn(Optional.of(cardapio));
        when(alimentoRepository.findById(1L)).thenReturn(Optional.of(alimento));
        when(itemCardapioRepository.save(any())).thenReturn(itemSalvo);

        ItemCardapioRequestDTO dto = new ItemCardapioRequestDTO(1L, DiaSemana.SEGUNDA, TipoRefeicao.ALMOCO, 200.0);
        var resultado = cardapioService.adicionarItem(1L, dto);

        assertNotNull(resultado);
        verify(itemCardapioRepository).save(any());
    }

    @Test
    void adicionarItem_cardapioAprovado_lancaBusinessException() {
        Cardapio cardapio = buildCardapio(StatusCardapio.APROVADO);
        when(cardapioRepository.findById(1L)).thenReturn(Optional.of(cardapio));

        ItemCardapioRequestDTO dto = new ItemCardapioRequestDTO(1L, DiaSemana.SEGUNDA, TipoRefeicao.ALMOCO, 200.0);

        assertThrows(BusinessException.class, () -> cardapioService.adicionarItem(1L, dto));
        verify(itemCardapioRepository, never()).save(any());
    }

    @Test
    void calcularResumoNutricional_comItens_retornaResumo() {
        Alimento alimento = buildAlimento();
        ItemCardapio item = ItemCardapio.builder()
                .alimento(alimento).diaSemana(DiaSemana.SEGUNDA)
                .tipoRefeicao(TipoRefeicao.ALMOCO).quantidadeGramas(200.0).build();

        Cardapio cardapio = buildCardapio(StatusCardapio.RASCUNHO);
        cardapio.getItens().add(item);

        when(cardapioRepository.findById(1L)).thenReturn(Optional.of(cardapio));

        ResumoNutricionalDTO resumo = cardapioService.calcularResumoNutricional(1L);

        assertNotNull(resumo);
        // 130 kcal/100g × 200g = 260 kcal
        assertEquals(260.0, resumo.totalCalorias(), 0.01);
    }

    @Test
    void buscarPorId_cardapioInexistente_lancaResourceNotFoundException() {
        when(cardapioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cardapioService.buscarPorId(99L));
    }

    @Test
    void aprovarCardapio_statusValidado_mudaParaAprovado() {
        Cardapio cardapio = buildCardapio(StatusCardapio.VALIDADO);
        when(cardapioRepository.findById(1L)).thenReturn(Optional.of(cardapio));
        when(cardapioRepository.save(any())).thenReturn(cardapio);

        CardapioResponseDTO resultado = cardapioService.aprovarCardapio(1L);

        assertEquals(StatusCardapio.APROVADO, cardapio.getStatus());
    }

    @Test
    void aprovarCardapio_statusRascunho_lancaBusinessException() {
        Cardapio cardapio = buildCardapio(StatusCardapio.RASCUNHO);
        when(cardapioRepository.findById(1L)).thenReturn(Optional.of(cardapio));

        assertThrows(BusinessException.class, () -> cardapioService.aprovarCardapio(1L));
    }
}
