package com.caixafacil.pdv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.caixafacil.pdv.model.Caixa;
import com.caixafacil.pdv.model.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaixaRepository extends JpaRepository<Caixa, Long> {
    
    @Query("SELECT c FROM Caixa c WHERE c.status = 'ABERTO' ORDER BY c.dataHoraAbertura DESC")
    Optional<Caixa> findCaixaAberto();
    
    @Query("SELECT c FROM Caixa c WHERE c.usuario.id = ?1 AND c.status = 'ABERTO'")
    Optional<Caixa> findCaixaAbertoByUsuario(Long usuarioId);
    
    List<Caixa> findByUsuarioOrderByDataHoraAberturaDesc(Usuario usuario);
    
    List<Caixa> findAllByOrderByDataHoraAberturaDesc();
}
