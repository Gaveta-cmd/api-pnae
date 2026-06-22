package com.pnae.api.controller;

import com.pnae.application.dto.FornecedorRequestDTO;
import com.pnae.application.dto.FornecedorResponseDTO;
import com.pnae.application.service.FornecedorService;
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
@RequestMapping("/api/fornecedores")
@RequiredArgsConstructor
public class FornecedorController {

    private final FornecedorService fornecedorService;

    @GetMapping
    public ResponseEntity<List<FornecedorResponseDTO>> listar(
            @RequestParam(required = false) String nome) {
        if (nome != null && !nome.isBlank()) {
            return ResponseEntity.ok(fornecedorService.buscarPorNome(nome));
        }
        return ResponseEntity.ok(fornecedorService.listarAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(fornecedorService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<FornecedorResponseDTO> criar(@Valid @RequestBody FornecedorRequestDTO dto) {
        FornecedorResponseDTO criado = fornecedorService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FornecedorResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody FornecedorRequestDTO dto) {
        return ResponseEntity.ok(fornecedorService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        fornecedorService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
