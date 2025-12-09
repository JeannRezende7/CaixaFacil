package com.caixafacil.pdv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.caixafacil.pdv.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByLogin(String login);
    Optional<Usuario> findByLoginAndSenha(String login, String senha);
}
