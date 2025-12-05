package com.vendeja.pdv.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "formas_pagamento")
@Data
public class FormaPagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String descricao;
    
    @Column(nullable = false, length = 2)
    private String tipoPagamento = "99";
    
    @Column(nullable = false)
    private Boolean permiteParcelamento = false;
    
    @Column(nullable = false)
    private Boolean ativo = true;
}
