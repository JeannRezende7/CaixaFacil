package com.vendeja.pdv.repository;

import com.vendeja.pdv.model.Caixa;
import com.vendeja.pdv.model.MovimentacaoCaixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentacaoCaixaRepository extends JpaRepository<MovimentacaoCaixa, Long> {
    
    List<MovimentacaoCaixa> findByCaixaOrderByDataHoraDesc(Caixa caixa);
}
