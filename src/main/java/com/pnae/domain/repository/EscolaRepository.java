package com.pnae.domain.repository;

import com.pnae.domain.model.Escola;
import com.pnae.domain.model.TipoEscola;
import com.pnae.domain.model.UF;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EscolaRepository extends JpaRepository<Escola, Long> {

    List<Escola> findByAtivaTrue();

    List<Escola> findByTipo(TipoEscola tipo);

    List<Escola> findByEnderecoEstado(UF estado);

    List<Escola> findByNomeContainingIgnoreCase(String nome);
}
