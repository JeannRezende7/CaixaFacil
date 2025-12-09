package com.caixafacil.pdv.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caixafacil.pdv.model.Produto;
import com.caixafacil.pdv.repository.ProdutoRepository;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> buscarEstoqueBaixo() {
        return produtoRepository.findEstoqueBaixo();
    }

    public List<Produto> buscarEstoqueAlerta() {
        return produtoRepository.findEstoqueAlerta();
    }
}
