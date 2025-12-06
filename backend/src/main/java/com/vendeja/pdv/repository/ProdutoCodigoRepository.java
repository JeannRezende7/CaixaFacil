package com.vendeja.pdv.repository;

import com.vendeja.pdv.model.ProdutoCodigo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdutoCodigoRepository extends JpaRepository<ProdutoCodigo, Long> {
    Optional<ProdutoCodigo> findByCodigo(String codigo);
    void deleteByProdutoId(Long produtoId);
}
