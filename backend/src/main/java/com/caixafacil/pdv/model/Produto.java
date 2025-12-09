package com.caixafacil.pdv.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String codigo;
    
    private String descricao;
    private String unidade;
    
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    
    private BigDecimal precoVenda = BigDecimal.ZERO;
    private BigDecimal precoCusto = BigDecimal.ZERO;
    private BigDecimal estoque = BigDecimal.ZERO;
    private BigDecimal estoqueMinimo = BigDecimal.ZERO;
    
    private Boolean controlarEstoque = true;
    private Boolean ativo = true;
    
    @Column(length = 1000)
    private String observacoes;
    
    private LocalDateTime dataCadastro;
    
    private String fotoPath;
    
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProdutoCodigo> codigosAlternativos = new ArrayList<>();
}
