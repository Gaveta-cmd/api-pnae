package com.pnae.api.controller;

import com.pnae.application.dto.AlimentoRequestDTO;
import com.pnae.application.dto.AlimentoResponseDTO;
import com.pnae.application.service.AlimentoService;
import com.pnae.domain.model.CategoriaAlimento;
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
@RequestMapping("/api/alimentos")
@RequiredArgsConstructor
public class AlimentoController {

    private final AlimentoService alimentoService;

    @GetMapping
    public ResponseEntity<List<AlimentoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(alimentoService.listarTodos());
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<AlimentoResponseDTO>> listarAtivos() {
        return ResponseEntity.ok(alimentoService.listarAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlimentoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alimentoService.buscarPorId(id));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<AlimentoResponseDTO>> listarPorCategoria(@PathVariable CategoriaAlimento categoria) {
        return ResponseEntity.ok(alimentoService.listarPorCategoria(categoria));
    }

    @GetMapping("/busca")
    public ResponseEntity<List<AlimentoResponseDTO>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(alimentoService.buscarPorNome(nome));
    }

    @PostMapping
    public ResponseEntity<AlimentoResponseDTO> criar(@RequestBody @Valid AlimentoRequestDTO dto) {
        AlimentoResponseDTO criado = alimentoService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlimentoResponseDTO> atualizar(@PathVariable Long id,
                                                          @RequestBody @Valid AlimentoRequestDTO dto) {
        return ResponseEntity.ok(alimentoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        alimentoService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
