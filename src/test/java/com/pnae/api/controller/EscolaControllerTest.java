package com.pnae.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pnae.application.dto.EnderecoDTO;
import com.pnae.application.dto.EscolaRequestDTO;
import com.pnae.application.dto.EscolaResponseDTO;
import com.pnae.application.service.EscolaService;
import com.pnae.domain.exception.ResourceNotFoundException;
import com.pnae.domain.model.TipoEscola;
import com.pnae.domain.model.UF;
import com.pnae.infrastructure.config.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = EscolaController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class EscolaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EscolaService escolaService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    private EscolaResponseDTO buildResponse(Long id) {
        return new EscolaResponseDTO(
                id, "EMEF Teste", "Rua das Flores", "100", null,
                "Centro", "São Paulo", UF.SP, "01310-100",
                TipoEscola.MUNICIPAL, 200, true, LocalDateTime.now()
        );
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
    void listarEscolas_deveRetornar200ComLista() throws Exception {
        when(escolaService.listarTodas()).thenReturn(List.of(buildResponse(1L)));

        mockMvc.perform(get("/api/escolas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("EMEF Teste"));
    }

    @Test
    void buscarPorId_idExistente_deveRetornar200() throws Exception {
        when(escolaService.buscarPorId(1L)).thenReturn(buildResponse(1L));

        mockMvc.perform(get("/api/escolas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("EMEF Teste"));
    }

    @Test
    void buscarPorId_idInexistente_deveRetornar404() throws Exception {
        when(escolaService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Escola", 99L));

        mockMvc.perform(get("/api/escolas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void criarEscola_bodyValido_deveRetornar201() throws Exception {
        when(escolaService.criar(any())).thenReturn(buildResponse(1L));

        mockMvc.perform(post("/api/escolas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void criarEscola_nomeVazio_deveRetornar400() throws Exception {
        EscolaRequestDTO requestInvalido = new EscolaRequestDTO(
                "",
                new EnderecoDTO("Rua A", "1", null, null, "SP", UF.SP, "01310-100"),
                TipoEscola.MUNICIPAL,
                100
        );

        mockMvc.perform(post("/api/escolas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
