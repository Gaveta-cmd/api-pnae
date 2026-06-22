package com.pnae.api.controller;

import com.pnae.application.dto.AuthRequestDTO;
import com.pnae.application.dto.AuthResponseDTO;
import com.pnae.application.dto.RegisterRequestDTO;
import com.pnae.application.dto.UsuarioResponseDTO;
import com.pnae.application.service.UsuarioService;
import com.pnae.domain.model.Usuario;
import com.pnae.infrastructure.config.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login e registro de usuários")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.senha()));

        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(dto.email());
        String token = jwtService.generateToken(usuario);
        return ResponseEntity.ok(AuthResponseDTO.of(token, UsuarioResponseDTO.from(usuario)));
    }

    @PostMapping("/registrar")
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(dto));
    }

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioResponseDTO> perfil(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(UsuarioResponseDTO.from(usuario));
    }
}
