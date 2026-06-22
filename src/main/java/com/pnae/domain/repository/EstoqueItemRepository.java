package com.pnae.domain.repository;

import com.pnae.domain.model.EstoqueItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EstoqueItemRepository extends JpaRepository<EstoqueItem, Long> {

    List<EstoqueItem> findByEscolaId(Long escolaId);

    List<EstoqueItem> findByAlimentoId(Long alimentoId);

    Optional<EstoqueItem> findByEscolaIdAndAlimentoIdAndLote(Long escolaId, Long alimentoId, String lote);

    List<EstoqueItem> findByEscolaIdAndDataValidadeBefore(Long escolaId, LocalDate data);

    List<EstoqueItem> findByEscolaIdAndDataValidadeBetween(Long escolaId, LocalDate inicio, LocalDate fim);

    @Query(value = """
            SELECT e.nome AS escola_nome,
                   SUM(est.quantidade_kg * a.custo_por_kg) AS custo_total,
                   COUNT(est.id) AS total_itens
            FROM tb_estoque est
            JOIN tb_alimento a ON est.alimento_id = a.id
            JOIN tb_escola e ON est.escola_id = e.id
            WHERE est.escola_id = :escolaId
            GROUP BY e.nome
            """, nativeQuery = true)
    List<Object[]> calcularCustoTotalPorEscola(@Param("escolaId") Long escolaId);
}
