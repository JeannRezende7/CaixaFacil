package com.caixafacil.pdv.model;

import jakarta.persistence.*;

@Entity
@Table(name = "formas_pagamento")
public class FormaPagamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String descricao;
    
    @Column(nullable = false, length = 2, name = "tipo_pagamento")
    private String tipoPagamento;
    
    @Column(nullable = false)
    private Boolean ativo = true;
    
    @Column(nullable = false)
    private Boolean permiteParcelamento = false;
    
    @Column(length = 20)
    private String categoria; // DINHEIRO, PIX, CARTAO, TICKET, VALE, PARCELADO
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getTipoPagamento() {
        return tipoPagamento;
    }
    
    public void setTipoPagamento(String tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }
    
    public Boolean getAtivo() {
        return ativo;
    }
    
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    
    public Boolean getPermiteParcelamento() {
        return permiteParcelamento;
    }
    
    public void setPermiteParcelamento(Boolean permiteParcelamento) {
        this.permiteParcelamento = permiteParcelamento;
    }
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
