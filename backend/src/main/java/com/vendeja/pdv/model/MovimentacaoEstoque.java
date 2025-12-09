package com.vendeja.pdv.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "estoque_movimentacao")
@Data
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Produto produto;

    // ENTRADA, SAIDA, AJUSTE
    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantidade;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal estoqueAnterior;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal estoqueAtual;

    @Column(length = 255)
    private String motivo;      // Para saída/ajuste
    @Column(length = 255)
    private String observacao;  // Para entrada

    @ManyToOne
    private Usuario usuario;    // por enquanto pode ficar null

    @Column(nullable = false)
    private LocalDateTime dataHora;
}
