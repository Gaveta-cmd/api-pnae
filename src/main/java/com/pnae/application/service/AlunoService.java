package com.pnae.application.service;

import com.pnae.application.dto.AlunoRequestDTO;
import com.pnae.application.dto.AlunoResponseDTO;
import com.pnae.domain.exception.BusinessException;
import com.pnae.domain.exception.ResourceNotFoundException;
import com.pnae.domain.model.Aluno;
import com.pnae.domain.model.Escola;
import com.pnae.domain.repository.AlunoRepository;
import com.pnae.domain.repository.EscolaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private static final Logger log = LoggerFactory.getLogger(AlunoService.class);

    private final AlunoRepository alunoRepository;
    private final EscolaRepository escolaRepository;

    @Transactional(readOnly = true)
    public List<AlunoResponseDTO> listarTodos() {
        return alunoRepository.findAll().stream()
                .map(AlunoResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AlunoResponseDTO buscarPorId(Long id) {
        return alunoRepository.findById(id)
                .map(AlunoResponseDTO::from)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
    }

    @Transactional(readOnly = true)
    public List<AlunoResponseDTO> listarPorEscola(Long escolaId) {
        if (!escolaRepository.existsById(escolaId)) {
            throw new ResourceNotFoundException("Escola", escolaId);
        }
        return alunoRepository.findByEscolaId(escolaId).stream()
                .map(AlunoResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AlunoResponseDTO> buscarPorNome(String nome) {
        return alunoRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(AlunoResponseDTO::from)
                .toList();
    }

    @Transactional
    public AlunoResponseDTO criar(AlunoRequestDTO dto) {
        Escola escola = buscarEscolaValida(dto.escolaId());
        validarCapacidade(escola);

        if (alunoRepository.findByMatricula(dto.matricula()).isPresent()) {
            throw new BusinessException("Matrícula '" + dto.matricula() + "' já está em uso");
        }

        Aluno aluno = Aluno.builder()
                .nome(dto.nome())
                .dataNascimento(dto.dataNascimento())
                .cpf(dto.cpf())
                .matricula(dto.matricula())
                .escola(escola)
                .build();

        AlunoResponseDTO criado = AlunoResponseDTO.from(alunoRepository.save(aluno));
        log.info("Aluno matriculado: id={}, nome={}, escola={}", criado.id(), criado.nome(), escola.getNome());
        return criado;
    }

    @Transactional
    public AlunoResponseDTO atualizar(Long id, AlunoRequestDTO dto) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", id));

        Escola escola = buscarEscolaValida(dto.escolaId());

        boolean trocouEscola = !aluno.getEscola().getId().equals(escola.getId());
        if (trocouEscola) {
            validarCapacidade(escola);
        }

        alunoRepository.findByMatricula(dto.matricula())
                .filter(a -> !a.getId().equals(id))
                .ifPresent(a -> { throw new BusinessException("Matrícula '" + dto.matricula() + "' já está em uso"); });

        aluno.setNome(dto.nome());
        aluno.setDataNascimento(dto.dataNascimento());
        aluno.setCpf(dto.cpf());
        aluno.setMatricula(dto.matricula());
        aluno.setEscola(escola);

        return AlunoResponseDTO.from(alunoRepository.save(aluno));
    }

    @Transactional
    public void desativar(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
        aluno.setAtivo(false);
        alunoRepository.save(aluno);
        log.info("Aluno desativado: id={}, nome={}", id, aluno.getNome());
    }

    private Escola buscarEscolaValida(Long escolaId) {
        Escola escola = escolaRepository.findById(escolaId)
                .orElseThrow(() -> new ResourceNotFoundException("Escola", escolaId));
        if (!escola.isAtiva()) {
            throw new BusinessException("Não é possível matricular aluno em escola inativa");
        }
        return escola;
    }

    private void validarCapacidade(Escola escola) {
        long alunosAtivos = alunoRepository.countByEscolaIdAndAtivoTrue(escola.getId());
        if (alunosAtivos >= escola.getCapacidadeAlunos()) {
            throw new BusinessException(
                    "Escola '" + escola.getNome() + "' atingiu a capacidade máxima de " + escola.getCapacidadeAlunos() + " alunos"
            );
        }
    }
}
