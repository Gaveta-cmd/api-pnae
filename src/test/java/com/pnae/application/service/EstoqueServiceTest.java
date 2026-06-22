package com.pnae.application.service;

import com.pnae.application.dto.EstoqueEntradaDTO;
import com.pnae.application.dto.EstoqueItemResponseDTO;
import com.pnae.application.dto.EstoqueMovimentacaoDTO;
import com.pnae.domain.exception.BusinessException;
import com.pnae.domain.exception.ResourceNotFoundException;
import com.pnae.domain.model.Alimento;
import com.pnae.domain.model.CategoriaAlimento;
import com.pnae.domain.model.Endereco;
import com.pnae.domain.model.Escola;
import com.pnae.domain.model.EstoqueItem;
import com.pnae.domain.model.TipoEscola;
import com.pnae.domain.model.UF;
import com.pnae.domain.repository.AlimentoRepository;
import com.pnae.domain.repository.EscolaRepository;
import com.pnae.domain.repository.EstoqueItemRepository;
import com.pnae.domain.repository.FornecedorRepository;
import com.pnae.domain.repository.MovimentacaoEstoqueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock private EstoqueItemRepository estoqueItemRepository;
    @Mock private MovimentacaoEstoqueRepository movimentacaoRepository;
    @Mock private AlimentoRepository alimentoRepository;
    @Mock private EscolaRepository escolaRepository;
    @Mock private FornecedorRepository fornecedorRepository;

    @InjectMocks
    private EstoqueService estoqueService;

    private Alimento buildAlimento() {
        Alimento a = new Alimento();
        a.setId(1L); a.setNome("Arroz"); a.setCategoria(CategoriaAlimento.CEREAL);
        a.setCalorias(130.0); a.setProteinas(2.7); a.setCarboidratos(28.0);
        a.setGorduras(0.3); a.setFibras(0.4); a.setCalcio(3.0); a.setFerro(0.2);
        a.setCustoPorKg(new BigDecimal("5.00")); a.setAtivo(true);
        return a;
    }

    private Escola buildEscola() {
        return Escola.builder()
                .id(1L).nome("EMEF Teste").tipo(TipoEscola.MUNICIPAL)
                .capacidadeAlunos(100).ativa(true)
                .endereco(Endereco.builder()
                        .logradouro("Rua A").cidade("SP").estado(UF.SP).cep("01310-100").build())
                .build();
    }

    private EstoqueItem buildEstoqueItem(double quantidade) {
        return EstoqueItem.builder()
                .id(1L).alimento(buildAlimento()).escola(buildEscola())
                .quantidadeKg(quantidade).lote("SEM_LOTE")
                .dataValidade(LocalDate.now().plusDays(30))
                .build();
    }

    @Test
    void registrarEntrada_novoItem_criaNovo() {
        Alimento alimento = buildAlimento();
        Escola escola = buildEscola();
        EstoqueItem itemSalvo = buildEstoqueItem(10.0);

        when(alimentoRepository.findById(1L)).thenReturn(Optional.of(alimento));
        when(escolaRepository.findById(1L)).thenReturn(Optional.of(escola));
        when(estoqueItemRepository.findByEscolaIdAndAlimentoIdAndLote(1L, 1L, "SEM_LOTE"))
                .thenReturn(Optional.empty());
        when(estoqueItemRepository.save(any())).thenReturn(itemSalvo);
        when(movimentacaoRepository.save(any())).thenReturn(null);

        EstoqueEntradaDTO dto = new EstoqueEntradaDTO(1L, 1L, null, 10.0,
                LocalDate.now().plusDays(30), null);

        EstoqueItemResponseDTO resultado = estoqueService.registrarEntrada(dto, null);

        assertNotNull(resultado);
        verify(estoqueItemRepository).save(any());
        verify(movimentacaoRepository).save(any());
    }

    @Test
    void registrarEntrada_itemExistente_acumulaQuantidade() {
        EstoqueItem itemExistente = buildEstoqueItem(20.0);
        when(alimentoRepository.findById(1L)).thenReturn(Optional.of(buildAlimento()));
        when(escolaRepository.findById(1L)).thenReturn(Optional.of(buildEscola()));
        when(estoqueItemRepository.findByEscolaIdAndAlimentoIdAndLote(1L, 1L, "SEM_LOTE"))
                .thenReturn(Optional.of(itemExistente));
        when(estoqueItemRepository.save(any())).thenReturn(itemExistente);
        when(movimentacaoRepository.save(any())).thenReturn(null);

        EstoqueEntradaDTO dto = new EstoqueEntradaDTO(1L, 1L, null, 5.0,
                LocalDate.now().plusDays(30), null);

        estoqueService.registrarEntrada(dto, null);

        assertEquals(25.0, itemExistente.getQuantidadeKg(), 0.01);
    }

    @Test
    void registrarSaida_saldoSuficiente_subtraiQuantidade() {
        EstoqueItem item = buildEstoqueItem(50.0);
        when(estoqueItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(estoqueItemRepository.save(any())).thenReturn(item);
        when(movimentacaoRepository.save(any())).thenReturn(null);

        EstoqueMovimentacaoDTO dto = new EstoqueMovimentacaoDTO(10.0, "Consumo");

        estoqueService.registrarSaida(1L, dto, null);

        assertEquals(40.0, item.getQuantidadeKg(), 0.01);
        verify(movimentacaoRepository).save(any());
    }

    @Test
    void registrarSaida_saldoInsuficiente_lancaBusinessException() {
        EstoqueItem item = buildEstoqueItem(5.0);
        when(estoqueItemRepository.findById(1L)).thenReturn(Optional.of(item));

        EstoqueMovimentacaoDTO dto = new EstoqueMovimentacaoDTO(10.0, "Consumo");

        assertThrows(BusinessException.class, () -> estoqueService.registrarSaida(1L, dto, null));
        verify(estoqueItemRepository, never()).save(any());
    }

    @Test
    void registrarSaida_itemInexistente_lancaResourceNotFoundException() {
        when(estoqueItemRepository.findById(99L)).thenReturn(Optional.empty());

        EstoqueMovimentacaoDTO dto = new EstoqueMovimentacaoDTO(5.0, "Consumo");

        assertThrows(ResourceNotFoundException.class, () -> estoqueService.registrarSaida(99L, dto, null));
    }

    @Test
    void consultarItensVencidos_retornaApenasVencidos() {
        EstoqueItem vencido = buildEstoqueItem(10.0);
        vencido.setDataValidade(LocalDate.now().minusDays(1));

        when(estoqueItemRepository.findByEscolaIdAndDataValidadeBefore(eq(1L), any()))
                .thenReturn(List.of(vencido));

        List<EstoqueItemResponseDTO> resultado = estoqueService.consultarItensVencidos(1L);

        assertEquals(1, resultado.size());
    }
}
