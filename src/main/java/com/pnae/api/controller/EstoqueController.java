package com.pnae.api.controller;

import com.pnae.application.dto.EstoqueEntradaDTO;
import com.pnae.application.dto.EstoqueItemResponseDTO;
import com.pnae.application.dto.EstoqueMovimentacaoDTO;
import com.pnae.application.dto.MovimentacaoResponseDTO;
import com.pnae.application.service.EstoqueService;
import com.pnae.domain.model.Usuario;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
@RequiredArgsConstructor
@Tag(name = "Estoque", description = "Controle de estoque")
@SecurityRequirement(name = "bearerAuth")
public class EstoqueController {

    private final EstoqueService estoqueService;

    @PostMapping("/entrada")
    public ResponseEntity<EstoqueItemResponseDTO> registrarEntrada(
            @Valid @RequestBody EstoqueEntradaDTO dto,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(estoqueService.registrarEntrada(dto, usuario));
    }

    @PostMapping("/{id}/saida")
    public ResponseEntity<EstoqueItemResponseDTO> registrarSaida(
            @PathVariable Long id,
            @Valid @RequestBody EstoqueMovimentacaoDTO dto,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(estoqueService.registrarSaida(id, dto, usuario));
    }

    @PostMapping("/{id}/perda")
    public ResponseEntity<EstoqueItemResponseDTO> registrarPerda(
            @PathVariable Long id,
            @Valid @RequestBody EstoqueMovimentacaoDTO dto,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(estoqueService.registrarPerda(id, dto, usuario));
    }

    @GetMapping("/escola/{escolaId}")
    public ResponseEntity<List<EstoqueItemResponseDTO>> consultarPorEscola(@PathVariable Long escolaId) {
        return ResponseEntity.ok(estoqueService.consultarEstoquePorEscola(escolaId));
    }

    @GetMapping("/escola/{escolaId}/vencidos")
    public ResponseEntity<List<EstoqueItemResponseDTO>> consultarVencidos(@PathVariable Long escolaId) {
        return ResponseEntity.ok(estoqueService.consultarItensVencidos(escolaId));
    }

    @GetMapping("/escola/{escolaId}/alertas")
    public ResponseEntity<List<EstoqueItemResponseDTO>> consultarAlertasVencimento(
            @PathVariable Long escolaId,
            @RequestParam(defaultValue = "7") Integer dias) {
        return ResponseEntity.ok(estoqueService.consultarItensProximosVencimento(escolaId, dias));
    }

    @GetMapping("/{id}/movimentacoes")
    public ResponseEntity<List<MovimentacaoResponseDTO>> consultarMovimentacoes(@PathVariable Long id) {
        return ResponseEntity.ok(estoqueService.consultarMovimentacoes(id));
    }
}
