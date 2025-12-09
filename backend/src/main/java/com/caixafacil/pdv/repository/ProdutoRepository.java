package com.caixafacil.pdv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.caixafacil.pdv.model.Produto;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // =========================================================================
    // BUSCA POR CÓDIGO PRINCIPAL
    // =========================================================================
    Produto findByCodigo(String codigo);

    // =========================================================================
    // BUSCA POR CÓDIGO PRINCIPAL OU CÓDIGOS ALTERNATIVOS
    // =========================================================================
    @Query("""
        SELECT p FROM Produto p
        LEFT JOIN p.codigosAlternativos c
        WHERE p.codigo = :codigo OR c.codigo = :codigo
    """)
    Produto findByCodigoOrCodigoAlternativo(@Param("codigo") String codigo);

    // =========================================================================
    // BUSCA PARCIAL — USADA NO PDV
    // =========================================================================
    @Query("""
        SELECT DISTINCT p FROM Produto p
        LEFT JOIN p.codigosAlternativos c
        WHERE LOWER(p.descricao) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(c.codigo) LIKE LOWER(CONCAT('%', :texto, '%'))
    """)
    List<Produto> buscarParcial(@Param("texto") String texto);

    // =========================================================================
    // BUSCA POR DESCRIÇÃO CONTENDO TEXTO
    // =========================================================================
    List<Produto> findByDescricaoContainingIgnoreCase(String descricao);

    // =========================================================================
    // SOMENTE PRODUTOS ATIVOS
    // =========================================================================
    List<Produto> findByAtivoTrue();

    // =========================================================================
    // ESTOQUE BAIXO — abaixo do mínimo
    // =========================================================================
    @Query("""
        SELECT p FROM Produto p
        WHERE p.estoque <= p.estoqueMinimo
        ORDER BY p.estoque ASC
    """)
    List<Produto> findEstoqueBaixo();

    // =========================================================================
    // ESTOQUE EM ALERTA — entre mínimo e até 20% acima
    // =========================================================================
    @Query("""
        SELECT p FROM Produto p
        WHERE p.estoque > p.estoqueMinimo
          AND p.estoque <= (p.estoqueMinimo * 1.2)
        ORDER BY p.estoque ASC
    """)
    List<Produto> findEstoqueAlerta();
}
