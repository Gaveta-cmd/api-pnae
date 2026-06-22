package com.pnae.domain.repository;

import com.pnae.domain.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    List<Aluno> findByEscolaId(Long escolaId);

    List<Aluno> findByAtivoTrue();

    Optional<Aluno> findByMatricula(String matricula);

    List<Aluno> findByNomeContainingIgnoreCase(String nome);

    long countByEscolaId(Long escolaId);

    long countByEscolaIdAndAtivoTrue(Long escolaId);
}
