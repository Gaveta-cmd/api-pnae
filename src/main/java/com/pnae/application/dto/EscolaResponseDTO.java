package com.pnae.application.dto;

import com.pnae.domain.model.Escola;
import com.pnae.domain.model.TipoEscola;
import com.pnae.domain.model.UF;

import java.time.LocalDateTime;

public record EscolaResponseDTO(
        Long id,
        String nome,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        UF estado,
        String cep,
        TipoEscola tipo,
        Integer capacidadeAlunos,
        boolean ativa,
        LocalDateTime dataCadastro
) {
    public static EscolaResponseDTO from(Escola escola) {
        return new EscolaResponseDTO(
                escola.getId(),
                escola.getNome(),
                escola.getEndereco().getLogradouro(),
                escola.getEndereco().getNumero(),
                escola.getEndereco().getComplemento(),
                escola.getEndereco().getBairro(),
                escola.getEndereco().getCidade(),
                escola.getEndereco().getEstado(),
                escola.getEndereco().getCep(),
                escola.getTipo(),
                escola.getCapacidadeAlunos(),
                escola.isAtiva(),
                escola.getDataCadastro()
        );
    }
}
