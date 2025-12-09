package com.caixafacil.pdv.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
@Data
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 20)
    private String codigo;
    
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Column(length = 20)
    private String cpfCnpj;
    
    @Column(length = 15)
    private String telefone;
    
    @Column(length = 100)
    private String email;
    
    @Column(length = 100)
    private String endereco;
    
    @Column(length = 50)
    private String cidade;
    
    @Column(length = 2)
    private String uf;
    
    @Column(length = 10)
    private String cep;
    
    @Column(nullable = false)
    private Boolean ativo = true;
    
    @Column(nullable = false)
    private LocalDateTime dataCadastro = LocalDateTime.now();
}
