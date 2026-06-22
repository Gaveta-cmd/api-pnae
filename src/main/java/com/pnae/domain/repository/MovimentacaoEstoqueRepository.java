package com.pnae.domain.repository;

import com.pnae.domain.model.MovimentacaoEstoque;
import com.pnae.domain.model.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    List<MovimentacaoEstoque> findByEstoqueItemId(Long estoqueItemId);

    List<MovimentacaoEstoque> findByDataMovimentacaoBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query(value = """
            SELECT a.nome AS alimento_nome,
                   SUM(me.quantidade_kg) AS quantidade_consumida,
                   SUM(me.quantidade_kg * a.custo_por_kg) AS custo_total
            FROM tb_movimentacao_estoque me
            JOIN tb_estoque est ON me.estoque_item_id = est.id
            JOIN tb_alimento a ON est.alimento_id = a.id
            WHERE est.escola_id = :escolaId
              AND me.tipo = 'SAIDA'
              AND MONTH(me.data_movimentacao) = :mes
              AND YEAR(me.data_movimentacao) = :ano
            GROUP BY a.nome, a.custo_por_kg
            ORDER BY custo_total DESC
            """, nativeQuery = true)
    List<Object[]> calcularConsumoMensal(
            @Param("escolaId") Long escolaId,
            @Param("mes") Integer mes,
            @Param("ano") Integer ano);
}
