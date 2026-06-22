package com.pnae.application.service;

import com.pnae.application.dto.AlimentoRequestDTO;
import com.pnae.application.dto.AlimentoResponseDTO;
import com.pnae.domain.exception.ResourceNotFoundException;
import com.pnae.domain.model.Alimento;
import com.pnae.domain.model.CategoriaAlimento;
import com.pnae.domain.repository.AlimentoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlimentoService {

    private static final Logger log = LoggerFactory.getLogger(AlimentoService.class);

    private final AlimentoRepository alimentoRepository;

    @Transactional(readOnly = true)
    public List<AlimentoResponseDTO> listarTodos() {
        return alimentoRepository.findAll().stream()
                .map(AlimentoResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AlimentoResponseDTO> listarAtivos() {
        return alimentoRepository.findByAtivoTrue().stream()
                .map(AlimentoResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AlimentoResponseDTO buscarPorId(Long id) {
        return alimentoRepository.findById(id)
                .map(AlimentoResponseDTO::from)
                .orElseThrow(() -> new ResourceNotFoundException("Alimento", id));
    }

    @Transactional(readOnly = true)
    public List<AlimentoResponseDTO> listarPorCategoria(CategoriaAlimento categoria) {
        return alimentoRepository.findByCategoria(categoria).stream()
                .map(AlimentoResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AlimentoResponseDTO> buscarPorNome(String nome) {
        return alimentoRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(AlimentoResponseDTO::from)
                .toList();
    }

    @Transactional
    public AlimentoResponseDTO criar(AlimentoRequestDTO dto) {
        Alimento alimento = toEntity(dto, new Alimento());
        AlimentoResponseDTO criado = AlimentoResponseDTO.from(alimentoRepository.save(alimento));
        log.info("Alimento criado: id={}, nome={}", criado.id(), criado.nome());
        return criado;
    }

    @Transactional
    public AlimentoResponseDTO atualizar(Long id, AlimentoRequestDTO dto) {
        Alimento alimento = alimentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alimento", id));
        return AlimentoResponseDTO.from(alimentoRepository.save(toEntity(dto, alimento)));
    }

    @Transactional
    public void desativar(Long id) {
        Alimento alimento = alimentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alimento", id));
        alimento.setAtivo(false);
        alimentoRepository.save(alimento);
        log.info("Alimento desativado: id={}, nome={}", id, alimento.getNome());
    }

    private Alimento toEntity(AlimentoRequestDTO dto, Alimento alimento) {
        alimento.setNome(dto.nome());
        alimento.setCategoria(dto.categoria());
        alimento.setCalorias(dto.calorias());
        alimento.setProteinas(dto.proteinas());
        alimento.setCarboidratos(dto.carboidratos());
        alimento.setGorduras(dto.gorduras());
        alimento.setFibras(dto.fibras());
        alimento.setCalcio(dto.calcio());
        alimento.setFerro(dto.ferro());
        alimento.setCustoPorKg(dto.custoPorKg());
        return alimento;
    }
}
