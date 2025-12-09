package com.caixafacil.pdv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.caixafacil.pdv.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCodigo(String codigo);
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
    List<Cliente> findByAtivoTrue();
}
