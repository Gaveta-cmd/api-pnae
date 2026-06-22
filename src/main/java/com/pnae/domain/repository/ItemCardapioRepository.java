package com.pnae.domain.repository;

import com.pnae.domain.model.DiaSemana;
import com.pnae.domain.model.ItemCardapio;
import com.pnae.domain.model.TipoRefeicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemCardapioRepository extends JpaRepository<ItemCardapio, Long> {

    List<ItemCardapio> findByCardapioId(Long cardapioId);

    List<ItemCardapio> findByCardapioIdAndDiaSemana(Long cardapioId, DiaSemana diaSemana);

    List<ItemCardapio> findByCardapioIdAndTipoRefeicao(Long cardapioId, TipoRefeicao tipoRefeicao);

    @Query(value = """
            SELECT ic.dia_semana,
                   AVG((a.calorias / 100.0) * ic.quantidade_gramas)     AS media_calorias,
                   AVG((a.proteinas / 100.0) * ic.quantidade_gramas)    AS media_proteinas,
                   AVG((a.carboidratos / 100.0) * ic.quantidade_gramas) AS media_carboidratos,
                   AVG((a.gorduras / 100.0) * ic.quantidade_gramas)     AS media_gorduras
            FROM tb_item_cardapio ic
            JOIN tb_alimento a ON ic.alimento_id = a.id
            WHERE ic.cardapio_id = :cardapioId
            GROUP BY ic.dia_semana
            ORDER BY ic.dia_semana
            """, nativeQuery = true)
    List<Object[]> calcularMediaNutricionalPorDia(@Param("cardapioId") Long cardapioId);
}
