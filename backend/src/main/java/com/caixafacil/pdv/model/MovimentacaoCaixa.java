package com.caixafacil.pdv.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class MovimentacaoCaixa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "caixa_id")
    private Caixa caixa;
    
    private String tipo; // ABERTURA, VENDA, SUPRIMENTO, SANGRIA, FECHAMENTO
    
    private Double valor;
    
    @Column(length = 500)
    private String descricao;
    
    private LocalDateTime dataHora;
    
    @ManyToOne
    @JoinColumn(name = "venda_id")
    private Venda venda;
    
    @ManyToOne
    @JoinColumn(name = "forma_pagamento_id")
    private FormaPagamento formaPagamento;
}
