package com.caixafacil.pdv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.caixafacil.pdv.model.Categoria;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByAtivoTrue();
}
