package com.vendeja.pdv.service;

import com.vendeja.pdv.dto.VendaDTO;
import com.vendeja.pdv.dto.VendaItemDTO;
import com.vendeja.pdv.dto.VendaPagamentoDTO;
import com.vendeja.pdv.model.*;
import com.vendeja.pdv.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendaService {
    
    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final FormaPagamentoRepository formaPagamentoRepository;
    
    @Transactional
    public Venda salvar(VendaDTO vendaDTO) {
        try {
            Venda venda = new Venda();
            
            Usuario usuario = usuarioRepository.findById(vendaDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + vendaDTO.getUsuarioId()));
            venda.setUsuario(usuario);
            
            if (vendaDTO.getClienteId() != null) {
                Cliente cliente = clienteRepository.findById(vendaDTO.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + vendaDTO.getClienteId()));
                venda.setCliente(cliente);
            }
            
            venda.setSubtotal(vendaDTO.getSubtotal() != null ? vendaDTO.getSubtotal() : BigDecimal.ZERO);
            venda.setDescontoPercentual(vendaDTO.getDescontoPercentual() != null ? vendaDTO.getDescontoPercentual() : BigDecimal.ZERO);
            venda.setDescontoValor(vendaDTO.getDescontoValor() != null ? vendaDTO.getDescontoValor() : BigDecimal.ZERO);
            venda.setAcrescimoPercentual(vendaDTO.getAcrescimoPercentual() != null ? vendaDTO.getAcrescimoPercentual() : BigDecimal.ZERO);
            venda.setAcrescimoValor(vendaDTO.getAcrescimoValor() != null ? vendaDTO.getAcrescimoValor() : BigDecimal.ZERO);
            venda.setFrete(vendaDTO.getFrete() != null ? vendaDTO.getFrete() : BigDecimal.ZERO);
            venda.setTotal(vendaDTO.getTotal() != null ? vendaDTO.getTotal() : BigDecimal.ZERO);
            venda.setValorPago(vendaDTO.getValorPago() != null ? vendaDTO.getValorPago() : BigDecimal.ZERO);
            venda.setTroco(vendaDTO.getTroco() != null ? vendaDTO.getTroco() : BigDecimal.ZERO);
            venda.setObservacoes(vendaDTO.getObservacoes());
            venda.setCancelada(false);
            
            Long maxNum = vendaRepository.findMaxNumeroDocumento();
            venda.setNumeroDocumento(maxNum + 1);
            venda.setDataHora(LocalDateTime.now());
            
            // Converter itens
            List<VendaItem> itens = new ArrayList<>();
            if (vendaDTO.getItens() != null && !vendaDTO.getItens().isEmpty()) {
                int sequencia = 1;
                for (VendaItemDTO itemDTO : vendaDTO.getItens()) {
                    VendaItem item = new VendaItem();
                    
                    Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemDTO.getProdutoId()));
                    
                    item.setProduto(produto);
                    item.setVenda(venda);
                    item.setSequencia(sequencia++);
                    item.setQuantidade(itemDTO.getQuantidade() != null ? itemDTO.getQuantidade() : BigDecimal.ONE);
                    item.setPrecoUnitario(itemDTO.getPrecoUnitario() != null ? itemDTO.getPrecoUnitario() : BigDecimal.ZERO);
                    item.setDescontoPercentual(itemDTO.getDescontoPercentual() != null ? itemDTO.getDescontoPercentual() : BigDecimal.ZERO);
                    item.setDescontoValor(itemDTO.getDescontoValor() != null ? itemDTO.getDescontoValor() : BigDecimal.ZERO);
                    item.setAcrescimoPercentual(itemDTO.getAcrescimoPercentual() != null ? itemDTO.getAcrescimoPercentual() : BigDecimal.ZERO);
                    item.setAcrescimoValor(itemDTO.getAcrescimoValor() != null ? itemDTO.getAcrescimoValor() : BigDecimal.ZERO);
                    item.setTotal(itemDTO.getTotal() != null ? itemDTO.getTotal() : BigDecimal.ZERO);
                    
                    itens.add(item);
                }
            }
            venda.setItens(itens);
            
            // Converter pagamentos
            List<VendaPagamento> pagamentos = new ArrayList<>();
            if (vendaDTO.getPagamentos() != null && !vendaDTO.getPagamentos().isEmpty()) {
                for (VendaPagamentoDTO pagDTO : vendaDTO.getPagamentos()) {
                    VendaPagamento pagamento = new VendaPagamento();
                    
                    FormaPagamento formaPagamento = formaPagamentoRepository.findById(pagDTO.getFormaPagamentoId())
                        .orElseThrow(() -> new RuntimeException("Forma de pagamento não encontrada: " + pagDTO.getFormaPagamentoId()));
                    
                    pagamento.setFormaPagamento(formaPagamento);
                    pagamento.setVenda(venda);
                    pagamento.setValor(pagDTO.getValor() != null ? pagDTO.getValor() : BigDecimal.ZERO);
                    pagamento.setTroco(pagDTO.getTroco() != null ? pagDTO.getTroco() : BigDecimal.ZERO);
                    
                    pagamentos.add(pagamento);
                }
            }
            venda.setPagamentos(pagamentos);
            
            Venda vendaSalva = vendaRepository.save(venda);
            baixarEstoque(vendaSalva);
            
            return vendaSalva;
            
        } catch (Exception e) {
            System.err.println("Erro ao salvar venda: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar venda: " + e.getMessage(), e);
        }
    }
    
    private void baixarEstoque(Venda venda) {
        if (venda.getItens() != null) {
            for (VendaItem item : venda.getItens()) {
                Produto produto = item.getProduto();
                
                if (produto.getControlarEstoque() != null && produto.getControlarEstoque()) {
                    BigDecimal estoqueAtual = produto.getEstoque() != null ? produto.getEstoque() : BigDecimal.ZERO;
                    BigDecimal quantidade = item.getQuantidade() != null ? item.getQuantidade() : BigDecimal.ZERO;
                    BigDecimal novoEstoque = estoqueAtual.subtract(quantidade);
                    produto.setEstoque(novoEstoque);
                    produtoRepository.save(produto);
                }
            }
        }
    }
}
