package com.pnae.api.controller;

import com.pnae.application.dto.CardapioRequestDTO;
import com.pnae.application.dto.CardapioResponseDTO;
import com.pnae.application.dto.ItemCardapioRequestDTO;
import com.pnae.application.dto.ItemCardapioResponseDTO;
import com.pnae.application.dto.ResumoNutricionalDTO;
import com.pnae.application.dto.ValidacaoNutricionalDTO;
import com.pnae.application.service.CardapioService;
import com.pnae.domain.model.DiaSemana;
import com.pnae.domain.model.FaixaEtaria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cardapios")
@RequiredArgsConstructor
@Tag(name = "Cardápios", description = "Gestão de cardápios e validação nutricional")
@SecurityRequirement(name = "bearerAuth")
public class CardapioController {

    private final CardapioService cardapioService;

    @Operation(summary = "Criar cardápio", description = "Cria um novo cardápio semanal para uma escola")
    @ApiResponse(responseCode = "201", description = "Cardápio criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "422", description = "Escola inativa ou cardápio duplicado")
    @PostMapping
    public ResponseEntity<CardapioResponseDTO> criarCardapio(@RequestBody @Valid CardapioRequestDTO dto) {
        CardapioResponseDTO criado = cardapioService.criarCardapio(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @Operation(summary = "Buscar cardápio por ID")
    @ApiResponse(responseCode = "200", description = "Cardápio encontrado")
    @ApiResponse(responseCode = "404", description = "Cardápio não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<CardapioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cardapioService.buscarPorId(id));
    }

    @Operation(summary = "Listar cardápios por escola")
    @GetMapping("/escola/{escolaId}")
    public ResponseEntity<List<CardapioResponseDTO>> listarPorEscola(@PathVariable Long escolaId) {
        return ResponseEntity.ok(cardapioService.listarPorEscola(escolaId));
    }

    @Operation(summary = "Buscar cardápio por escola e semana")
    @GetMapping("/escola/{escolaId}/semana")
    public ResponseEntity<CardapioResponseDTO> buscarPorEscolaESemana(
            @PathVariable Long escolaId,
            @RequestParam Integer semana,
            @RequestParam Integer ano) {
        return ResponseEntity.ok(cardapioService.buscarPorEscolaESemana(escolaId, semana, ano));
    }

    @Operation(summary = "Adicionar item ao cardápio")
    @ApiResponse(responseCode = "201", description = "Item adicionado com sucesso")
    @ApiResponse(responseCode = "422", description = "Cardápio aprovado não pode ser editado")
    @PostMapping("/{id}/itens")
    public ResponseEntity<ItemCardapioResponseDTO> adicionarItem(
            @PathVariable Long id,
            @RequestBody @Valid ItemCardapioRequestDTO dto) {
        ItemCardapioResponseDTO item = cardapioService.adicionarItem(id, dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{itemId}")
                .buildAndExpand(item.id())
                .toUri();
        return ResponseEntity.created(location).body(item);
    }

    @Operation(summary = "Remover item do cardápio")
    @DeleteMapping("/{id}/itens/{itemId}")
    public ResponseEntity<Void> removerItem(@PathVariable Long id, @PathVariable Long itemId) {
        cardapioService.removerItem(id, itemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Calcular resumo nutricional do cardápio")
    @GetMapping("/{id}/nutricional")
    public ResponseEntity<ResumoNutricionalDTO> resumoNutricional(@PathVariable Long id) {
        return ResponseEntity.ok(cardapioService.calcularResumoNutricional(id));
    }

    @Operation(summary = "Calcular resumo nutricional por dia")
    @GetMapping("/{id}/nutricional/{dia}")
    public ResponseEntity<ResumoNutricionalDTO> resumoNutricionalPorDia(
            @PathVariable Long id,
            @PathVariable DiaSemana dia) {
        return ResponseEntity.ok(cardapioService.calcularResumoNutricionalPorDia(id, dia));
    }

    @Operation(summary = "Validar cardápio por faixa etária")
    @PostMapping("/{id}/validar")
    public ResponseEntity<Map<FaixaEtaria, List<ValidacaoNutricionalDTO>>> validarParaFaixa(
            @PathVariable Long id,
            @RequestParam FaixaEtaria faixa) {
        return ResponseEntity.ok(cardapioService.validarParaFaixa(id, faixa));
    }

    @Operation(summary = "Validar cardápio para todas as faixas etárias da escola")
    @PostMapping("/{id}/validar-escola")
    public ResponseEntity<Map<FaixaEtaria, List<ValidacaoNutricionalDTO>>> validarParaEscola(@PathVariable Long id) {
        return ResponseEntity.ok(cardapioService.validarParaEscola(id));
    }

    @Operation(summary = "Aprovar cardápio validado")
    @ApiResponse(responseCode = "200", description = "Cardápio aprovado")
    @ApiResponse(responseCode = "422", description = "Cardápio não está em status VALIDADO")
    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<CardapioResponseDTO> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(cardapioService.aprovarCardapio(id));
    }

    @Operation(summary = "Rejeitar cardápio")
    @PatchMapping("/{id}/rejeitar")
    public ResponseEntity<CardapioResponseDTO> rejeitar(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(cardapioService.rejeitarCardapio(id, body.get("motivo")));
    }

    @Operation(summary = "Consultar status do cardápio")
    @GetMapping("/{id}/status")
    public ResponseEntity<CardapioResponseDTO> consultarStatus(@PathVariable Long id) {
        return ResponseEntity.ok(cardapioService.buscarPorId(id));
    }
}
