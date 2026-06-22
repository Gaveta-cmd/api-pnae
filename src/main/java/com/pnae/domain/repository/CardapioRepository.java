package com.pnae.domain.repository;

import com.pnae.domain.model.Cardapio;
import com.pnae.domain.model.StatusCardapio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardapioRepository extends JpaRepository<Cardapio, Long> {

    List<Cardapio> findByEscolaId(Long escolaId);

    Optional<Cardapio> findByEscolaIdAndSemanaAndAno(Long escolaId, Integer semana, Integer ano);

    List<Cardapio> findByStatus(StatusCardapio status);

    List<Cardapio> findByEscolaIdAndAno(Long escolaId, Integer ano);
}
