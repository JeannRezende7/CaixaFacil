package com.vendeja.pdv.repository;

import com.vendeja.pdv.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCodigo(String codigo);
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
    List<Cliente> findByAtivoTrue();
}
