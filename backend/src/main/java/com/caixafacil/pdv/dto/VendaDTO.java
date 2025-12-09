package com.caixafacil.pdv.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class VendaDTO {
    private Long usuarioId;
    private Long clienteId;
    private BigDecimal subtotal;
    private BigDecimal descontoPercentual;
    private BigDecimal descontoValor;
    private BigDecimal acrescimoPercentual;
    private BigDecimal acrescimoValor;
    private BigDecimal frete;
    private BigDecimal total;
    private BigDecimal valorPago;
    private BigDecimal troco;
    private String observacoes;
    private List<VendaItemDTO> itens;
    private List<VendaPagamentoDTO> pagamentos;
}
