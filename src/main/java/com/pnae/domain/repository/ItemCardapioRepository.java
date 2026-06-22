package com.pnae.domain.repository;

import com.pnae.domain.model.DiaSemana;
import com.pnae.domain.model.ItemCardapio;
import com.pnae.domain.model.TipoRefeicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemCardapioRepository extends JpaRepository<ItemCardapio, Long> {

    List<ItemCardapio> findByCardapioId(Long cardapioId);

    List<ItemCardapio> findByCardapioIdAndDiaSemana(Long cardapioId, DiaSemana diaSemana);

    List<ItemCardapio> findByCardapioIdAndTipoRefeicao(Long cardapioId, TipoRefeicao tipoRefeicao);
}
