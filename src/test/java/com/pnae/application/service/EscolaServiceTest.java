package com.pnae.application.service;

import com.pnae.application.dto.EscolaRequestDTO;
import com.pnae.application.dto.EscolaResponseDTO;
import com.pnae.application.dto.EnderecoDTO;
import com.pnae.domain.exception.ResourceNotFoundException;
import com.pnae.domain.model.Endereco;
import com.pnae.domain.model.Escola;
import com.pnae.domain.model.TipoEscola;
import com.pnae.domain.model.UF;
import com.pnae.domain.repository.EscolaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EscolaServiceTest {

    @Mock
    private EscolaRepository escolaRepository;

    @InjectMocks
    private EscolaService escolaService;

    private Escola buildEscola(Long id, boolean ativa) {
        return Escola.builder()
                .id(id)
                .nome("EMEF Teste")
                .tipo(TipoEscola.MUNICIPAL)
                .capacidadeAlunos(200)
                .ativa(ativa)
                .endereco(Endereco.builder()
                        .logradouro("Rua das Flores")
                        .cidade("São Paulo")
                        .estado(UF.SP)
                        .cep("01310-100")
                        .build())
                .build();
    }

    private EscolaRequestDTO buildRequest() {
        return new EscolaRequestDTO(
                "EMEF Teste",
                new EnderecoDTO("Rua das Flores", "100", null, "Centro", "São Paulo", UF.SP, "01310-100"),
                TipoEscola.MUNICIPAL,
                200
        );
    }

    @Test
    void criar_bodyValido_chamaSaveERetornaDTO() {
        Escola salva = buildEscola(1L, true);
        when(escolaRepository.save(any(Escola.class))).thenReturn(salva);

        EscolaResponseDTO resultado = escolaService.criar(buildRequest());

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("EMEF Teste", resultado.nome());
        verify(escolaRepository, times(1)).save(any(Escola.class));
    }

    @Test
    void buscarPorId_idExistente_retornaDTO() {
        when(escolaRepository.findById(1L)).thenReturn(Optional.of(buildEscola(1L, true)));

        EscolaResponseDTO resultado = escolaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
    }

    @Test
    void buscarPorId_idInexistente_lancaResourceNotFoundException() {
        when(escolaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> escolaService.buscarPorId(99L));
    }

    @Test
    void desativar_escolaExistente_setaAtivaFalse() {
        Escola escola = buildEscola(1L, true);
        when(escolaRepository.findById(1L)).thenReturn(Optional.of(escola));
        when(escolaRepository.save(any())).thenReturn(escola);

        escolaService.desativar(1L);

        assertFalse(escola.isAtiva());
        verify(escolaRepository).save(escola);
    }

    @Test
    void desativar_idInexistente_lancaResourceNotFoundException() {
        when(escolaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> escolaService.desativar(99L));
    }

    @Test
    void listarAtivas_retornaApenasEscolasAtivas() {
        Escola ativa = buildEscola(1L, true);
        when(escolaRepository.findByAtivaTrue()).thenReturn(List.of(ativa));

        List<EscolaResponseDTO> resultado = escolaService.listarAtivas();

        assertEquals(1, resultado.size());
        verify(escolaRepository).findByAtivaTrue();
        verify(escolaRepository, never()).findAll();
    }

    @Test
    void listarTodas_retornaTodasEscolas() {
        when(escolaRepository.findAll()).thenReturn(List.of(buildEscola(1L, true), buildEscola(2L, false)));

        List<EscolaResponseDTO> resultado = escolaService.listarTodas();

        assertEquals(2, resultado.size());
    }

    @Test
    void criar_verificaCamposCorretamenteMapeados() {
        ArgumentCaptor<Escola> captor = ArgumentCaptor.forClass(Escola.class);
        Escola salva = buildEscola(1L, true);
        when(escolaRepository.save(captor.capture())).thenReturn(salva);

        escolaService.criar(buildRequest());

        Escola capturada = captor.getValue();
        assertEquals("EMEF Teste", capturada.getNome());
        assertEquals(TipoEscola.MUNICIPAL, capturada.getTipo());
        assertEquals(200, capturada.getCapacidadeAlunos());
        assertEquals("Rua das Flores", capturada.getEndereco().getLogradouro());
    }
}
