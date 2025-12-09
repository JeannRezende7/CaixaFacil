package com.caixafacil.pdv.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class VendaItemDTO {
    private Long produtoId;
    private BigDecimal quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal descontoPercentual;
    private BigDecimal descontoValor;
    private BigDecimal acrescimoPercentual;
    private BigDecimal acrescimoValor;
    private BigDecimal total;
}
