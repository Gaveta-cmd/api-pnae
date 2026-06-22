package com.pnae.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pnae.application.dto.AuthRequestDTO;
import com.pnae.application.dto.AuthResponseDTO;
import com.pnae.application.dto.UsuarioResponseDTO;
import com.pnae.application.service.UsuarioService;
import com.pnae.domain.model.Role;
import com.pnae.domain.model.Usuario;
import com.pnae.infrastructure.config.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = AuthController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UsuarioService usuarioService;

    private Usuario buildUsuario() {
        return Usuario.builder()
                .id(1L)
                .nome("Admin")
                .email("admin@pnae.gov.br")
                .senha("$2a$10$encoded")
                .role(Role.ADMIN)
                .ativo(true)
                .dataCriacao(LocalDateTime.now())
                .build();
    }

    @Test
    void login_credenciaisValidas_deveRetornar200ComToken() throws Exception {
        Usuario usuario = buildUsuario();
        UsuarioResponseDTO usuarioDTO = UsuarioResponseDTO.from(usuario);
        AuthResponseDTO authResponse = AuthResponseDTO.of("jwt-token-exemplo", usuarioDTO);

        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken("admin@pnae.gov.br", "Admin@2024!"));
        when(usuarioService.loadUserByUsername("admin@pnae.gov.br")).thenReturn(usuario);
        when(jwtService.generateToken(any())).thenReturn("jwt-token-exemplo");

        AuthRequestDTO request = new AuthRequestDTO("admin@pnae.gov.br", "Admin@2024!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-exemplo"))
                .andExpect(jsonPath("$.tipo").value("Bearer"));
    }

    @Test
    void login_credenciaisInvalidas_deveRetornar401() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        AuthRequestDTO request = new AuthRequestDTO("admin@pnae.gov.br", "senha-errada");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_emailVazio_deveRetornar400() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("", "Admin@2024!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
