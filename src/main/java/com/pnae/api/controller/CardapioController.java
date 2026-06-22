package com.pnae.api.controller;

import com.pnae.application.dto.CardapioRequestDTO;
import com.pnae.application.dto.CardapioResponseDTO;
import com.pnae.application.dto.ItemCardapioRequestDTO;
import com.pnae.application.dto.ItemCardapioResponseDTO;
import com.pnae.application.dto.ResumoNutricionalDTO;
import com.pnae.application.service.CardapioService;
import com.pnae.domain.model.DiaSemana;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cardapios")
@RequiredArgsConstructor
public class CardapioController {

    private final CardapioService cardapioService;

    @PostMapping
    public ResponseEntity<CardapioResponseDTO> criarCardapio(@RequestBody @Valid CardapioRequestDTO dto) {
        CardapioResponseDTO criado = cardapioService.criarCardapio(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardapioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cardapioService.buscarPorId(id));
    }

    @GetMapping("/escola/{escolaId}")
    public ResponseEntity<List<CardapioResponseDTO>> listarPorEscola(@PathVariable Long escolaId) {
        return ResponseEntity.ok(cardapioService.listarPorEscola(escolaId));
    }

    @GetMapping("/escola/{escolaId}/semana")
    public ResponseEntity<CardapioResponseDTO> buscarPorEscolaESemana(
            @PathVariable Long escolaId,
            @RequestParam Integer semana,
            @RequestParam Integer ano) {
        return ResponseEntity.ok(cardapioService.buscarPorEscolaESemana(escolaId, semana, ano));
    }

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

    @DeleteMapping("/{id}/itens/{itemId}")
    public ResponseEntity<Void> removerItem(@PathVariable Long id, @PathVariable Long itemId) {
        cardapioService.removerItem(id, itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/nutricional")
    public ResponseEntity<ResumoNutricionalDTO> resumoNutricional(@PathVariable Long id) {
        return ResponseEntity.ok(cardapioService.calcularResumoNutricional(id));
    }

    @GetMapping("/{id}/nutricional/{dia}")
    public ResponseEntity<ResumoNutricionalDTO> resumoNutricionalPorDia(
            @PathVariable Long id,
            @PathVariable DiaSemana dia) {
        return ResponseEntity.ok(cardapioService.calcularResumoNutricionalPorDia(id, dia));
    }
}
