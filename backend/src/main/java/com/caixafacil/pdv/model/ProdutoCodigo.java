package com.caixafacil.pdv.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "produtos_codigos", 
       uniqueConstraints = @UniqueConstraint(columnNames = "codigo"))
@Data
public class ProdutoCodigo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    @JsonIgnore
    private Produto produto;
    
    @Column(nullable = false, length = 50)
    private String codigo;
    
    @Column(length = 100)
    private String descricao;
}
