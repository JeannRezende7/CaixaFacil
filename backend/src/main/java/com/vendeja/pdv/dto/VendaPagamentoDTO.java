package com.vendeja.pdv.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class VendaPagamentoDTO {
    private Long formaPagamentoId;
    private BigDecimal valor;
    private BigDecimal troco = BigDecimal.ZERO;
}
