package com.caixafacil.pdv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.caixafacil.pdv.model.MovimentacaoEstoque;

import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    List<MovimentacaoEstoque> findByProdutoIdOrderByDataHoraDesc(Long produtoId);
}
