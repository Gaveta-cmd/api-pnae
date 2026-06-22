package com.pnae.application.service;

import com.pnae.application.dto.CardapioResponseDTO;
import com.pnae.application.dto.EscolaResumoDTO;
import com.pnae.application.dto.EstoqueItemResponseDTO;
import com.pnae.application.dto.RelatorioConsumoDTO;
import com.pnae.application.dto.RelatorioConsumoDTO.ItemConsumoDTO;
import com.pnae.application.dto.RelatorioCustoAlunoDTO;
import com.pnae.application.dto.RelatorioEscolasDTO;
import com.pnae.application.dto.RelatorioEscolasDTO.EscolaResumoAlunosDTO;
import com.pnae.application.dto.RelatorioEstoqueDTO;
import com.pnae.application.dto.RelatorioNutricionalDTO;
import com.pnae.application.dto.RelatorioNutricionalDTO.MediaNutricionalDiaDTO;
import com.pnae.application.dto.ResumoNutricionalDTO;
import com.pnae.domain.exception.ResourceNotFoundException;
import com.pnae.domain.model.Cardapio;
import com.pnae.domain.model.DiaSemana;
import com.pnae.domain.model.Escola;
import com.pnae.domain.model.TipoEscola;
import com.pnae.domain.repository.AlunoRepository;
import com.pnae.domain.repository.CardapioRepository;
import com.pnae.domain.repository.EscolaRepository;
import com.pnae.domain.repository.EstoqueItemRepository;
import com.pnae.domain.repository.ItemCardapioRepository;
import com.pnae.domain.repository.MovimentacaoEstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private static final Logger log = LoggerFactory.getLogger(RelatorioService.class);

    private final EscolaRepository escolaRepository;
    private final AlunoRepository alunoRepository;
    private final CardapioRepository cardapioRepository;
    private final ItemCardapioRepository itemCardapioRepository;
    private final EstoqueItemRepository estoqueItemRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    @Transactional(readOnly = true)
    public RelatorioEscolasDTO relatorioEscolasResumido() {
        log.info("Gerando relatório resumido de escolas");

        List<Escola> escolas = escolaRepository.findAll();

        Map<TipoEscola, Long> porTipo = escolas.stream()
                .collect(Collectors.groupingBy(Escola::getTipo, Collectors.counting()));

        List<EscolaResumoAlunosDTO> lista = escolas.stream()
                .map(e -> new EscolaResumoAlunosDTO(
                        e.getId(),
                        e.getNome(),
                        e.getTipo(),
                        alunoRepository.countByEscolaIdAndAtivoTrue(e.getId())))
                .toList();

        long totalAlunos = lista.stream().mapToLong(EscolaResumoAlunosDTO::totalAlunos).sum();

        return new RelatorioEscolasDTO(escolas.size(), totalAlunos, porTipo, lista);
    }

    @Transactional(readOnly = true)
    public RelatorioNutricionalDTO relatorioNutricionalCardapio(Long cardapioId) {
        log.info("Gerando relatório nutricional para cardápio id={}", cardapioId);

        Cardapio cardapio = cardapioRepository.findById(cardapioId)
                .orElseThrow(() -> new ResourceNotFoundException("Cardápio", cardapioId));

        Map<DiaSemana, ResumoNutricionalDTO> detalhamento = Arrays.stream(DiaSemana.values())
                .collect(Collectors.toMap(
                        dia -> dia,
                        dia -> {
                            var itensDia = cardapio.getItens().stream()
                                    .filter(i -> i.getDiaSemana() == dia)
                                    .toList();
                            return ResumoNutricionalDTO.from(dia, itensDia);
                        }
                ));

        ResumoNutricionalDTO mediaSemana = ResumoNutricionalDTO.total(cardapio.getItens());

        List<MediaNutricionalDiaDTO> mediasNativas = itemCardapioRepository
                .calcularMediaNutricionalPorDia(cardapioId)
                .stream()
                .map(row -> new MediaNutricionalDiaDTO(
                        String.valueOf(row[0]),
                        toDouble(row[1]),
                        toDouble(row[2]),
                        toDouble(row[3]),
                        toDouble(row[4])
                ))
                .toList();

        return new RelatorioNutricionalDTO(
                CardapioResponseDTO.from(cardapio),
                detalhamento,
                mediaSemana,
                mediasNativas
        );
    }

    @Transactional(readOnly = true)
    public RelatorioEstoqueDTO relatorioEstoqueEscola(Long escolaId) {
        log.info("Gerando relatório de estoque para escola id={}", escolaId);

        Escola escola = escolaRepository.findById(escolaId)
                .orElseThrow(() -> new ResourceNotFoundException("Escola", escolaId));

        List<EstoqueItemResponseDTO> todos = estoqueItemRepository.findByEscolaId(escolaId)
                .stream().map(EstoqueItemResponseDTO::from).toList();

        List<EstoqueItemResponseDTO> vencidos = estoqueItemRepository
                .findByEscolaIdAndDataValidadeBefore(escolaId, LocalDate.now())
                .stream().map(EstoqueItemResponseDTO::from).toList();

        List<EstoqueItemResponseDTO> alerta = estoqueItemRepository
                .findByEscolaIdAndDataValidadeBetween(escolaId, LocalDate.now(), LocalDate.now().plusDays(7))
                .stream().map(EstoqueItemResponseDTO::from).toList();

        BigDecimal custoTotal = estoqueItemRepository.calcularCustoTotalPorEscola(escolaId)
                .stream()
                .map(row -> toBigDecimal(row[1]))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (!vencidos.isEmpty()) {
            log.warn("Relatório: {} itens vencidos na escola {}", vencidos.size(), escola.getNome());
        }

        return new RelatorioEstoqueDTO(EscolaResumoDTO.from(escola), todos, vencidos, alerta, custoTotal);
    }

    @Transactional(readOnly = true)
    public RelatorioConsumoDTO relatorioConsumoMensal(Long escolaId, Integer mes, Integer ano) {
        log.info("Gerando relatório de consumo mensal: escola={}, mes={}/{}", escolaId, mes, ano);

        Escola escola = escolaRepository.findById(escolaId)
                .orElseThrow(() -> new ResourceNotFoundException("Escola", escolaId));

        List<Object[]> rows = movimentacaoRepository.calcularConsumoMensal(escolaId, mes, ano);

        List<ItemConsumoDTO> itens = rows.stream()
                .map(row -> new ItemConsumoDTO(
                        String.valueOf(row[0]),
                        toDouble(row[1]),
                        toBigDecimal(row[2])
                ))
                .toList();

        BigDecimal custoTotal = itens.stream()
                .map(ItemConsumoDTO::custo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RelatorioConsumoDTO(EscolaResumoDTO.from(escola), mes, ano, itens, custoTotal);
    }

    @Transactional(readOnly = true)
    public RelatorioCustoAlunoDTO relatorioCustoPorAluno(Long escolaId, Integer mes, Integer ano) {
        log.info("Gerando relatório de custo por aluno: escola={}, mes={}/{}", escolaId, mes, ano);

        Escola escola = escolaRepository.findById(escolaId)
                .orElseThrow(() -> new ResourceNotFoundException("Escola", escolaId));

        long totalAlunos = alunoRepository.countByEscolaIdAndAtivoTrue(escolaId);

        List<Object[]> rows = movimentacaoRepository.calcularConsumoMensal(escolaId, mes, ano);
        BigDecimal custoTotal = rows.stream()
                .map(row -> toBigDecimal(row[2]))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal custoPorAluno = totalAlunos > 0
                ? custoTotal.divide(BigDecimal.valueOf(totalAlunos), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new RelatorioCustoAlunoDTO(
                EscolaResumoDTO.from(escola), mes, ano, totalAlunos, custoTotal, custoPorAluno);
    }

    private double toDouble(Object obj) {
        if (obj == null) return 0.0;
        if (obj instanceof Number n) return n.doubleValue();
        return 0.0;
    }

    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) return BigDecimal.ZERO;
        if (obj instanceof BigDecimal bd) return bd;
        if (obj instanceof Number n) return BigDecimal.valueOf(n.doubleValue()).setScale(2, RoundingMode.HALF_UP);
        return BigDecimal.ZERO;
    }
}
