package com.vendeja.pdv.service;

import com.vendeja.pdv.model.MovimentacaoEstoque;
import com.vendeja.pdv.model.Produto;
import com.vendeja.pdv.repository.MovimentacaoEstoqueRepository;
import com.vendeja.pdv.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    /**
     * Entrada de estoque – adiciona quantidade
     */
    @Transactional
    public Produto entrada(Long produtoId, BigDecimal quantidade, String observacao) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + produtoId));

        BigDecimal estoqueAnterior = produto.getEstoque() != null
                ? produto.getEstoque()
                : BigDecimal.ZERO;

        BigDecimal estoqueAtual = estoqueAnterior.add(quantidade);

        produto.setEstoque(estoqueAtual);
        produtoRepository.save(produto);

        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        mov.setProduto(produto);
        mov.setTipo("ENTRADA");
        mov.setQuantidade(quantidade);
        mov.setEstoqueAnterior(estoqueAnterior);
        mov.setEstoqueAtual(estoqueAtual);
        mov.setObservacao(observacao);
        mov.setDataHora(LocalDateTime.now());
        // mov.setUsuario(...); // depois dá pra preencher com o usuário logado
        movimentacaoEstoqueRepository.save(mov);

        return produto;
    }

    /**
     * Ajuste direto – pode ser positivo ou negativo
     * No front você está usando isso como SAÍDA (quantidade negativa).
     */
    @Transactional
    public Produto ajustar(Long produtoId, BigDecimal quantidade, String motivo) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + produtoId));

        BigDecimal estoqueAnterior = produto.getEstoque() != null
                ? produto.getEstoque()
                : BigDecimal.ZERO;

        BigDecimal estoqueAtual = estoqueAnterior.add(quantidade);

        if (estoqueAtual.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Ajuste deixaria o estoque negativo!");
        }

        produto.setEstoque(estoqueAtual);
        produtoRepository.save(produto);

        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        mov.setProduto(produto);
        // se quantidade < 0, tratamos como SAÍDA, senão como AJUSTE
        mov.setTipo(quantidade.compareTo(BigDecimal.ZERO) < 0 ? "SAIDA" : "AJUSTE");
        mov.setQuantidade(quantidade);
        mov.setEstoqueAnterior(estoqueAnterior);
        mov.setEstoqueAtual(estoqueAtual);
        mov.setMotivo(motivo);
        mov.setDataHora(LocalDateTime.now());
        movimentacaoEstoqueRepository.save(mov);

        return produto;
    }

    /**
     * Consulta estoque atual
     */
    public Produto consultar(Long produtoId) {
        return produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + produtoId));
    }
}
