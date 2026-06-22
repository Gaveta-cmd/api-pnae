package com.pnae.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tb_escola")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Escola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Valid
    @Embedded
    private Endereco endereco;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoEscola tipo;

    @Min(1)
    @Column(name = "capacidade_alunos", nullable = false)
    private Integer capacidadeAlunos;

    @Builder.Default
    @Column(name = "ativa", nullable = false)
    private boolean ativa = true;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @PrePersist
    private void prePersist() {
        this.dataCadastro = LocalDateTime.now();
        this.ativa = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Escola escola)) return false;
        return Objects.equals(id, escola.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
