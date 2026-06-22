package com.pnae.api.controller;

import com.pnae.application.dto.EscolaRequestDTO;
import com.pnae.application.dto.EscolaResponseDTO;
import com.pnae.application.service.EscolaService;
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
public class EscolaController {

    private final EscolaService escolaService;

    @GetMapping
    public ResponseEntity<List<EscolaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(escolaService.listarTodas());
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<EscolaResponseDTO>> listarAtivas() {
        return ResponseEntity.ok(escolaService.listarAtivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EscolaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(escolaService.buscarPorId(id));
    }

    @GetMapping("/busca")
    public ResponseEntity<List<EscolaResponseDTO>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(escolaService.buscarPorNome(nome));
    }

    @PostMapping
    public ResponseEntity<EscolaResponseDTO> criar(@RequestBody @Valid EscolaRequestDTO dto) {
        EscolaResponseDTO criada = escolaService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criada.id())
                .toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EscolaResponseDTO> atualizar(@PathVariable Long id,
                                                        @RequestBody @Valid EscolaRequestDTO dto) {
        return ResponseEntity.ok(escolaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        escolaService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
