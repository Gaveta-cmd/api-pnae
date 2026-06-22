package com.pnae.domain.repository;

import com.pnae.domain.model.Alimento;
import com.pnae.domain.model.CategoriaAlimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlimentoRepository extends JpaRepository<Alimento, Long> {

    List<Alimento> findByAtivoTrue();

    List<Alimento> findByCategoria(CategoriaAlimento categoria);

    List<Alimento> findByNomeContainingIgnoreCase(String nome);
}
