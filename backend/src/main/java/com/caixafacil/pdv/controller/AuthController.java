package com.caixafacil.pdv.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.caixafacil.pdv.repository.UsuarioRepository;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UsuarioRepository usuarioRepository;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String login = credentials.get("login");
        String senha = credentials.get("senha");
        
        return usuarioRepository.findByLoginAndSenha(login, senha)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(401).build());
    }
}
