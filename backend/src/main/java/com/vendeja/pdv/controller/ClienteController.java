package com.vendeja.pdv.controller;

import com.vendeja.pdv.model.Cliente;
import com.vendeja.pdv.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {
    
    private final ClienteRepository clienteRepository;
    
    @GetMapping
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        return clienteRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Cliente> buscarPorCodigo(@PathVariable String codigo) {
        return clienteRepository.findByCodigo(codigo)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Cliente> criar(@RequestBody Cliente cliente) {
        // Aplicar padding de 6 dígitos no código
        if (cliente.getCodigo() != null && !cliente.getCodigo().isEmpty()) {
            try {
                int numero = Integer.parseInt(cliente.getCodigo());
                cliente.setCodigo(String.format("%06d", numero));
            } catch (NumberFormatException e) {
                // Se não for número, mantém como está
            }
        }
        
        cliente.setDataCadastro(LocalDateTime.now());
        return ResponseEntity.ok(clienteRepository.save(cliente));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        if (!clienteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // Aplicar padding de 6 dígitos no código
        if (cliente.getCodigo() != null && !cliente.getCodigo().isEmpty()) {
            try {
                int numero = Integer.parseInt(cliente.getCodigo());
                cliente.setCodigo(String.format("%06d", numero));
            } catch (NumberFormatException e) {
                // Se não for número, mantém como está
            }
        }
        
        cliente.setId(id);
        return ResponseEntity.ok(clienteRepository.save(cliente));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!clienteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        clienteRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
