package com.vendeja.pdv.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vendas")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Venda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private Long numeroDocumento;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false)
    private LocalDateTime dataHora;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal descontoPercentual = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal descontoValor = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal acrescimoPercentual = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal acrescimoValor = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal frete = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal valorPago = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal troco = BigDecimal.ZERO;
    
    @Column(length = 500)
    private String observacoes;
    
    @Column(nullable = false)
    private Boolean cancelada = false;
    
    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VendaItem> itens;
    
    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VendaPagamento> pagamentos;
}
