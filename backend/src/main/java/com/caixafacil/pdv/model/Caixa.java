package com.caixafacil.pdv.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Caixa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    private LocalDateTime dataHoraAbertura;
    private LocalDateTime dataHoraFechamento;
    
    private Double valorAbertura = 0.0;
    private Double valorFechamento = 0.0;
    private Double valorVendas = 0.0;
    private Double valorSuprimentos = 0.0;
    private Double valorSangrias = 0.0;
    
    private String status; // ABERTO, FECHADO
    
    @Column(length = 1000)
    private String observacoes;
    
    @Column(length = 1000)
    private String observacoesFechamento;
    
    @OneToMany(mappedBy = "caixa", cascade = CascadeType.ALL)
    private List<MovimentacaoCaixa> movimentacoes = new ArrayList<>();
}
