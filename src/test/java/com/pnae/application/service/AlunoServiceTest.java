package com.pnae.application.service;

import com.pnae.application.dto.AlunoRequestDTO;
import com.pnae.application.dto.AlunoResponseDTO;
import com.pnae.domain.exception.BusinessException;
import com.pnae.domain.exception.ResourceNotFoundException;
import com.pnae.domain.model.Aluno;
import com.pnae.domain.model.Endereco;
import com.pnae.domain.model.Escola;
import com.pnae.domain.model.TipoEscola;
import com.pnae.domain.model.UF;
import com.pnae.domain.repository.AlunoRepository;
import com.pnae.domain.repository.EscolaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private EscolaRepository escolaRepository;

    @InjectMocks
    private AlunoService alunoService;

    private Escola buildEscolaAtiva() {
        return Escola.builder()
                .id(1L)
                .nome("EMEF Teste")
                .tipo(TipoEscola.MUNICIPAL)
                .capacidadeAlunos(100)
                .ativa(true)
                .endereco(Endereco.builder()
                        .logradouro("Rua A")
                        .cidade("SP")
                        .estado(UF.SP)
                        .cep("01310-100")
                        .build())
                .build();
    }

    private Escola buildEscolaInativa() {
        Escola e = buildEscolaAtiva();
        e.setAtiva(false);
        return e;
    }

    private AlunoRequestDTO buildRequest(Long escolaId) {
        return new AlunoRequestDTO(
                "João Silva",
                LocalDate.of(2015, 3, 10),
                null,
                "MAT001",
                escolaId
        );
    }

    private Aluno buildAluno() {
        return Aluno.builder()
                .id(1L)
                .nome("João Silva")
                .dataNascimento(LocalDate.of(2015, 3, 10))
                .matricula("MAT001")
                .ativo(true)
                .escola(buildEscolaAtiva())
                .build();
    }

    @Test
    void criar_escolaAtivaSemLotacao_deveSalvar() {
        Escola escola = buildEscolaAtiva();
        when(escolaRepository.findById(1L)).thenReturn(Optional.of(escola));
        when(alunoRepository.countByEscolaIdAndAtivoTrue(1L)).thenReturn(50L);
        when(alunoRepository.findByMatricula("MAT001")).thenReturn(Optional.empty());
        when(alunoRepository.save(any())).thenReturn(buildAluno());

        AlunoResponseDTO resultado = alunoService.criar(buildRequest(1L));

        assertNotNull(resultado);
        verify(alunoRepository).save(any());
    }

    @Test
    void criar_escolaInativa_lancaBusinessException() {
        when(escolaRepository.findById(1L)).thenReturn(Optional.of(buildEscolaInativa()));

        assertThrows(BusinessException.class, () -> alunoService.criar(buildRequest(1L)));
        verify(alunoRepository, never()).save(any());
    }

    @Test
    void criar_capacidadeExcedida_lancaBusinessException() {
        Escola escola = buildEscolaAtiva(); // capacidade 100
        when(escolaRepository.findById(1L)).thenReturn(Optional.of(escola));
        when(alunoRepository.countByEscolaIdAndAtivoTrue(1L)).thenReturn(100L);

        assertThrows(BusinessException.class, () -> alunoService.criar(buildRequest(1L)));
        verify(alunoRepository, never()).save(any());
    }

    @Test
    void buscarPorId_existente_retornaDTO() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(buildAluno()));

        AlunoResponseDTO resultado = alunoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
    }

    @Test
    void buscarPorId_inexistente_lancaResourceNotFoundException() {
        when(alunoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> alunoService.buscarPorId(99L));
    }

    @Test
    void desativar_alunoExistente_setaAtivoFalse() {
        Aluno aluno = buildAluno();
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any())).thenReturn(aluno);

        alunoService.desativar(1L);

        assertFalse(aluno.isAtivo());
        verify(alunoRepository).save(aluno);
    }
}
