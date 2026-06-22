package com.pnae.application.dto;

import com.pnae.domain.model.Aluno;
import com.pnae.domain.model.FaixaEtaria;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AlunoResponseDTO(
        Long id,
        String nome,
        LocalDate dataNascimento,
        String cpf,
        String matricula,
        EscolaResumoDTO escola,
        FaixaEtaria faixaEtaria,
        String descricaoFaixaEtaria,
        boolean ativo,
        LocalDateTime dataCadastro
) {
    public static AlunoResponseDTO from(Aluno aluno) {
        FaixaEtaria faixa = aluno.getFaixaEtaria();
        return new AlunoResponseDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getDataNascimento(),
                aluno.getCpf(),
                aluno.getMatricula(),
                EscolaResumoDTO.from(aluno.getEscola()),
                faixa,
                faixa.getDescricao(),
                aluno.isAtivo(),
                aluno.getDataCadastro()
        );
    }
}
