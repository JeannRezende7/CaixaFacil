package com.vendeja.pdv.controller;

import com.vendeja.pdv.dto.VendaDTO;
import com.vendeja.pdv.model.Venda;
import com.vendeja.pdv.repository.VendaRepository;
import com.vendeja.pdv.service.VendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vendas")
@RequiredArgsConstructor
public class VendaController {
    
    private final VendaRepository vendaRepository;
    private final VendaService vendaService;
    
    @GetMapping
    public List<Venda> listar() {
        return vendaRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Venda> buscarPorId(@PathVariable Long id) {
        return vendaRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/documento/{numero}")
    public ResponseEntity<Venda> buscarPorNumeroDocumento(@PathVariable Long numero) {
        return vendaRepository.findByNumeroDocumento(numero)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody VendaDTO vendaDTO) {
        try {
            System.out.println("Recebendo venda: " + vendaDTO);
            Venda venda = vendaService.salvar(vendaDTO);
            return ResponseEntity.ok(venda);
        } catch (Exception e) {
            System.err.println("ERRO ao criar venda: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, String> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            erro.put("tipo", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }
    }
}
