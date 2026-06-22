package com.pnae.application.dto;

import com.pnae.domain.model.Role;
import com.pnae.domain.model.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        Role role,
        boolean ativo,
        LocalDateTime dataCriacao
) {
    public static UsuarioResponseDTO from(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.isAtivo(),
                usuario.getDataCriacao()
        );
    }
}
