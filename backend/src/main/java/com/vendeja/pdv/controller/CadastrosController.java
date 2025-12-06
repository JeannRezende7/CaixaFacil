package com.vendeja.pdv.controller;

import com.vendeja.pdv.model.Categoria;
import com.vendeja.pdv.model.FormaPagamento;
import com.vendeja.pdv.model.Usuario;
import com.vendeja.pdv.repository.CategoriaRepository;
import com.vendeja.pdv.repository.FormaPagamentoRepository;
import com.vendeja.pdv.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CadastrosController {

    private final CategoriaRepository categoriaRepository;
    private final FormaPagamentoRepository formaPagamentoRepository;
    private final UsuarioRepository usuarioRepository;

    // Categorias
    @GetMapping("/categorias")
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    @PostMapping("/categorias")
    public Categoria criarCategoria(@RequestBody Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    // Formas de Pagamento
    @GetMapping("/formas-pagamento")
    public List<FormaPagamento> listarFormasPagamento() {
        return formaPagamentoRepository.findAll();
    }

    @PostMapping("/formas-pagamento")
    public FormaPagamento criarFormaPagamento(@RequestBody FormaPagamento forma) {
        return formaPagamentoRepository.save(forma);
    }

    @GetMapping("/formas-pagamento/categoria/{categoria}")
    public ResponseEntity<List<FormaPagamento>> buscarFormasPagamentoCategoria(
            @PathVariable String categoria) {
        List<FormaPagamento> todasFormas = formaPagamentoRepository.findAll();
        List<FormaPagamento> formasFiltradas = todasFormas.stream()
                .filter(fp -> categoria.equalsIgnoreCase(fp.getCategoria()) && fp.getAtivo())
                .collect(Collectors.toList());
        return ResponseEntity.ok(formasFiltradas);
    }

    // Usuários
    @GetMapping("/usuarios")
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @PostMapping("/usuarios")
    public Usuario criarUsuario(@RequestBody Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        usuario.setId(id);
        return ResponseEntity.ok(usuarioRepository.save(usuario));
    }
}
