package com.pnae.api.controller;

import com.pnae.application.dto.EscolaRequestDTO;
import com.pnae.application.dto.EscolaResponseDTO;
import com.pnae.application.service.EscolaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/escolas")
@RequiredArgsConstructor
@Tag(name = "Escolas", description = "Gestão de escolas")
@SecurityRequirement(name = "bearerAuth")
public class EscolaController {

    private final EscolaService escolaService;

    @Operation(summary = "Listar todas as escolas")
    @GetMapping
    public ResponseEntity<List<EscolaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(escolaService.listarTodas());
    }

    @Operation(summary = "Listar escolas ativas")
    @GetMapping("/ativas")
    public ResponseEntity<List<EscolaResponseDTO>> listarAtivas() {
        return ResponseEntity.ok(escolaService.listarAtivas());
    }

    @Operation(summary = "Buscar escola por ID")
    @ApiResponse(responseCode = "200", description = "Escola encontrada")
    @ApiResponse(responseCode = "404", description = "Escola não encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<EscolaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(escolaService.buscarPorId(id));
    }

    @Operation(summary = "Buscar escolas por nome")
    @GetMapping("/busca")
    public ResponseEntity<List<EscolaResponseDTO>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(escolaService.buscarPorNome(nome));
    }

    @Operation(summary = "Criar escola", description = "Cadastra uma nova escola no sistema")
    @ApiResponse(responseCode = "201", description = "Escola criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PostMapping
    public ResponseEntity<EscolaResponseDTO> criar(@RequestBody @Valid EscolaRequestDTO dto) {
        EscolaResponseDTO criada = escolaService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criada.id())
                .toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @Operation(summary = "Atualizar escola")
    @ApiResponse(responseCode = "200", description = "Escola atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Escola não encontrada")
    @PutMapping("/{id}")
    public ResponseEntity<EscolaResponseDTO> atualizar(@PathVariable Long id,
                                                        @RequestBody @Valid EscolaRequestDTO dto) {
        return ResponseEntity.ok(escolaService.atualizar(id, dto));
    }

    @Operation(summary = "Desativar escola")
    @ApiResponse(responseCode = "204", description = "Escola desativada")
    @ApiResponse(responseCode = "404", description = "Escola não encontrada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        escolaService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
