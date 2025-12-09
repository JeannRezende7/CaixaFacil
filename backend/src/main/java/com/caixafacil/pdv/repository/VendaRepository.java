package com.caixafacil.pdv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.caixafacil.pdv.model.Venda;

import java.util.Optional;

public interface VendaRepository extends JpaRepository<Venda, Long> {
    Optional<Venda> findByNumeroDocumento(Long numeroDocumento);
    
    @Query("SELECT COALESCE(MAX(v.numeroDocumento), 0) FROM Venda v")
    Long findMaxNumeroDocumento();
}
