package com.pnae.application.service;

import com.pnae.application.dto.EstoqueEntradaDTO;
import com.pnae.application.dto.EstoqueItemResponseDTO;
import com.pnae.application.dto.EstoqueMovimentacaoDTO;
import com.pnae.application.dto.MovimentacaoResponseDTO;
import com.pnae.domain.exception.BusinessException;
import com.pnae.domain.exception.ResourceNotFoundException;
import com.pnae.domain.model.Alimento;
import com.pnae.domain.model.Escola;
import com.pnae.domain.model.EstoqueItem;
import com.pnae.domain.model.Fornecedor;
import com.pnae.domain.model.MovimentacaoEstoque;
import com.pnae.domain.model.TipoMovimentacao;
import com.pnae.domain.model.Usuario;
import com.pnae.domain.repository.AlimentoRepository;
import com.pnae.domain.repository.EscolaRepository;
import com.pnae.domain.repository.EstoqueItemRepository;
import com.pnae.domain.repository.FornecedorRepository;
import com.pnae.domain.repository.MovimentacaoEstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private static final Logger log = LoggerFactory.getLogger(EstoqueService.class);

    private final EstoqueItemRepository estoqueItemRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final AlimentoRepository alimentoRepository;
    private final EscolaRepository escolaRepository;
    private final FornecedorRepository fornecedorRepository;

    @Transactional
    public EstoqueItemResponseDTO registrarEntrada(EstoqueEntradaDTO dto, Usuario responsavel) {
        Alimento alimento = alimentoRepository.findById(dto.alimentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Alimento", dto.alimentoId()));
        Escola escola = escolaRepository.findById(dto.escolaId())
                .orElseThrow(() -> new ResourceNotFoundException("Escola", dto.escolaId()));
        Fornecedor fornecedor = dto.fornecedorId() != null
                ? fornecedorRepository.findById(dto.fornecedorId())
                        .orElseThrow(() -> new ResourceNotFoundException("Fornecedor", dto.fornecedorId()))
                : null;

        String lote = (dto.lote() != null && !dto.lote().isBlank()) ? dto.lote() : "SEM_LOTE";

        EstoqueItem item = estoqueItemRepository
                .findByEscolaIdAndAlimentoIdAndLote(dto.escolaId(), dto.alimentoId(), lote)
                .map(existing -> {
                    existing.setQuantidadeKg(existing.getQuantidadeKg() + dto.quantidadeKg());
                    if (dto.dataValidade() != null) existing.setDataValidade(dto.dataValidade());
                    if (dto.fornecedorId() != null) existing.setFornecedor(fornecedor);
                    return existing;
                })
                .orElseGet(() -> EstoqueItem.builder()
                        .alimento(alimento)
                        .escola(escola)
                        .fornecedor(fornecedor)
                        .quantidadeKg(dto.quantidadeKg())
                        .dataValidade(dto.dataValidade())
                        .lote(lote)
                        .build());

        EstoqueItem salvo = estoqueItemRepository.save(item);

        registrarMovimentacao(salvo, TipoMovimentacao.ENTRADA, dto.quantidadeKg(),
                "Entrada de estoque", responsavel);

        log.info("Entrada registrada: alimento={}, escola={}, quantidade={}kg",
                alimento.getNome(), escola.getNome(), dto.quantidadeKg());

        return EstoqueItemResponseDTO.from(salvo);
    }

    @Transactional
    public EstoqueItemResponseDTO registrarSaida(Long estoqueItemId, EstoqueMovimentacaoDTO dto, Usuario responsavel) {
        EstoqueItem item = buscarItem(estoqueItemId);

        if (item.getQuantidadeKg() < dto.quantidadeKg()) {
            throw new BusinessException(
                    String.format("Saldo insuficiente. Disponível: %.2fkg, solicitado: %.2fkg",
                            item.getQuantidadeKg(), dto.quantidadeKg()));
        }

        item.setQuantidadeKg(item.getQuantidadeKg() - dto.quantidadeKg());
        EstoqueItem salvo = estoqueItemRepository.save(item);

        registrarMovimentacao(salvo, TipoMovimentacao.SAIDA, dto.quantidadeKg(),
                dto.motivo(), responsavel);

        if (salvo.getQuantidadeKg() < 5.0) {
            log.warn("Estoque baixo: alimento={}, escola={}, saldo={}kg",
                    item.getAlimento().getNome(), item.getEscola().getNome(), salvo.getQuantidadeKg());
        }

        return EstoqueItemResponseDTO.from(salvo);
    }

    @Transactional
    public EstoqueItemResponseDTO registrarPerda(Long estoqueItemId, EstoqueMovimentacaoDTO dto, Usuario responsavel) {
        EstoqueItem item = buscarItem(estoqueItemId);

        if (item.getQuantidadeKg() < dto.quantidadeKg()) {
            throw new BusinessException(
                    String.format("Quantidade de perda (%.2fkg) supera o saldo disponível (%.2fkg)",
                            dto.quantidadeKg(), item.getQuantidadeKg()));
        }

        item.setQuantidadeKg(item.getQuantidadeKg() - dto.quantidadeKg());
        EstoqueItem salvo = estoqueItemRepository.save(item);

        registrarMovimentacao(salvo, TipoMovimentacao.PERDA, dto.quantidadeKg(),
                dto.motivo() != null ? dto.motivo() : "Perda registrada", responsavel);

        log.warn("Perda registrada: alimento={}, escola={}, quantidade={}kg, motivo={}",
                item.getAlimento().getNome(), item.getEscola().getNome(),
                dto.quantidadeKg(), dto.motivo());

        return EstoqueItemResponseDTO.from(salvo);
    }

    @Transactional(readOnly = true)
    public List<EstoqueItemResponseDTO> consultarEstoquePorEscola(Long escolaId) {
        if (!escolaRepository.existsById(escolaId)) {
            throw new ResourceNotFoundException("Escola", escolaId);
        }
        return estoqueItemRepository.findByEscolaId(escolaId).stream()
                .map(EstoqueItemResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EstoqueItemResponseDTO> consultarItensVencidos(Long escolaId) {
        return estoqueItemRepository.findByEscolaIdAndDataValidadeBefore(escolaId, LocalDate.now()).stream()
                .map(EstoqueItemResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EstoqueItemResponseDTO> consultarItensProximosVencimento(Long escolaId, Integer diasAlerta) {
        LocalDate inicio = LocalDate.now();
        LocalDate fim = inicio.plusDays(diasAlerta);
        List<EstoqueItem> itens = estoqueItemRepository.findByEscolaIdAndDataValidadeBetween(escolaId, inicio, fim);

        if (!itens.isEmpty()) {
            log.warn("Alerta de vencimento: {} itens vencem nos próximos {}d na escola {}",
                    itens.size(), diasAlerta, escolaId);
        }

        return itens.stream().map(EstoqueItemResponseDTO::from).toList();
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoResponseDTO> consultarMovimentacoes(Long estoqueItemId) {
        buscarItem(estoqueItemId);
        return movimentacaoRepository.findByEstoqueItemId(estoqueItemId).stream()
                .map(MovimentacaoResponseDTO::from)
                .toList();
    }

    private EstoqueItem buscarItem(Long id) {
        return estoqueItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item de estoque", id));
    }

    private void registrarMovimentacao(EstoqueItem item, TipoMovimentacao tipo,
                                       Double quantidade, String motivo, Usuario responsavel) {
        MovimentacaoEstoque mov = MovimentacaoEstoque.builder()
                .estoqueItem(item)
                .tipo(tipo)
                .quantidadeKg(quantidade)
                .motivo(motivo)
                .responsavel(responsavel)
                .build();
        movimentacaoRepository.save(mov);
    }
}
