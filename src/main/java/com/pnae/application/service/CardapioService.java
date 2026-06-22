package com.pnae.application.service;

import com.pnae.application.dto.CardapioRequestDTO;
import com.pnae.application.dto.CardapioResponseDTO;
import com.pnae.application.dto.ItemCardapioRequestDTO;
import com.pnae.application.dto.ItemCardapioResponseDTO;
import com.pnae.application.dto.ResumoNutricionalDTO;
import com.pnae.application.dto.ValidacaoNutricionalDTO;
import com.pnae.domain.exception.BusinessException;
import com.pnae.domain.exception.ResourceNotFoundException;
import com.pnae.domain.model.Alimento;
import com.pnae.domain.model.Cardapio;
import com.pnae.domain.model.DiaSemana;
import com.pnae.domain.model.Escola;
import com.pnae.domain.model.FaixaEtaria;
import com.pnae.domain.model.ItemCardapio;
import com.pnae.domain.model.StatusCardapio;
import com.pnae.domain.repository.AlimentoRepository;
import com.pnae.domain.repository.CardapioRepository;
import com.pnae.domain.repository.EscolaRepository;
import com.pnae.domain.repository.ItemCardapioRepository;
import com.pnae.domain.service.ValidacaoNutricionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CardapioService {

    private final CardapioRepository cardapioRepository;
    private final ItemCardapioRepository itemCardapioRepository;
    private final EscolaRepository escolaRepository;
    private final AlimentoRepository alimentoRepository;
    private final @Lazy ValidacaoNutricionalService validacaoNutricionalService;

    @Transactional
    public CardapioResponseDTO criarCardapio(CardapioRequestDTO dto) {
        Escola escola = escolaRepository.findById(dto.escolaId())
                .orElseThrow(() -> new ResourceNotFoundException("Escola", dto.escolaId()));
        if (!escola.isAtiva()) {
            throw new BusinessException("Não é possível criar cardápio para escola inativa");
        }
        cardapioRepository.findByEscolaIdAndSemanaAndAno(dto.escolaId(), dto.semana(), dto.ano())
                .ifPresent(c -> { throw new BusinessException(
                        "Já existe cardápio para '" + escola.getNome() + "' na semana " + dto.semana() + "/" + dto.ano()); });

        Cardapio cardapio = Cardapio.builder()
                .escola(escola)
                .nome(dto.nome())
                .semana(dto.semana())
                .ano(dto.ano())
                .observacoes(dto.observacoes())
                .build();
        return CardapioResponseDTO.from(cardapioRepository.save(cardapio));
    }

    @Transactional(readOnly = true)
    public CardapioResponseDTO buscarPorId(Long id) {
        return CardapioResponseDTO.from(buscarCardapio(id));
    }

    @Transactional(readOnly = true)
    public List<CardapioResponseDTO> listarPorEscola(Long escolaId) {
        if (!escolaRepository.existsById(escolaId)) {
            throw new ResourceNotFoundException("Escola", escolaId);
        }
        return cardapioRepository.findByEscolaId(escolaId).stream()
                .map(CardapioResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CardapioResponseDTO buscarPorEscolaESemana(Long escolaId, Integer semana, Integer ano) {
        return cardapioRepository.findByEscolaIdAndSemanaAndAno(escolaId, semana, ano)
                .map(CardapioResponseDTO::from)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cardápio não encontrado para escola " + escolaId + " na semana " + semana + "/" + ano));
    }

    @Transactional
    public ItemCardapioResponseDTO adicionarItem(Long cardapioId, ItemCardapioRequestDTO dto) {
        Cardapio cardapio = buscarCardapio(cardapioId);
        if (cardapio.getStatus() == StatusCardapio.APROVADO) {
            throw new BusinessException("Não é possível editar um cardápio já APROVADO");
        }
        Alimento alimento = alimentoRepository.findById(dto.alimentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Alimento", dto.alimentoId()));
        if (!alimento.isAtivo()) {
            throw new BusinessException("Alimento '" + alimento.getNome() + "' está inativo");
        }
        ItemCardapio item = ItemCardapio.builder()
                .cardapio(cardapio)
                .alimento(alimento)
                .diaSemana(dto.diaSemana())
                .tipoRefeicao(dto.tipoRefeicao())
                .quantidadeGramas(dto.quantidadeGramas())
                .build();
        return ItemCardapioResponseDTO.from(itemCardapioRepository.save(item));
    }

    @Transactional
    public void removerItem(Long cardapioId, Long itemId) {
        Cardapio cardapio = buscarCardapio(cardapioId);
        if (cardapio.getStatus() == StatusCardapio.APROVADO) {
            throw new BusinessException("Não é possível editar um cardápio já APROVADO");
        }
        ItemCardapio item = itemCardapioRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item de cardápio", itemId));
        if (!item.getCardapio().getId().equals(cardapioId)) {
            throw new BusinessException("Item não pertence ao cardápio informado");
        }
        itemCardapioRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public ResumoNutricionalDTO calcularResumoNutricional(Long cardapioId) {
        return ResumoNutricionalDTO.total(buscarCardapio(cardapioId).getItens());
    }

    @Transactional(readOnly = true)
    public ResumoNutricionalDTO calcularResumoNutricionalPorDia(Long cardapioId, DiaSemana dia) {
        Cardapio cardapio = buscarCardapio(cardapioId);
        List<ItemCardapio> itensDia = cardapio.getItens().stream()
                .filter(i -> i.getDiaSemana() == dia)
                .toList();
        return ResumoNutricionalDTO.from(dia, itensDia);
    }

    @Transactional
    public Map<FaixaEtaria, List<ValidacaoNutricionalDTO>> validarParaFaixa(Long id, FaixaEtaria faixa) {
        Cardapio cardapio = buscarCardapio(id);
        List<ValidacaoNutricionalDTO> resultado = validacaoNutricionalService.validarCardapioCompleto(id, faixa);
        boolean aprovado = resultado.stream().allMatch(ValidacaoNutricionalDTO::aprovado);
        cardapio.setStatus(aprovado ? StatusCardapio.VALIDADO : StatusCardapio.REJEITADO);
        if (!aprovado) {
            String alertas = resultado.stream()
                    .filter(v -> !v.aprovado())
                    .flatMap(v -> v.alertas().stream())
                    .distinct()
                    .reduce("", (a, b) -> a + "- " + b + "\n");
            String obs = "Validação para " + faixa.getDescricao() + " falhou:\n" + alertas;
            cardapio.setObservacoes(obs.length() > 997 ? obs.substring(0, 997) + "..." : obs);
        }
        cardapioRepository.save(cardapio);
        return Map.of(faixa, resultado);
    }

    @Transactional
    public Map<FaixaEtaria, List<ValidacaoNutricionalDTO>> validarParaEscola(Long id) {
        Cardapio cardapio = buscarCardapio(id);
        Map<FaixaEtaria, List<ValidacaoNutricionalDTO>> resultado =
                validacaoNutricionalService.validarCardapioParaEscola(id);
        boolean aprovado = resultado.values().stream()
                .flatMap(List::stream)
                .allMatch(ValidacaoNutricionalDTO::aprovado);
        cardapio.setStatus(aprovado ? StatusCardapio.VALIDADO : StatusCardapio.REJEITADO);
        if (!aprovado) {
            String alertas = resultado.values().stream()
                    .flatMap(List::stream)
                    .filter(v -> !v.aprovado())
                    .flatMap(v -> v.alertas().stream())
                    .distinct()
                    .reduce("", (a, b) -> a + "- " + b + "\n");
            String obs = "Validação para escola falhou:\n" + alertas;
            cardapio.setObservacoes(obs.length() > 997 ? obs.substring(0, 997) + "..." : obs);
        }
        cardapioRepository.save(cardapio);
        return resultado;
    }

    @Transactional
    public CardapioResponseDTO aprovarCardapio(Long id) {
        Cardapio cardapio = buscarCardapio(id);
        if (cardapio.getStatus() != StatusCardapio.VALIDADO) {
            throw new BusinessException("Somente cardápios VALIDADOS podem ser aprovados. Status atual: " + cardapio.getStatus());
        }
        cardapio.setStatus(StatusCardapio.APROVADO);
        return CardapioResponseDTO.from(cardapioRepository.save(cardapio));
    }

    @Transactional
    public CardapioResponseDTO rejeitarCardapio(Long id, String motivo) {
        Cardapio cardapio = buscarCardapio(id);
        cardapio.setStatus(StatusCardapio.REJEITADO);
        cardapio.setObservacoes(motivo);
        return CardapioResponseDTO.from(cardapioRepository.save(cardapio));
    }

    public Cardapio buscarCardapio(Long id) {
        return cardapioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cardápio", id));
    }
}
