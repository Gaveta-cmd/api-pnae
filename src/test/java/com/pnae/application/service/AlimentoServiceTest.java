package com.pnae.application.service;

import com.pnae.application.dto.AlimentoRequestDTO;
import com.pnae.application.dto.AlimentoResponseDTO;
import com.pnae.domain.exception.ResourceNotFoundException;
import com.pnae.domain.model.Alimento;
import com.pnae.domain.model.CategoriaAlimento;
import com.pnae.domain.repository.AlimentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlimentoServiceTest {

    @Mock
    private AlimentoRepository alimentoRepository;

    @InjectMocks
    private AlimentoService alimentoService;

    private Alimento buildAlimento(Long id) {
        Alimento a = new Alimento();
        a.setId(id);
        a.setNome("Arroz");
        a.setCategoria(CategoriaAlimento.CEREAL);
        a.setCalorias(130.0);
        a.setProteinas(2.7);
        a.setCarboidratos(28.0);
        a.setGorduras(0.3);
        a.setFibras(0.4);
        a.setCalcio(3.0);
        a.setFerro(0.2);
        a.setCustoPorKg(new BigDecimal("5.00"));
        a.setAtivo(true);
        return a;
    }

    private AlimentoRequestDTO buildRequest() {
        return new AlimentoRequestDTO(
                "Arroz", CategoriaAlimento.CEREAL,
                130.0, 2.7, 28.0, 0.3, 0.4, 3.0, 0.2,
                new BigDecimal("5.00")
        );
    }

    @Test
    void criar_bodyValido_chamaSaveERetornaDTO() {
        Alimento salvo = buildAlimento(1L);
        when(alimentoRepository.save(any())).thenReturn(salvo);

        AlimentoResponseDTO resultado = alimentoService.criar(buildRequest());

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Arroz", resultado.nome());
        verify(alimentoRepository).save(any());
    }

    @Test
    void buscarPorId_existente_retornaDTO() {
        when(alimentoRepository.findById(1L)).thenReturn(Optional.of(buildAlimento(1L)));

        AlimentoResponseDTO resultado = alimentoService.buscarPorId(1L);

        assertEquals(1L, resultado.id());
    }

    @Test
    void buscarPorId_inexistente_lancaResourceNotFoundException() {
        when(alimentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> alimentoService.buscarPorId(99L));
    }

    @Test
    void listarAtivos_chamaFindByAtivoTrue() {
        when(alimentoRepository.findByAtivoTrue()).thenReturn(List.of(buildAlimento(1L)));

        List<AlimentoResponseDTO> resultado = alimentoService.listarAtivos();

        assertEquals(1, resultado.size());
        verify(alimentoRepository).findByAtivoTrue();
    }

    @Test
    void desativar_alimentoExistente_setaAtivoFalse() {
        Alimento alimento = buildAlimento(1L);
        when(alimentoRepository.findById(1L)).thenReturn(Optional.of(alimento));
        when(alimentoRepository.save(any())).thenReturn(alimento);

        alimentoService.desativar(1L);

        assertFalse(alimento.isAtivo());
    }

    @Test
    void desativar_inexistente_lancaResourceNotFoundException() {
        when(alimentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> alimentoService.desativar(99L));
    }

    @Test
    void atualizar_existente_salvaComNovosDados() {
        Alimento existente = buildAlimento(1L);
        when(alimentoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(alimentoRepository.save(any())).thenReturn(existente);

        AlimentoRequestDTO request = new AlimentoRequestDTO(
                "Arroz Integral", CategoriaAlimento.CEREAL,
                130.0, 2.7, 28.0, 0.3, 0.4, 3.0, 0.2,
                new BigDecimal("6.00")
        );

        alimentoService.atualizar(1L, request);

        assertEquals("Arroz Integral", existente.getNome());
        verify(alimentoRepository).save(existente);
    }
}
