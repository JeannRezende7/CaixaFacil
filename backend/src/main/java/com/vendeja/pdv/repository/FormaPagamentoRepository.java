package com.vendeja.pdv.repository;

import com.vendeja.pdv.model.FormaPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FormaPagamentoRepository extends JpaRepository<FormaPagamento, Long> {
    List<FormaPagamento> findByAtivoTrue();
}
