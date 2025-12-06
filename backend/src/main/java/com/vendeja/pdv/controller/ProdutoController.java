package com.vendeja.pdv.controller;

import com.vendeja.pdv.model.Produto;
import com.vendeja.pdv.model.ProdutoCodigo;
import com.vendeja.pdv.repository.CategoriaRepository;
import com.vendeja.pdv.repository.ProdutoCodigoRepository;
import com.vendeja.pdv.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {
    
    private final ProdutoRepository produtoRepository;
    private final ProdutoCodigoRepository produtoCodigoRepository;
    private final CategoriaRepository categoriaRepository;
    
    private static final String UPLOAD_DIR = "uploads/produtos/";
    
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
    
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Produto> buscarPorCodigo(@PathVariable String codigo) {
        // Buscar pelo código principal
        Produto produto = produtoRepository.findByCodigo(codigo).orElse(null);
        
        // Se não encontrou, buscar pelos códigos alternativos
        if (produto == null) {
            ProdutoCodigo produtoCodigo = produtoCodigoRepository.findByCodigo(codigo).orElse(null);
            if (produtoCodigo != null) {
                produto = produtoCodigo.getProduto();
            }
        }
        
        if (produto != null) {
            return ResponseEntity.ok(produto);
        }
        
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<Produto> criar(@RequestBody Produto produto) {
        // Aplicar padding de 6 dígitos no código
        if (produto.getCodigo() != null && !produto.getCodigo().isEmpty()) {
            try {
                int numero = Integer.parseInt(produto.getCodigo());
                produto.setCodigo(String.format("%06d", numero));
            } catch (NumberFormatException e) {
                // Se não for número, mantém como está
            }
        }
        
        produto.setDataCadastro(LocalDateTime.now());
        
        if (produto.getCategoria() != null && produto.getCategoria().getId() != null) {
            produto.setCategoria(categoriaRepository.findById(produto.getCategoria().getId()).orElse(null));
        }
        
        Produto produtoSalvo = produtoRepository.save(produto);
        
        // Salvar códigos alternativos
        if (produto.getCodigosAlternativos() != null) {
            for (ProdutoCodigo codigo : produto.getCodigosAlternativos()) {
                codigo.setProduto(produtoSalvo);
                produtoCodigoRepository.save(codigo);
            }
        }
        
        return ResponseEntity.ok(produtoSalvo);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @RequestBody Produto produto) {
        if (!produtoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // Aplicar padding de 6 dígitos no código
        if (produto.getCodigo() != null && !produto.getCodigo().isEmpty()) {
            try {
                int numero = Integer.parseInt(produto.getCodigo());
                produto.setCodigo(String.format("%06d", numero));
            } catch (NumberFormatException e) {
                // Se não for número, mantém como está
            }
        }
        
        produto.setId(id);
        
        if (produto.getCategoria() != null && produto.getCategoria().getId() != null) {
            produto.setCategoria(categoriaRepository.findById(produto.getCategoria().getId()).orElse(null));
        }
        
        // Remover códigos alternativos antigos
        produtoCodigoRepository.deleteByProdutoId(id);
        
        Produto produtoSalvo = produtoRepository.save(produto);
        
        // Salvar novos códigos alternativos
        if (produto.getCodigosAlternativos() != null) {
            for (ProdutoCodigo codigo : produto.getCodigosAlternativos()) {
                codigo.setProduto(produtoSalvo);
                produtoCodigoRepository.save(codigo);
            }
        }
        
        return ResponseEntity.ok(produtoSalvo);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!produtoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        produtoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/foto")
    public ResponseEntity<?> uploadFoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
            
            // Criar diretório se não existir
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // Gerar nome único para o arquivo
            String extension = "";
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;
            
            // Salvar arquivo
            Path filepath = Paths.get(UPLOAD_DIR + filename);
            Files.write(filepath, file.getBytes());
            
            // Atualizar produto
            produto.setFotoPath(filename);
            produtoRepository.save(produto);
            
            Map<String, String> response = new HashMap<>();
            response.put("fotoPath", filename);
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Erro ao fazer upload da foto: " + e.getMessage());
        }
    }
}
