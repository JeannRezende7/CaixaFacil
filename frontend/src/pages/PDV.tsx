import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { produtoService, clienteService, vendaService, cadastrosService } from '../services/api';
import { Produto, Cliente, VendaItem, FormaPagamento, Usuario } from '../types';
import ModalPagamento from '../components/ModalPagamento';
import { useNotification } from '../contexts/NotificationContext';
import axios from 'axios';
import CupomNaoFiscal from '../components/CupomNaoFiscal';

interface PagamentoItem {
  formaPagamento: FormaPagamento;
  valorDigitado: number;
  valorPago: number;
  troco: number;
}

export default function PDV() {
  const navigate = useNavigate();
  const { showSuccess, showError, showWarning } = useNotification();
  const [usuario, setUsuario] = useState<Usuario | null>(null);
  const [codigoProduto, setCodigoProduto] = useState('');
  const [codigoCliente, setCodigoCliente] = useState('');
  const [cliente, setCliente] = useState<Cliente | null>(null);
  const [produtoAtual, setProdutoAtual] = useState<Produto | null>(null);
  const [itens, setItens] = useState<VendaItem[]>([]);
  const [itemSelecionado, setItemSelecionado] = useState<number>(-1);
  const [formasPagamento, setFormasPagamento] = useState<FormaPagamento[]>([]);
  const [mostrarModalPagamento, setMostrarModalPagamento] = useState(false);

  const [empresa, setEmpresa] = useState<any>(null);
  const [mostrarCupom, setMostrarCupom] = useState(false);
  const [vendaFinalizada, setVendaFinalizada] = useState<any>(null);

  // CONTROLE DE CAIXA
  const [caixaAberto, setCaixaAberto] = useState(false);
  const [controlarCaixa, setControlarCaixa] = useState(false);

  const [descontoGlobalPerc, setDescontoGlobalPerc] = useState('0');
  const [descontoGlobalValor, setDescontoGlobalValor] = useState('0');
  const [acrescimoGlobalPerc, setAcrescimoGlobalPerc] = useState('0');
  const [acrescimoGlobalValor, setAcrescimoGlobalValor] = useState('0');
  const [frete, setFrete] = useState('0');

  const inputProdutoRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    const usuarioStr = localStorage.getItem('usuario');
    if (!usuarioStr) {
      navigate('/');
      return;
    }
    setUsuario(JSON.parse(usuarioStr));

    cadastrosService.listarFormasPagamento().then(res => {
      setFormasPagamento(res.data);
    });
  }, [navigate]);

  useEffect(() => {
    axios.get('http://localhost:8080/api/configuracao')
      .then(res => {
        setEmpresa(res.data);
        setControlarCaixa(res.data.controlarCaixa || false);

        // Carregar cliente padrão se configurado
        if (res.data.clientePadrao) {
          setCliente(res.data.clientePadrao);
          setCodigoCliente(res.data.clientePadrao.codigo || '');
          console.log('Cliente padrão carregado:', res.data.clientePadrao.nome);
        }
      })
      .catch(err => console.error('Erro ao carregar configuração:', err));
  }, []);

  // Verificar status do caixa
  useEffect(() => {
    if (controlarCaixa) {
      verificarStatusCaixa();
    }
  }, [controlarCaixa]);

  const verificarStatusCaixa = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/caixa/status');
      setCaixaAberto(res.data.caixaAberto);
    } catch (error) {
      console.error('Erro ao verificar caixa:', error);
    }
  };

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Delete' && itemSelecionado >= 0) {
        removerItem(itemSelecionado);
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [itemSelecionado, itens]);

  const buscarProduto = async (codigo: string) => {
    if (!codigo.trim()) return;

    try {
      // Busca inteligente com padding automático
      const res = await axios.get(`http://localhost:8080/api/produtos/buscar-parcial/${codigo}`);
      if (Array.isArray(res.data) && res.data.length > 0) {
        const produto = res.data[0]; // pega o primeiro
        setProdutoAtual(produto);
        adicionarItem(produto);
      } else {
        showError('Produto não encontrado');
      }
      setCodigoProduto('');
      inputProdutoRef.current?.focus();
    } catch (error) {
      showError('Produto não encontrado');
      setCodigoProduto('');
      setProdutoAtual(null);
    }
  };

  const buscarCliente = async (codigo: string) => {
    if (!codigo.trim()) {
      setCliente(null);
      return;
    }

    try {
      // Busca inteligente com padding automático
      const res = await axios.get(`http://localhost:8080/api/clientes/buscar-parcial/${codigo}`);
      setCliente(res.data);
      setCodigoCliente(res.data.codigo);
    } catch (error) {
      showError('Cliente não encontrado');
      setCodigoCliente('');
    }
  };

  const carregarClientePadrao = async (clienteId: number) => {
    try {
      const res = await axios.get(`http://localhost:8080/api/clientes/${clienteId}`);
      setCliente(res.data);
      setCodigoCliente(res.data.codigo || '');
    } catch (error) {
      console.error('Erro ao carregar cliente padrão:', error);
    }
  };

  const adicionarItem = (produto: Produto) => {
    const novoItem: VendaItem = {
      produto,
      quantidade: 1,
      precoUnitario: produto.precoVenda,
      descontoPercentual: 0,
      descontoValor: 0,
      acrescimoPercentual: 0,
      acrescimoValor: 0,
      total: produto.precoVenda,
    };
    setItens([...itens, novoItem]);
  };

  const removerItem = (index: number) => {
    setItens(itens.filter((_, i) => i !== index));
    setItemSelecionado(-1);
  };

  const calcularTotalItem = (item: VendaItem): number => {
    let total = item.precoUnitario * item.quantidade;

    if (item.descontoPercentual && item.descontoPercentual > 0) {
      total -= (total * item.descontoPercentual / 100);
    }

    if (item.descontoValor && item.descontoValor > 0) {
      total -= item.descontoValor;
    }

    if (item.acrescimoPercentual && item.acrescimoPercentual > 0) {
      total += (total * item.acrescimoPercentual / 100);
    }

    if (item.acrescimoValor && item.acrescimoValor > 0) {
      total += item.acrescimoValor;
    }

    return Math.max(0, total);
  };

  const atualizarQuantidade = (index: number, quantidade: string) => {
    const qtd = Math.max(0, parseFloat(quantidade) || 0);
    const novosItens = [...itens];
    novosItens[index].quantidade = qtd;
    novosItens[index].total = calcularTotalItem(novosItens[index]);
    setItens(novosItens);
  };

  const atualizarDesconto = (index: number, tipo: 'perc' | 'valor', valor: string) => {
    const val = Math.max(0, parseFloat(valor) || 0);
    const novosItens = [...itens];
    const item = novosItens[index];

    if (tipo === 'perc') {
      item.descontoPercentual = val;
      const subtotal = item.precoUnitario * item.quantidade;
      item.descontoValor = (subtotal * val / 100);
    } else {
      item.descontoValor = val;
      const subtotal = item.precoUnitario * item.quantidade;
      item.descontoPercentual = subtotal > 0 ? (val / subtotal * 100) : 0;
    }

    item.total = calcularTotalItem(item);
    setItens(novosItens);
  };

  const atualizarAcrescimo = (index: number, tipo: 'perc' | 'valor', valor: string) => {
    const val = Math.max(0, parseFloat(valor) || 0);
    const novosItens = [...itens];
    const item = novosItens[index];

    if (tipo === 'perc') {
      item.acrescimoPercentual = val;
      const subtotal = item.precoUnitario * item.quantidade;
      item.acrescimoValor = (subtotal * val / 100);
    } else {
      item.acrescimoValor = val;
      const subtotal = item.precoUnitario * item.quantidade;
      item.acrescimoPercentual = subtotal > 0 ? (val / subtotal * 100) : 0;
    }

    item.total = calcularTotalItem(item);
    setItens(novosItens);
  };

  const calcularSubtotal = () => {
    return itens.reduce((acc, item) => acc + item.total, 0);
  };

  const calcularTotal = () => {
    let total = calcularSubtotal();

    const descPerc = Math.max(0, parseFloat(descontoGlobalPerc) || 0);
    if (descPerc > 0) {
      total -= (total * descPerc / 100);
    }

    const descValor = Math.max(0, parseFloat(descontoGlobalValor) || 0);
    total -= descValor;

    const acrPerc = Math.max(0, parseFloat(acrescimoGlobalPerc) || 0);
    if (acrPerc > 0) {
      total += (total * acrPerc / 100);
    }

    const acrValor = Math.max(0, parseFloat(acrescimoGlobalValor) || 0);
    total += acrValor;

    const freteVal = Math.max(0, parseFloat(frete) || 0);
    total += freteVal;

    return Math.max(0, total);
  };

  const abrirModalPagamento = () => {
    if (itens.length === 0) {
      showWarning('Adicione itens à venda');
      return;
    }

    // VALIDAR CAIXA ABERTO
    if (controlarCaixa && !caixaAberto) {
      showError('Não é possível finalizar venda sem caixa aberto!');
      return;
    }

    setMostrarModalPagamento(true);
  };

  const finalizarVenda = async (pagamentos: PagamentoItem[], valorPago: number, troco: number) => {
    const vendaDTO = {
      usuarioId: usuario!.id,
      clienteId: cliente?.id || null,
      subtotal: calcularSubtotal(),
      descontoPercentual: parseFloat(descontoGlobalPerc) || 0,
      descontoValor: parseFloat(descontoGlobalValor) || 0,
      acrescimoPercentual: parseFloat(acrescimoGlobalPerc) || 0,
      acrescimoValor: parseFloat(acrescimoGlobalValor) || 0,
      frete: parseFloat(frete) || 0,
      total: calcularTotal(),
      valorPago: valorPago,
      troco: troco,
      observacoes: null,
      itens: itens.map(item => ({
        produtoId: item.produto.id,
        quantidade: item.quantidade,
        precoUnitario: item.precoUnitario,
        descontoPercentual: item.descontoPercentual || 0,
        descontoValor: item.descontoValor || 0,
        acrescimoPercentual: item.acrescimoPercentual || 0,
        acrescimoValor: item.acrescimoValor || 0,
        total: item.total,
      })),
      pagamentos: pagamentos.map(p => ({
        formaPagamentoId: p.formaPagamento.id,
        valor: p.valorPago,
        troco: p.troco,
      })),
    };

    try {
      const res = await vendaService.criar(vendaDTO);
      setMostrarModalPagamento(false);

      if (troco > 0) {
        showSuccess(`Venda ${res.data.numeroDocumento} finalizada! Troco: R$ ${troco.toFixed(2)}`);
      } else {
        showSuccess(`Venda ${res.data.numeroDocumento} finalizada com sucesso!`);
      }

      // Preparar dados completos para o cupom
      const vendaCompleta = {
        ...res.data,
        itens: itens,
        cliente: cliente,
        usuario: usuario,
        pagamentos: pagamentos.map(p => ({
          formaPagamento: p.formaPagamento,
          valor: p.valorPago,
        })),
      };

      setVendaFinalizada(vendaCompleta);
      setMostrarCupom(true);
      limparVenda();

      // Atualizar status do caixa após venda
      if (controlarCaixa) {
        verificarStatusCaixa();
      }
    } catch (error: any) {
      console.error('Erro:', error);
      let mensagem = 'Erro desconhecido';
      if (error.response?.data) {
        if (typeof error.response.data === 'string') {
          mensagem = error.response.data;
        } else if (error.response.data.erro) {
          mensagem = error.response.data.erro;
        } else {
          mensagem = JSON.stringify(error.response.data);
        }
      } else if (error.message) {
        mensagem = error.message;
      }
      showError('Erro ao finalizar venda: ' + mensagem);
    }
  };

  const limparVenda = () => {
    setItens([]);

    // Se tem cliente padrão configurado, recarregar ele
    if (empresa?.clientePadrao) {
      setCliente(empresa.clientePadrao);
      setCodigoCliente(empresa.clientePadrao.codigo || '');
    } else {
      setCliente(null);
      setCodigoCliente('');
    }

    setCodigoProduto('');
    setDescontoGlobalPerc('0');
    setDescontoGlobalValor('0');
    setAcrescimoGlobalPerc('0');
    setAcrescimoGlobalValor('0');
    setFrete('0');
    setItemSelecionado(-1);
    inputProdutoRef.current?.focus();
  };

  return (
    <div className="h-screen flex flex-col bg-[#0f1215] text-gray-200">
      <div className="bg-[#111418] border-b border-[#1f2429] text-gray-200 p-4 flex justify-between items-center shadow-md">
        <div className="flex items-center gap-4">
          {empresa?.logoPath && (
            <img
              src={`http://localhost:8080/uploads/logos/${empresa.logoPath}`}
              alt="Logo"
              className="h-10 w-auto opacity-90"
            />
          )}
          <h1 className="text-xl font-semibold tracking-wide text-white">
            {empresa?.nomeFantasia || 'Caixa Fácil'}
          </h1>

          {controlarCaixa && (
            <div
              className={`px-3 py-1 rounded font-bold text-xs ${
                caixaAberto ? 'bg-green-600/20 text-green-400' : 'bg-red-600/20 text-red-400'
              }`}
            >
              {caixaAberto ? 'CAIXA ABERTO' : 'CAIXA FECHADO'}
            </div>
          )}
        </div>

        <div className="flex gap-3 items-center text-sm">
          <span className="mr-2 text-gray-400">Operador: {usuario?.nome}</span>

          {controlarCaixa && (
            <button
              onClick={() => navigate('/caixa')}
              className="px-4 py-2 rounded bg-[#1b1f24] text-green-400 border border-[#2a2f35] hover:bg-[#22272d]"
            >
              💰 Caixa
            </button>
          )}

          <button
            onClick={() => navigate('/vendas')}
            className="px-4 py-2 rounded bg-[#1b1f24] text-gray-300 border border-[#2a2f35] hover:bg-[#22272d]"
          >
            Vendas
          </button>
          <button
            onClick={() => navigate('/cadastros')}
            className="px-4 py-2 rounded bg-[#1b1f24] text-gray-300 border border-[#2a2f35] hover:bg-[#22272d]"
          >
            Cadastros
          </button>

          <button
            onClick={() => navigate('/estoque/consulta')}
            className="px-4 py-2 rounded bg-[#1b1f24] text-gray-300 border border-[#2a2f35] hover:bg-[#22272d]"
          >
            📦 Estoque
          </button>

          <button
            onClick={() => navigate('/configuracao')}
            className="px-4 py-2 rounded bg-[#1b1f24] text-gray-300 border border-[#2a2f35] hover:bg-[#22272d]"
          >
            ⚙️ Configuração
          </button>
          <button
            onClick={() => {
              localStorage.removeItem('usuario');
              navigate('/');
            }}
            className="bg-red-600/20 text-red-400 px-4 py-2 rounded border border-red-600/40 hover:bg-red-600/30"
          >
            Sair
          </button>
        </div>
      </div>

      <div className="flex-1 flex p-4 gap-4">
        <div className="flex-1 flex flex-col gap-4">
          <div className="bg-[#111418] p-4 rounded border border-[#1f2429] shadow">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold mb-1 text-gray-400">
                  CÓDIGO DO PRODUTO (F1)
                </label>
                <div className="flex gap-2 items-center">
                  <div className="flex-1 flex gap-2">
                    <input
                      ref={inputProdutoRef}
                      type="text"
                      value={codigoProduto}
                      onChange={(e) => setCodigoProduto(e.target.value)}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') buscarProduto(codigoProduto);
                      }}
                      className="flex-1 px-3 py-2 rounded bg-[#0d0f12] border border-[#2a2f35] text-gray-200 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-green-500/40"
                      placeholder="Digite o código ou use o leitor..."
                      autoFocus
                    />
                    <button
                      onClick={() => buscarProduto(codigoProduto)}
                      className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-500"
                    >
                      🔍
                    </button>
                  </div>

                  {produtoAtual?.fotoPath && (
                    <div className="w-20 h-20 border-2 border-[#2a2f35] rounded overflow-hidden flex items-center justify-center bg-[#0d0f12]">
                      <img
                        src={`http://localhost:8080/uploads/produtos/${produtoAtual.fotoPath}`}
                        alt="Foto do Produto"
                        className="max-w-full max-h-full object-contain"
                      />
                    </div>
                  )}
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold mb-1 text-gray-400">
                  CÓDIGO DO CLIENTE
                </label>
                <div className="flex gap-2">
                  <input
                    type="text"
                    value={codigoCliente}
                    onChange={(e) => setCodigoCliente(e.target.value)}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter') buscarCliente(codigoCliente);
                    }}
                    className="flex-1 px-3 py-2 rounded bg-[#0d0f12] border border-[#2a2f35] text-gray-200 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-green-500/40"
                    placeholder="Opcional"
                  />
                  <button
                    onClick={() => buscarCliente(codigoCliente)}
                    className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-500"
                  >
                    🔍
                  </button>
                </div>
                {cliente && (
                  <div className="mt-2 text-xs text-green-400 font-semibold">
                    Cliente: {cliente.nome}
                  </div>
                )}
              </div>
            </div>
          </div>

          <div className="flex-1 bg-[#111418] rounded border border-[#1f2429] overflow-hidden flex flex-col">
            <div className="bg-[#1a1f24] p-2 font-bold grid grid-cols-12 gap-2 text-xs text-gray-400 border-b border-[#2a2f35]">
              <div className="col-span-1">QTD.</div>
              <div className="col-span-3">DESCRIÇÃO</div>
              <div className="col-span-1">PREÇO UNIT.</div>
              <div className="col-span-1">DESC %</div>
              <div className="col-span-1">DESC R$</div>
              <div className="col-span-1">ACRÉS %</div>
              <div className="col-span-1">ACRÉS R$</div>
              <div className="col-span-2 text-right">TOTAL</div>
            </div>

            <div className="flex-1 overflow-y-auto">
              {itens.map((item, index) => (
                <div
                  key={index}
                  className={`grid grid-cols-12 gap-2 p-2 border-b border-[#1f2429] cursor-pointer hover:bg-[#161b20] transition ${
                    itemSelecionado === index ? 'bg-[#1e252b]' : ''
                  }`}
                  onClick={() => setItemSelecionado(index)}
                >
                  <div className="col-span-1 text-sm">{item.quantidade}</div>
                  <div className="col-span-3 text-xs truncate">{item.produto.descricao}</div>
                  <div className="col-span-1">
                    <input
                      type="number"
                      value={item.quantidade}
                      onChange={(e) => atualizarQuantidade(index, e.target.value)}
                      className="w-full px-2 py-1 rounded bg-[#0d0f12] border border-[#2a2f35] text-right text-xs text-gray-200"
                      step="0.001"
                      min="0"
                    />
                  </div>
                  <div className="col-span-1 text-right text-xs text-gray-200">
                    {item.precoUnitario.toFixed(2)}
                  </div>
                  <div className="col-span-1">
                    <input
                      type="number"
                      value={item.descontoPercentual || 0}
                      onChange={(e) => atualizarDesconto(index, 'perc', e.target.value)}
                      className="w-full px-2 py-1 rounded bg-[#0d0f12] border border-[#2a2f35] text-right text-xs text-gray-200"
                      min="0"
                      max="100"
                    />
                  </div>
                  <div className="col-span-1">
                    <input
                      type="number"
                      value={(item.descontoValor || 0).toFixed(2)}
                      onChange={(e) => atualizarDesconto(index, 'valor', e.target.value)}
                      className="w-full px-2 py-1 rounded bg-[#0d0f12] border border-[#2a2f35] text-right text-xs text-gray-200"
                      min="0"
                    />
                  </div>
                  <div className="col-span-1">
                    <input
                      type="number"
                      value={item.acrescimoPercentual || 0}
                      onChange={(e) => atualizarAcrescimo(index, 'perc', e.target.value)}
                      className="w-full px-2 py-1 rounded bg-[#0d0f12] border border-[#2a2f35] text-right text-xs text-gray-200"
                      min="0"
                    />
                  </div>
                  <div className="col-span-1">
                    <input
                      type="number"
                      value={(item.acrescimoValor || 0).toFixed(2)}
                      onChange={(e) => atualizarAcrescimo(index, 'valor', e.target.value)}
                      className="w-full px-2 py-1 rounded bg-[#0d0f12] border border-[#2a2f35] text-right text-xs text-gray-200"
                      min="0"
                    />
                  </div>
                  <div className="col-span-2 text-right font-bold text-green-400 text-sm">
                    R$ {item.total.toFixed(2)}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        <div className="w-96 flex flex-col gap-4">
          <div className="bg-[#111418] p-4 rounded border border-[#1f2429] shadow space-y-3 text-gray-200">
            <div className="flex justify-between text-xs text-gray-400">
              <span>Subtotal:</span>
              <span className="font-bold text-gray-100">
                R$ {calcularSubtotal().toFixed(2)}
              </span>
            </div>

            <div className="grid grid-cols-2 gap-2 text-xs">
              <div>
                <label className="text-[11px] text-gray-400">Desc. Global %</label>
                <input
                  type="number"
                  value={descontoGlobalPerc}
                  onChange={(e) => setDescontoGlobalPerc(e.target.value)}
                  className="w-full px-2 py-1 rounded bg-[#0d0f12] border border-[#2a2f35] text-right text-xs text-gray-200"
                  min="0"
                  max="100"
                />
              </div>
              <div>
                <label className="text-[11px] text-gray-400">Desc. Global R$</label>
                <input
                  type="number"
                  value={descontoGlobalValor}
                  onChange={(e) => setDescontoGlobalValor(e.target.value)}
                  className="w-full px-2 py-1 rounded bg-[#0d0f12] border border-[#2a2f35] text-right text-xs text-gray-200"
                  min="0"
                />
              </div>
              <div>
                <label className="text-[11px] text-gray-400">Acrés. Global %</label>
                <input
                  type="number"
                  value={acrescimoGlobalPerc}
                  onChange={(e) => setAcrescimoGlobalPerc(e.target.value)}
                  className="w-full px-2 py-1 rounded bg-[#0d0f12] border border-[#2a2f35] text-right text-xs text-gray-200"
                  min="0"
                />
              </div>
              <div>
                <label className="text-[11px] text-gray-400">Acrés. Global R$</label>
                <input
                  type="number"
                  value={acrescimoGlobalValor}
                  onChange={(e) => setAcrescimoGlobalValor(e.target.value)}
                  className="w-full px-2 py-1 rounded bg-[#0d0f12] border border-[#2a2f35] text-right text-xs text-gray-200"
                  min="0"
                />
              </div>
            </div>

            <div>
              <label className="text-[11px] text-gray-400">Frete</label>
              <input
                type="number"
                value={frete}
                onChange={(e) => setFrete(e.target.value)}
                className="w-full px-2 py-1 rounded bg-[#0d0f12] border border-[#2a2f35] text-right text-xs text-gray-200"
                min="0"
              />
            </div>

            <div className="border-t border-[#1f2429] pt-3 mt-2">
              <div className="flex justify-between items-center">
                <span className="text-base font-semibold text-gray-300">TOTAL:</span>
                <span className="text-2xl font-bold text-green-400">
                  R$ {calcularTotal().toFixed(2)}
                </span>
              </div>
            </div>
          </div>

          <div className="flex flex-col gap-2">
            <button
              onClick={abrirModalPagamento}
              className="bg-green-600 text-white font-bold py-4 px-4 rounded hover:bg-green-500 text-lg shadow-lg shadow-green-600/20"
            >
              F2 - Finalizar Venda
            </button>
            <button
              onClick={limparVenda}
              className="bg-red-600/30 text-red-400 font-bold py-3 px-4 rounded border border-red-600/40 hover:bg-red-600/40"
            >
              Cancelar Venda
            </button>
          </div>
        </div>
      </div>

      {mostrarModalPagamento && (
        <ModalPagamento
          valorTotal={calcularTotal()}
          formasPagamento={formasPagamento}
          onConfirmar={finalizarVenda}
          onCancelar={() => setMostrarModalPagamento(false)}
        />
      )}

      {mostrarCupom && vendaFinalizada && empresa && (
        <CupomNaoFiscal
          venda={vendaFinalizada}
          empresa={empresa}
          onClose={() => setMostrarCupom(false)}
        />
      )}
    </div>
  );
}
