package com.pnae.application.service;

import com.pnae.application.dto.EscolaRequestDTO;
import com.pnae.application.dto.EscolaResponseDTO;
import com.pnae.domain.exception.ResourceNotFoundException;
import com.pnae.domain.model.Endereco;
import com.pnae.domain.model.Escola;
import com.pnae.domain.repository.EscolaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EscolaService {

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
        return EscolaResponseDTO.from(escolaRepository.save(escola));
    }

    @Transactional
    public EscolaResponseDTO atualizar(Long id, EscolaRequestDTO dto) {
        Escola escola = escolaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Escola", id));

        escola.setNome(dto.nome());
        escola.setEndereco(toEndereco(dto));
        escola.setTipo(dto.tipo());
        escola.setCapacidadeAlunos(dto.capacidadeAlunos());

        return EscolaResponseDTO.from(escolaRepository.save(escola));
    }

    @Transactional
    public void desativar(Long id) {
        Escola escola = escolaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Escola", id));
        escola.setAtiva(false);
        escolaRepository.save(escola);
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
