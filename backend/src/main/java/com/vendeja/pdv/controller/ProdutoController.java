package com.vendeja.pdv.controller;

import com.vendeja.pdv.model.Produto;
import com.vendeja.pdv.model.ProdutoCodigo;
import com.vendeja.pdv.repository.ProdutoCodigoRepository;
import com.vendeja.pdv.repository.ProdutoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;
    private final ProdutoCodigoRepository produtoCodigoRepository;
    
    private static final String UPLOAD_DIR = "uploads/produtos/";

    public ProdutoController(ProdutoRepository produtoRepository, ProdutoCodigoRepository produtoCodigoRepository) {
        this.produtoRepository = produtoRepository;
        this.produtoCodigoRepository = produtoCodigoRepository;
        
        // Criar diretório se não existir
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            System.err.println("Erro ao criar diretório de uploads: " + e.getMessage());
        }
    }

    @GetMapping
    public List<Produto> listar() {
        return produtoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar/{codigo}")
    public ResponseEntity<Produto> buscarPorCodigo(@PathVariable String codigo) {
        // Busca por código principal
        Optional<Produto> produto = produtoRepository.findByCodigo(codigo);
        if (produto.isPresent()) {
            return ResponseEntity.ok(produto.get());
        }
        
        // Busca por código alternativo (EAN)
        Optional<ProdutoCodigo> produtoCodigo = produtoCodigoRepository.findByCodigo(codigo);
        if (produtoCodigo.isPresent()) {
            return ResponseEntity.ok(produtoCodigo.get().getProduto());
        }
        
        return ResponseEntity.notFound().build();
    }

    // NOVO: Busca parcial com LIKE
    @GetMapping("/buscar-parcial/{codigo}")
    public ResponseEntity<Produto> buscarPorCodigoParcial(@PathVariable String codigo) {
        // Formata código com zeros à esquerda
        String codigoFormatado = String.format("%06d", Integer.parseInt(codigo));
        
        // Busca por código principal
        Optional<Produto> produto = produtoRepository.findByCodigo(codigoFormatado);
        if (produto.isPresent()) {
            return ResponseEntity.ok(produto.get());
        }
        
        // Busca por código alternativo (EAN)
        Optional<ProdutoCodigo> produtoCodigo = produtoCodigoRepository.findByCodigo(codigoFormatado);
        if (produtoCodigo.isPresent()) {
            return ResponseEntity.ok(produtoCodigo.get().getProduto());
        }
        
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Produto> criar(@RequestBody Produto produto) {
        // Formatar código com 6 dígitos
        if (produto.getCodigo() != null && !produto.getCodigo().isEmpty()) {
            try {
                int numero = Integer.parseInt(produto.getCodigo());
                produto.setCodigo(String.format("%06d", numero));
            } catch (NumberFormatException e) {
                // Mantém como está se não for número
            }
        }
        
        produto.setDataCadastro(LocalDateTime.now());
        Produto saved = produtoRepository.save(produto);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @RequestBody Produto produto) {
        if (!produtoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // Formatar código com 6 dígitos
        if (produto.getCodigo() != null && !produto.getCodigo().isEmpty()) {
            try {
                int numero = Integer.parseInt(produto.getCodigo());
                produto.setCodigo(String.format("%06d", numero));
            } catch (NumberFormatException e) {
                // Mantém como está se não for número
            }
        }
        
        produto.setId(id);
        Produto updated = produtoRepository.save(produto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!produtoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        produtoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Upload de foto do produto
    @PostMapping("/{id}/foto")
    public ResponseEntity<?> uploadFoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Optional<Produto> optProduto = produtoRepository.findById(id);
            if (!optProduto.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Produto produto = optProduto.get();
            
            // Gerar nome único para o arquivo
            String filename = UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());
            Path filepath = Paths.get(UPLOAD_DIR + filename);
            
            // Salvar arquivo
            Files.write(filepath, file.getBytes());
            
            // Atualizar caminho no produto
            produto.setFotoPath(filename);
            produtoRepository.save(produto);
            
            return ResponseEntity.ok().body("{\"fotoPath\": \"" + filename + "\"}");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao fazer upload: " + e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDot = filename.lastIndexOf('.');
        return lastDot == -1 ? "" : filename.substring(lastDot);
    }
}
