package com.vendeja.pdv.controller;

import com.vendeja.pdv.model.MovimentacaoEstoque;
import com.vendeja.pdv.model.Produto;
import com.vendeja.pdv.service.EstoqueService;
import com.vendeja.pdv.repository.MovimentacaoEstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/estoque")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @PostMapping("/entrada")
    public Produto entrada(
            @RequestParam Long produtoId,
            @RequestParam BigDecimal quantidade,
            @RequestParam(required = false) String observacao
    ) {
        return estoqueService.entrada(produtoId, quantidade, observacao);
    }

    @PostMapping("/ajuste")
    public Produto ajuste(
            @RequestParam Long produtoId,
            @RequestParam BigDecimal quantidade,
            @RequestParam String motivo
    ) {
        return estoqueService.ajustar(produtoId, quantidade, motivo);
    }

    @GetMapping("/consultar")
    public Produto consultar(@RequestParam Long produtoId) {
        return estoqueService.consultar(produtoId);
    }

    @GetMapping("/historico")
    public List<MovimentacaoEstoque> historico(@RequestParam Long produtoId) {
        return movimentacaoEstoqueRepository.findByProdutoIdOrderByDataHoraDesc(produtoId);
    }
}
