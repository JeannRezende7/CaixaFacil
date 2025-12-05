package com.vendeja.pdv.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "produtos")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String codigo;
    
    @Column(nullable = false, length = 200)
    private String descricao;
    
    @Column(length = 10)
    private String unidade = "UN";
    
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal precoVenda = BigDecimal.ZERO;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal precoCusto = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal estoque = BigDecimal.ZERO;
    
    @Column(precision = 15, scale = 3)
    private BigDecimal estoqueMinimo = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private Boolean controlarEstoque = true;
    
    @Column(nullable = false)
    private Boolean ativo = true;
    
    @Column(length = 500)
    private String observacoes;
    
    @Column(nullable = false)
    private LocalDateTime dataCadastro = LocalDateTime.now();
    
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProdutoCodigo> codigosAlternativos;
}
