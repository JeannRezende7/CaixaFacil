package com.caixafacil.pdv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.caixafacil.pdv.model.Caixa;
import com.caixafacil.pdv.model.MovimentacaoCaixa;

import java.util.List;

@Repository
public interface MovimentacaoCaixaRepository extends JpaRepository<MovimentacaoCaixa, Long> {
    
    List<MovimentacaoCaixa> findByCaixaOrderByDataHoraDesc(Caixa caixa);
}
