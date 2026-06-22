package com.pnae.application.dto;

public record AuthResponseDTO(
        String token,
        String tipo,
        String expiraEm,
        UsuarioResponseDTO usuario
) {
    public static AuthResponseDTO of(String token, UsuarioResponseDTO usuario) {
        return new AuthResponseDTO(token, "Bearer", "24h", usuario);
    }
}
