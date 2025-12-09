package com.caixafacil.pdv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.caixafacil.pdv.model.ProdutoCodigo;

import java.util.Optional;

@Repository
public interface ProdutoCodigoRepository extends JpaRepository<ProdutoCodigo, Long> {
    Optional<ProdutoCodigo> findByCodigo(String codigo);
    void deleteByProdutoId(Long produtoId);
}
