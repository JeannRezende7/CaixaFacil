package com.vendeja.pdv.config;

import com.vendeja.pdv.model.*;
import com.vendeja.pdv.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final ConfiguracaoRepository configuracaoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;
    private final FormaPagamentoRepository formaPagamentoRepository;

    public DataInitializer(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            ConfiguracaoRepository configuracaoRepository,
            CategoriaRepository categoriaRepository,
            ProdutoRepository produtoRepository,
            FormaPagamentoRepository formaPagamentoRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.configuracaoRepository = configuracaoRepository;
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
        this.formaPagamentoRepository = formaPagamentoRepository;
    }

    @Override
    public void run(String... args) {
        // 1. Criar usuário admin
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setLogin("admin");
            admin.setSenha("admin");
            admin.setNome("Administrador");
            admin.setAdmin(true);
            admin.setAtivo(true);
            admin.setDataCadastro(LocalDateTime.now());
            usuarioRepository.save(admin);
            System.out.println("Usuário admin criado!");
        }

        // 2. Criar cliente Consumidor Final
        Cliente clientePadrao = null;
        if (clienteRepository.count() == 0) {
            clientePadrao = new Cliente();
            clientePadrao.setCodigo("000001");
            clientePadrao.setNome("CONSUMIDOR FINAL");
            clientePadrao.setAtivo(true);
            clientePadrao.setDataCadastro(LocalDateTime.now());
            clientePadrao = clienteRepository.save(clientePadrao);
            System.out.println("Cliente Consumidor Final criado!");
        } else {
            clientePadrao = clienteRepository.findByCodigo("000001").orElse(null);
        }

        // 3. Criar/atualizar configuração com cliente padrão
        Configuracao config = configuracaoRepository.findById(1L).orElse(new Configuracao());
        if (config.getId() == null) {
            config.setControlarCaixa(false);
            if (clientePadrao != null) {
                config.setClientePadrao(clientePadrao);
            }
            configuracaoRepository.save(config);
            System.out.println("Configuração criada com cliente padrão!");
        }

        // 4. Criar categoria Geral
        if (categoriaRepository.count() == 0) {
            Categoria categoria = new Categoria();
            categoria.setDescricao("GERAL");
            categoria.setAtivo(true);
            categoriaRepository.save(categoria);
            System.out.println("Categoria Geral criada!");
        }

        // 5. Criar produto exemplo
        if (produtoRepository.count() == 0) {
            Categoria categoria = categoriaRepository.findAll().get(0);
            Produto produto = new Produto();
            produto.setCodigo("000001");
            produto.setDescricao("PRODUTO EXEMPLO 1");
            produto.setUnidade("UN");
            produto.setPrecoVenda(BigDecimal.valueOf(10.00));
            produto.setPrecoCusto(BigDecimal.valueOf(5.00));
            produto.setEstoque(BigDecimal.ZERO);
            produto.setEstoqueMinimo(BigDecimal.ZERO);
            produto.setControlarEstoque(true);
            produto.setAtivo(true);
            produto.setCategoria(categoria);
            produto.setDataCadastro(LocalDateTime.now());
            produtoRepository.save(produto);
            System.out.println("Produto exemplo criado!");
        }

        // 6. Criar formas de pagamento com CATEGORIAS
        if (formaPagamentoRepository.count() == 0) {
            // CATEGORIA: DINHEIRO
            FormaPagamento dinheiro = new FormaPagamento();
            dinheiro.setDescricao("DINHEIRO");
            dinheiro.setTipoPagamento("DI");
            dinheiro.setCategoria("DINHEIRO");
            dinheiro.setAtivo(true);
            dinheiro.setPermiteParcelamento(false);
            formaPagamentoRepository.save(dinheiro);

            // CATEGORIA: PIX
            FormaPagamento pix = new FormaPagamento();
            pix.setDescricao("PIX");
            pix.setTipoPagamento("PI");
            pix.setCategoria("PIX");
            pix.setAtivo(true);
            pix.setPermiteParcelamento(false);
            formaPagamentoRepository.save(pix);

            // CATEGORIA: CARTAO
            FormaPagamento cartaoDebito = new FormaPagamento();
            cartaoDebito.setDescricao("CARTÃO DÉBITO");
            cartaoDebito.setTipoPagamento("CD");
            cartaoDebito.setCategoria("CARTAO");
            cartaoDebito.setAtivo(true);
            cartaoDebito.setPermiteParcelamento(false);
            formaPagamentoRepository.save(cartaoDebito);

            FormaPagamento cartaoCredito = new FormaPagamento();
            cartaoCredito.setDescricao("CARTÃO CRÉDITO");
            cartaoCredito.setTipoPagamento("CC");
            cartaoCredito.setCategoria("CARTAO");
            cartaoCredito.setAtivo(true);
            cartaoCredito.setPermiteParcelamento(false);
            formaPagamentoRepository.save(cartaoCredito);

            // CATEGORIA: PARCELADO
            FormaPagamento parcelado = new FormaPagamento();
            parcelado.setDescricao("CRÉDITO PARCELADO");
            parcelado.setTipoPagamento("CP");
            parcelado.setCategoria("PARCELADO");
            parcelado.setAtivo(true);
            parcelado.setPermiteParcelamento(true);
            formaPagamentoRepository.save(parcelado);

            // CATEGORIA: TICKET
            FormaPagamento ticket = new FormaPagamento();
            ticket.setDescricao("TICKET ALIMENTAÇÃO");
            ticket.setTipoPagamento("TI");
            ticket.setCategoria("TICKET");
            ticket.setAtivo(true);
            ticket.setPermiteParcelamento(false);
            formaPagamentoRepository.save(ticket);

            // CATEGORIA: VALE
            FormaPagamento vale = new FormaPagamento();
            vale.setDescricao("VALE REFEIÇÃO");
            vale.setTipoPagamento("VL");
            vale.setCategoria("VALE");
            vale.setAtivo(true);
            vale.setPermiteParcelamento(false);
            formaPagamentoRepository.save(vale);

            System.out.println("Formas de pagamento criadas com categorias!");
        }
    }
}
