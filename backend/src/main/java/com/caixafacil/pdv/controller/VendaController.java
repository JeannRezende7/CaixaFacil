package com.caixafacil.pdv.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.caixafacil.pdv.dto.VendaDTO;
import com.caixafacil.pdv.model.Caixa;
import com.caixafacil.pdv.model.Configuracao;
import com.caixafacil.pdv.model.MovimentacaoCaixa;
import com.caixafacil.pdv.model.Venda;
import com.caixafacil.pdv.repository.CaixaRepository;
import com.caixafacil.pdv.repository.ConfiguracaoRepository;
import com.caixafacil.pdv.repository.MovimentacaoCaixaRepository;
import com.caixafacil.pdv.repository.VendaRepository;
import com.caixafacil.pdv.service.VendaService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vendas")
@RequiredArgsConstructor
public class VendaController {
    
    private final VendaRepository vendaRepository;
    private final VendaService vendaService;
    
    @Autowired
    private CaixaRepository caixaRepository;
    
    @Autowired
    private MovimentacaoCaixaRepository movimentacaoCaixaRepository;
    
    @Autowired
    private ConfiguracaoRepository configuracaoRepository;
    
    @GetMapping
    public List<Venda> listar() {
        return vendaRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Venda> buscarPorId(@PathVariable Long id) {
        return vendaRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/documento/{numero}")
    public ResponseEntity<Venda> buscarPorNumeroDocumento(@PathVariable Long numero) {
        return vendaRepository.findByNumeroDocumento(numero)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody VendaDTO vendaDTO) {
        try {
            System.out.println("Recebendo venda: " + vendaDTO);
            
            // Verificar se controle de caixa está habilitado
            Optional<Configuracao> configOpt = configuracaoRepository.findById(1L);
            if (configOpt.isPresent() && configOpt.get().getControlarCaixa() != null 
                && configOpt.get().getControlarCaixa()) {
                
                // Validar se existe caixa aberto
                Optional<Caixa> caixaOpt = caixaRepository.findCaixaAberto();
                if (!caixaOpt.isPresent()) {
                    Map<String, String> erro = new HashMap<>();
                    erro.put("erro", "Não é possível finalizar venda sem caixa aberto");
                    erro.put("tipo", "CaixaFechadoException");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
                }
            }
            
            // Salvar a venda
            Venda venda = vendaService.salvar(vendaDTO);
            
            // Se controle de caixa está ativo, registrar no caixa
            if (configOpt.isPresent() && configOpt.get().getControlarCaixa() != null 
                && configOpt.get().getControlarCaixa()) {
                
                Optional<Caixa> caixaOpt = caixaRepository.findCaixaAberto();
                if (caixaOpt.isPresent()) {
                    Caixa caixa = caixaOpt.get();
                    
                    // Converter BigDecimal para Double
                    Double valorVenda = venda.getTotal().doubleValue();
                    
                    // Atualizar valor de vendas no caixa
                    caixa.setValorVendas(caixa.getValorVendas() + valorVenda);
                    caixaRepository.save(caixa);
                    
                    // Registrar movimentação para cada forma de pagamento da venda
                    if (venda.getPagamentos() != null && !venda.getPagamentos().isEmpty()) {
                        for (var pagamento : venda.getPagamentos()) {
                            MovimentacaoCaixa mov = new MovimentacaoCaixa();
                            mov.setCaixa(caixa);
                            mov.setTipo("VENDA");
                            mov.setValor(pagamento.getValor().doubleValue());
                            mov.setDescricao("Venda #" + venda.getNumeroDocumento() + 
                                           " - " + pagamento.getFormaPagamento().getDescricao());
                            mov.setDataHora(LocalDateTime.now());
                            mov.setVenda(venda);
                            mov.setFormaPagamento(pagamento.getFormaPagamento());
                            movimentacaoCaixaRepository.save(mov);
                        }
                    }
                    
                    System.out.println("Venda registrada no caixa: " + valorVenda);
                }
            }
            
            return ResponseEntity.ok(venda);
        } catch (Exception e) {
            System.err.println("ERRO ao criar venda: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, String> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            erro.put("tipo", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }
    }
}
