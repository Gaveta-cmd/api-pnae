package com.pnae.application.service;

import com.pnae.application.dto.EscolaRequestDTO;
import com.pnae.application.dto.EscolaResponseDTO;
import com.pnae.domain.exception.ResourceNotFoundException;
import com.pnae.domain.model.Endereco;
import com.pnae.domain.model.Escola;
import com.pnae.domain.repository.EscolaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EscolaService {

    private static final Logger log = LoggerFactory.getLogger(EscolaService.class);

    private final EscolaRepository escolaRepository;

    @Transactional(readOnly = true)
    public List<EscolaResponseDTO> listarTodas() {
        return escolaRepository.findAll().stream()
                .map(EscolaResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EscolaResponseDTO> listarAtivas() {
        return escolaRepository.findByAtivaTrue().stream()
                .map(EscolaResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EscolaResponseDTO buscarPorId(Long id) {
        return escolaRepository.findById(id)
                .map(EscolaResponseDTO::from)
                .orElseThrow(() -> new ResourceNotFoundException("Escola", id));
    }

    @Transactional(readOnly = true)
    public List<EscolaResponseDTO> buscarPorNome(String nome) {
        return escolaRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(EscolaResponseDTO::from)
                .toList();
    }

    @Transactional
    public EscolaResponseDTO criar(EscolaRequestDTO dto) {
        Escola escola = Escola.builder()
                .nome(dto.nome())
                .endereco(toEndereco(dto))
                .tipo(dto.tipo())
                .capacidadeAlunos(dto.capacidadeAlunos())
                .build();
        EscolaResponseDTO criada = EscolaResponseDTO.from(escolaRepository.save(escola));
        log.info("Escola criada: id={}, nome={}", criada.id(), criada.nome());
        return criada;
    }

    @Transactional
    public EscolaResponseDTO atualizar(Long id, EscolaRequestDTO dto) {
        Escola escola = escolaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Escola", id));

        escola.setNome(dto.nome());
        escola.setEndereco(toEndereco(dto));
        escola.setTipo(dto.tipo());
        escola.setCapacidadeAlunos(dto.capacidadeAlunos());

        EscolaResponseDTO atualizada = EscolaResponseDTO.from(escolaRepository.save(escola));
        log.info("Escola atualizada: id={}, nome={}", atualizada.id(), atualizada.nome());
        return atualizada;
    }

    @Transactional
    public void desativar(Long id) {
        Escola escola = escolaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Escola", id));
        escola.setAtiva(false);
        escolaRepository.save(escola);
        log.info("Escola desativada: id={}, nome={}", id, escola.getNome());
    }

    private Endereco toEndereco(EscolaRequestDTO dto) {
        return Endereco.builder()
                .logradouro(dto.endereco().logradouro())
                .numero(dto.endereco().numero())
                .complemento(dto.endereco().complemento())
                .bairro(dto.endereco().bairro())
                .cidade(dto.endereco().cidade())
                .estado(dto.endereco().estado())
                .cep(dto.endereco().cep())
                .build();
    }
}
