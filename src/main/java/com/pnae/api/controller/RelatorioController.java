package com.pnae.api.controller;

import com.pnae.application.dto.RelatorioCustoAlunoDTO;
import com.pnae.application.dto.RelatorioConsumoDTO;
import com.pnae.application.dto.RelatorioEscolasDTO;
import com.pnae.application.dto.RelatorioEstoqueDTO;
import com.pnae.application.dto.RelatorioNutricionalDTO;
import com.pnae.application.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/escolas")
    public ResponseEntity<RelatorioEscolasDTO> escolas() {
        return ResponseEntity.ok(relatorioService.relatorioEscolasResumido());
    }

    @GetMapping("/nutricional/cardapio/{id}")
    public ResponseEntity<RelatorioNutricionalDTO> nutricionalCardapio(@PathVariable Long id) {
        return ResponseEntity.ok(relatorioService.relatorioNutricionalCardapio(id));
    }

    @GetMapping("/estoque/escola/{escolaId}")
    public ResponseEntity<RelatorioEstoqueDTO> estoqueEscola(@PathVariable Long escolaId) {
        return ResponseEntity.ok(relatorioService.relatorioEstoqueEscola(escolaId));
    }

    @GetMapping("/consumo/escola/{escolaId}")
    public ResponseEntity<RelatorioConsumoDTO> consumoMensal(
            @PathVariable Long escolaId,
            @RequestParam Integer mes,
            @RequestParam Integer ano) {
        return ResponseEntity.ok(relatorioService.relatorioConsumoMensal(escolaId, mes, ano));
    }

    @GetMapping("/custo-aluno/escola/{escolaId}")
    public ResponseEntity<RelatorioCustoAlunoDTO> custoPorAluno(
            @PathVariable Long escolaId,
            @RequestParam Integer mes,
            @RequestParam Integer ano) {
        return ResponseEntity.ok(relatorioService.relatorioCustoPorAluno(escolaId, mes, ano));
    }
}
