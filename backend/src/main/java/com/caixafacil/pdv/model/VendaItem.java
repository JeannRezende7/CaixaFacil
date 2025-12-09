package com.caixafacil.pdv.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "vendas_itens")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VendaItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "venda_id", nullable = false)
    @JsonIgnore
    private Venda venda;
    
    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;
    
    @Column(nullable = false)
    private Integer sequencia;
    
    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantidade = BigDecimal.ONE;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal precoUnitario = BigDecimal.ZERO;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal descontoPercentual = BigDecimal.ZERO;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal descontoValor = BigDecimal.ZERO;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal acrescimoPercentual = BigDecimal.ZERO;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal acrescimoValor = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
}
