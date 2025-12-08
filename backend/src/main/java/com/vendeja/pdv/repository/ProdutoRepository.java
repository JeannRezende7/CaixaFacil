package com.vendeja.pdv.repository;

import com.vendeja.pdv.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // --------------------------------------------
    // BUSCAR POR CÓDIGO EXATO
    // --------------------------------------------
    Produto findByCodigo(String codigo);

    // --------------------------------------------
    // BUSCAR PRODUTO POR CÓDIGOS ALTERNATIVOS
    // --------------------------------------------
    @Query("SELECT p FROM Produto p LEFT JOIN p.codigosAlternativos c " +
           "WHERE p.codigo = :codigo OR c.codigo = :codigo")
    Produto findByCodigoOrCodigoAlternativo(@Param("codigo") String codigo);

    // --------------------------------------------
    // BUSCA PARCIAL (USADA PELO PDV)
    // --------------------------------------------
    @Query("""
           SELECT DISTINCT p FROM Produto p
           LEFT JOIN p.codigosAlternativos c
           WHERE LOWER(p.descricao) LIKE LOWER(CONCAT('%', :texto, '%'))
              OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :texto, '%'))
              OR LOWER(c.codigo) LIKE LOWER(CONCAT('%', :texto, '%'))
           """)
    List<Produto> buscarParcial(@Param("texto") String texto);

    // --------------------------------------------
    // BUSCAR POR DESCRIÇÃO (opcional)
    // --------------------------------------------
    List<Produto> findByDescricaoContainingIgnoreCase(String descricao);

    // --------------------------------------------
    // APENAS ATIVOS
    // --------------------------------------------
    List<Produto> findByAtivoTrue();
}
