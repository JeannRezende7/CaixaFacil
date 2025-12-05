package com.vendeja.pdv.repository;

import com.vendeja.pdv.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Optional<Produto> findByCodigo(String codigo);
    List<Produto> findByDescricaoContainingIgnoreCase(String descricao);
    List<Produto> findByAtivoTrue();
    
    @Query("SELECT p FROM Produto p LEFT JOIN p.codigosAlternativos c " +
           "WHERE p.codigo = :codigo OR c.codigo = :codigo")
    Optional<Produto> findByCodigoOrCodigoAlternativo(@Param("codigo") String codigo);
}
