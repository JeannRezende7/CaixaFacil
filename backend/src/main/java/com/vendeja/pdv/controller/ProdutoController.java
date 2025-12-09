package com.vendeja.pdv.controller;

import com.vendeja.pdv.model.Produto;
import com.vendeja.pdv.repository.ProdutoRepository;
import com.vendeja.pdv.service.ProdutoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ProdutoService produtoService;

    // ============================================================
    // LISTAR / BUSCAR
    // ============================================================

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

    // Buscar por código EXATO — DEPENDÊNCIA DO FRONT
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Produto> buscarPorCodigo(@PathVariable String codigo) {
        Produto produto = produtoRepository.findByCodigo(codigo);
        if (produto == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(produto);
    }

    // Buscar parcial — DEPENDÊNCIA PRINCIPAL DO PDV
    @GetMapping("/buscar-parcial/{texto}")
    public List<Produto> buscarParcial(@PathVariable String texto) {
        return produtoRepository.buscarParcial(texto);
    }

    // Buscar por query param — usado em telas de pesquisa
    @GetMapping("/buscar")
    public List<Produto> buscar(@RequestParam String q) {
        return produtoRepository.buscarParcial(q);
    }

    // ============================================================
    // CRUD
    // ============================================================

    @PostMapping
    public Produto criar(@RequestBody Produto produto) {
        return produtoRepository.save(produto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @RequestBody Produto produto) {
        return produtoRepository.findById(id)
                .map(p -> {
                    p.setCodigo(produto.getCodigo());
                    p.setDescricao(produto.getDescricao());
                    p.setUnidade(produto.getUnidade());
                    p.setCategoria(produto.getCategoria());
                    p.setPrecoVenda(produto.getPrecoVenda());
                    p.setPrecoCusto(produto.getPrecoCusto());
                    p.setEstoque(produto.getEstoque());
                    p.setEstoqueMinimo(produto.getEstoqueMinimo());
                    p.setControlarEstoque(produto.getControlarEstoque());
                    p.setAtivo(produto.getAtivo());
                    p.setObservacoes(produto.getObservacoes());
                    return ResponseEntity.ok(produtoRepository.save(p));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .map(p -> {
                    produtoRepository.delete(p);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ============================================================
    // UPLOAD DE FOTO
    // ============================================================

    @PostMapping("/{id}/foto")
    public ResponseEntity<Map<String, String>> uploadFoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            Optional<Produto> produtoOpt = produtoRepository.findById(id);
            if (produtoOpt.isEmpty())
                return ResponseEntity.notFound().build();

            Produto produto = produtoOpt.get();

            String uploadDir = "uploads/produtos/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";

            String filename = "produto_" + id + "_" + System.currentTimeMillis() + extension;

            // Remove foto antiga
            if (produto.getFotoPath() != null && !produto.getFotoPath().isEmpty()) {
                try {
                    Path oldFile = uploadPath.resolve(produto.getFotoPath());
                    Files.deleteIfExists(oldFile);
                } catch (Exception ignored) {
                }
            }

            // Salva nova foto
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            produto.setFotoPath(filename);
            produtoRepository.save(produto);

            Map<String, String> response = new HashMap<>();
            response.put("fotoPath", filename);
            response.put("message", "Foto enviada com sucesso");

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao salvar foto: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @DeleteMapping("/{id}/foto")
    public ResponseEntity<?> deletarFoto(@PathVariable Long id) {
        try {
            Optional<Produto> produtoOpt = produtoRepository.findById(id);
            if (produtoOpt.isEmpty())
                return ResponseEntity.notFound().build();

            Produto produto = produtoOpt.get();

            if (produto.getFotoPath() != null && !produto.getFotoPath().isEmpty()) {
                String uploadDir = "uploads/produtos/";
                Path filePath = Paths.get(uploadDir).resolve(produto.getFotoPath());
                Files.deleteIfExists(filePath);

                produto.setFotoPath(null);
                produtoRepository.save(produto);
            }

            return ResponseEntity.ok().build();

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao deletar foto");
        }
    }

    // ============= ESTOQUE BAIXO =============
    @GetMapping("/estoque-baixo")
    public ResponseEntity<List<Produto>> getEstoqueBaixo() {
        List<Produto> produtos = produtoService.buscarEstoqueBaixo();
        return ResponseEntity.ok(produtos);
    }

    // ============= ESTOQUE ALERTA (20%) =============
    @GetMapping("/estoque-alerta")
    public ResponseEntity<List<Produto>> getEstoqueAlerta() {
        List<Produto> produtos = produtoService.buscarEstoqueAlerta();
        return ResponseEntity.ok(produtos);
    }

}
