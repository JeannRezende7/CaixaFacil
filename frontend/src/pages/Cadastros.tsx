import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { produtoService, clienteService, cadastrosService } from '../services/api';
import { Produto, Cliente, Categoria, Usuario, FormaPagamento } from '../types';

export default function Cadastros() {
  const navigate = useNavigate();
  const [aba, setAba] = useState<'produtos' | 'clientes' | 'usuarios' | 'categorias' | 'formasPagamento'>('produtos');
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [formasPagamento, setFormasPagamento] = useState<FormaPagamento[]>([]);
  
  const [produtoForm, setProdutoForm] = useState<Partial<Produto>>({
    codigo: '',
    descricao: '',
    unidade: 'UN',
    precoVenda: 0,
    precoCusto: 0,
    estoque: 0,
    estoqueMinimo: 0,
    controlarEstoque: true,
    ativo: true,
  });
  
  const [clienteForm, setClienteForm] = useState<Partial<Cliente>>({
    nome: '',
    ativo: true,
  });
  
  const [usuarioForm, setUsuarioForm] = useState<Partial<Usuario>>({
    login: '',
    senha: '',
    nome: '',
    admin: false,
    ativo: true,
  });
  
  const [categoriaForm, setCategoriaForm] = useState<Partial<Categoria>>({
    descricao: '',
    ativo: true,
  });
  
  const [formaPagamentoForm, setFormaPagamentoForm] = useState<Partial<FormaPagamento>>({
    descricao: '',
    tipoPagamento: '99',
    permiteParcelamento: false,
    ativo: true,
  });

  useEffect(() => {
    carregarDados();
  }, [aba]);

  const carregarDados = async () => {
    try {
      if (aba === 'produtos') {
        const [prods, cats] = await Promise.all([
          produtoService.listar(),
          cadastrosService.listarCategorias(),
        ]);
        setProdutos(prods.data);
        setCategorias(cats.data);
      } else if (aba === 'clientes') {
        const res = await clienteService.listar();
        setClientes(res.data);
      } else if (aba === 'usuarios') {
        const res = await cadastrosService.listarUsuarios();
        setUsuarios(res.data);
      } else if (aba === 'categorias') {
        const res = await cadastrosService.listarCategorias();
        setCategorias(res.data);
      } else if (aba === 'formasPagamento') {
        const res = await cadastrosService.listarFormasPagamento();
        setFormasPagamento(res.data);
      }
    } catch (error) {
      console.error('Erro ao carregar dados:', error);
    }
  };

  const salvarProduto = async () => {
    if (!produtoForm.codigo || !produtoForm.descricao) {
      alert('Preencha código e descrição');
      return;
    }
    
    try {
      if (produtoForm.id) {
        await produtoService.atualizar(produtoForm.id, produtoForm as Produto);
      } else {
        await produtoService.criar(produtoForm as Produto);
      }
      alert('Produto salvo!');
      setProdutoForm({
        codigo: '',
        descricao: '',
        unidade: 'UN',
        precoVenda: 0,
        precoCusto: 0,
        estoque: 0,
        estoqueMinimo: 0,
        controlarEstoque: true,
        ativo: true,
      });
      carregarDados();
    } catch (error) {
      alert('Erro ao salvar produto');
    }
  };

  const salvarCliente = async () => {
    if (!clienteForm.nome) {
      alert('Preencha o nome');
      return;
    }
    
    try {
      if (clienteForm.id) {
        await clienteService.atualizar(clienteForm.id, clienteForm as Cliente);
      } else {
        await clienteService.criar(clienteForm as Cliente);
      }
      alert('Cliente salvo!');
      setClienteForm({ nome: '', ativo: true });
      carregarDados();
    } catch (error) {
      alert('Erro ao salvar cliente');
    }
  };
  
  const salvarUsuario = async () => {
    if (!usuarioForm.login || !usuarioForm.senha || !usuarioForm.nome) {
      alert('Preencha login, senha e nome');
      return;
    }
    
    try {
      if (usuarioForm.id) {
        await cadastrosService.atualizarUsuario(usuarioForm.id, usuarioForm as Usuario);
      } else {
        await cadastrosService.criarUsuario(usuarioForm as Usuario);
      }
      alert('Usuário salvo!');
      setUsuarioForm({ login: '', senha: '', nome: '', admin: false, ativo: true });
      carregarDados();
    } catch (error) {
      alert('Erro ao salvar usuário');
    }
  };
  
  const salvarCategoria = async () => {
    if (!categoriaForm.descricao) {
      alert('Preencha a descrição');
      return;
    }
    
    try {
      await cadastrosService.criarCategoria(categoriaForm as Categoria);
      alert('Categoria salva!');
      setCategoriaForm({ descricao: '', ativo: true });
      carregarDados();
    } catch (error) {
      alert('Erro ao salvar categoria');
    }
  };
  
  const salvarFormaPagamento = async () => {
    if (!formaPagamentoForm.descricao) {
      alert('Preencha a descrição');
      return;
    }
    
    try {
      await cadastrosService.criarFormaPagamento(formaPagamentoForm as FormaPagamento);
      alert('Forma de pagamento salva!');
      setFormaPagamentoForm({ descricao: '', permiteParcelamento: false, ativo: true });
      carregarDados();
    } catch (error) {
      alert('Erro ao salvar forma de pagamento');
    }
  };

  return (
    <div className="h-screen flex flex-col bg-gray-100">
      <div className="bg-primary text-white p-4 flex justify-between items-center">
        <h1 className="text-2xl font-bold">Cadastros</h1>
        <button
          onClick={() => navigate('/pdv')}
          className="bg-white text-primary px-4 py-2 rounded hover:bg-gray-100"
        >
          Voltar ao PDV
        </button>
      </div>

      <div className="p-4">
        <div className="bg-white rounded shadow">
          <div className="flex border-b overflow-x-auto">
            <button
              onClick={() => setAba('produtos')}
              className={`px-6 py-3 font-bold whitespace-nowrap ${
                aba === 'produtos' ? 'bg-primary text-white' : 'hover:bg-gray-100'
              }`}
            >
              Produtos
            </button>
            <button
              onClick={() => setAba('clientes')}
              className={`px-6 py-3 font-bold whitespace-nowrap ${
                aba === 'clientes' ? 'bg-primary text-white' : 'hover:bg-gray-100'
              }`}
            >
              Clientes
            </button>
            <button
              onClick={() => setAba('usuarios')}
              className={`px-6 py-3 font-bold whitespace-nowrap ${
                aba === 'usuarios' ? 'bg-primary text-white' : 'hover:bg-gray-100'
              }`}
            >
              Usuários
            </button>
            <button
              onClick={() => setAba('categorias')}
              className={`px-6 py-3 font-bold whitespace-nowrap ${
                aba === 'categorias' ? 'bg-primary text-white' : 'hover:bg-gray-100'
              }`}
            >
              Categorias
            </button>
            <button
              onClick={() => setAba('formasPagamento')}
              className={`px-6 py-3 font-bold whitespace-nowrap ${
                aba === 'formasPagamento' ? 'bg-primary text-white' : 'hover:bg-gray-100'
              }`}
            >
              Formas Pagamento
            </button>
          </div>

          <div className="p-4">
            {aba === 'produtos' && (
              <div>
                <h3 className="text-lg font-bold mb-4">Cadastro de Produto</h3>
                <div className="grid grid-cols-3 gap-4 mb-4">
                  <div>
                    <label className="block text-sm font-bold mb-1">Código*</label>
                    <input
                      type="text"
                      value={produtoForm.codigo}
                      onChange={(e) => setProdutoForm({...produtoForm, codigo: e.target.value})}
                      className="w-full px-3 py-2 border rounded"
                    />
                  </div>
                  <div className="col-span-2">
                    <label className="block text-sm font-bold mb-1">Descrição*</label>
                    <input
                      type="text"
                      value={produtoForm.descricao}
                      onChange={(e) => setProdutoForm({...produtoForm, descricao: e.target.value})}
                      className="w-full px-3 py-2 border rounded"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-bold mb-1">Unidade</label>
                    <input
                      type="text"
                      value={produtoForm.unidade}
                      onChange={(e) => setProdutoForm({...produtoForm, unidade: e.target.value})}
                      className="w-full px-3 py-2 border rounded"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-bold mb-1">Preço Venda</label>
                    <input
                      type="number"
                      value={produtoForm.precoVenda}
                      onChange={(e) => setProdutoForm({...produtoForm, precoVenda: Number(e.target.value)})}
                      className="w-full px-3 py-2 border rounded"
                      step="0.01"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-bold mb-1">Preço Custo</label>
                    <input
                      type="number"
                      value={produtoForm.precoCusto}
                      onChange={(e) => setProdutoForm({...produtoForm, precoCusto: Number(e.target.value)})}
                      className="w-full px-3 py-2 border rounded"
                      step="0.01"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-bold mb-1">Estoque</label>
                    <input
                      type="number"
                      value={produtoForm.estoque}
                      onChange={(e) => setProdutoForm({...produtoForm, estoque: Number(e.target.value)})}
                      className="w-full px-3 py-2 border rounded"
                      step="0.001"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-bold mb-1">Estoque Mínimo</label>
                    <input
                      type="number"
                      value={produtoForm.estoqueMinimo}
                      onChange={(e) => setProdutoForm({...produtoForm, estoqueMinimo: Number(e.target.value)})}
                      className="w-full px-3 py-2 border rounded"
                      step="0.001"
                    />
                  </div>
                  <div className="flex items-end">
                    <label className="flex items-center">
                      <input
                        type="checkbox"
                        checked={produtoForm.controlarEstoque}
                        onChange={(e) => setProdutoForm({...produtoForm, controlarEstoque: e.target.checked})}
                        className="mr-2"
                      />
                      <span className="text-sm font-bold">Controlar Estoque</span>
                    </label>
                  </div>
                </div>
                
                <div className="flex gap-2">
                  <button
                    onClick={salvarProduto}
                    className="bg-primary text-white px-6 py-2 rounded hover:bg-green-600 font-bold"
                  >
                    Salvar
                  </button>
                  <button
                    onClick={() => setProdutoForm({
                      codigo: '',
                      descricao: '',
                      unidade: 'UN',
                      precoVenda: 0,
                      precoCusto: 0,
                      estoque: 0,
                      estoqueMinimo: 0,
                      controlarEstoque: true,
                      ativo: true,
                    })}
                    className="bg-gray-500 text-white px-6 py-2 rounded hover:bg-gray-600"
                  >
                    Limpar
                  </button>
                </div>

                <div className="mt-6">
                  <h4 className="font-bold mb-2">Produtos Cadastrados</h4>
                  <div className="border rounded max-h-96 overflow-y-auto">
                    <table className="w-full">
                      <thead className="bg-gray-200">
                        <tr>
                          <th className="p-2 text-left">Código</th>
                          <th className="p-2 text-left">Descrição</th>
                          <th className="p-2 text-right">Preço</th>
                          <th className="p-2 text-right">Estoque</th>
                        </tr>
                      </thead>
                      <tbody>
                        {produtos.map(p => (
                          <tr key={p.id} className="border-t hover:bg-gray-50">
                            <td className="p-2">{p.codigo}</td>
                            <td className="p-2">{p.descricao}</td>
                            <td className="p-2 text-right">R$ {p.precoVenda.toFixed(2)}</td>
                            <td className="p-2 text-right">{p.estoque.toFixed(3)}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            )}

            {aba === 'clientes' && (
              <div>
                <h3 className="text-lg font-bold mb-4">Cadastro de Cliente</h3>
                <div className="grid grid-cols-3 gap-4 mb-4">
                  <div>
                    <label className="block text-sm font-bold mb-1">Código</label>
                    <input
                      type="text"
                      value={clienteForm.codigo || ''}
                      onChange={(e) => setClienteForm({...clienteForm, codigo: e.target.value})}
                      className="w-full px-3 py-2 border rounded"
                    />
                  </div>
                  <div className="col-span-2">
                    <label className="block text-sm font-bold mb-1">Nome*</label>
                    <input
                      type="text"
                      value={clienteForm.nome}
                      onChange={(e) => setClienteForm({...clienteForm, nome: e.target.value})}
                      className="w-full px-3 py-2 border rounded"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-bold mb-1">CPF/CNPJ</label>
                    <input
                      type="text"
                      value={clienteForm.cpfCnpj || ''}
                      onChange={(e) => setClienteForm({...clienteForm, cpfCnpj: e.target.value})}
                      className="w-full px-3 py-2 border rounded"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-bold mb-1">Telefone</label>
                    <input
                      type="text"
                      value={clienteForm.telefone || ''}
                      onChange={(e) => setClienteForm({...clienteForm, telefone: e.target.value})}
                      className="w-full px-3 py-2 border rounded"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-bold mb-1">Email</label>
                    <input
                      type="email"
                      value={clienteForm.email || ''}
                      onChange={(e) => setClienteForm({...clienteForm, email: e.target.value})}
                      className="w-full px-3 py-2 border rounded"
                    />
                  </div>
                </div>
                
                <div className="flex gap-2">
                  <button
                    onClick={salvarCliente}
                    className="bg-primary text-white px-6 py-2 rounded hover:bg-green-600 font-bold"
                  >
                    Salvar
                  </button>
                  <button
                    onClick={() => setClienteForm({ nome: '', ativo: true })}
                    className="bg-gray-500 text-white px-6 py-2 rounded hover:bg-gray-600"
                  >
                    Limpar
                  </button>
                </div>

                <div className="mt-6">
                  <h4 className="font-bold mb-2">Clientes Cadastrados</h4>
                  <div className="border rounded max-h-96 overflow-y-auto">
                    <table className="w-full">
                      <thead className="bg-gray-200">
                        <tr>
                          <th className="p-2 text-left">Código</th>
                          <th className="p-2 text-left">Nome</th>
                          <th className="p-2 text-left">CPF/CNPJ</th>
                          <th className="p-2 text-left">Telefone</th>
                        </tr>
                      </thead>
                      <tbody>
                        {clientes.map(c => (
                          <tr key={c.id} className="border-t hover:bg-gray-50">
                            <td className="p-2">{c.codigo}</td>
                            <td className="p-2">{c.nome}</td>
                            <td className="p-2">{c.cpfCnpj}</td>
                            <td className="p-2">{c.telefone}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            )}

            {aba === 'usuarios' && (
              <div>
                <h3 className="text-lg font-bold mb-4">Cadastro de Usuário</h3>
                <div className="grid grid-cols-3 gap-4 mb-4">
                  <div>
                    <label className="block text-sm font-bold mb-1">Login*</label>
                    <input
                      type="text"
                      value={usuarioForm.login}
                      onChange={(e) => setUsuarioForm({...usuarioForm, login: e.target.value})}
                      className="w-full px-3 py-2 border rounded"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-bold mb-1">Senha*</label>
                    <input
                      type="password"
                      value={usuarioForm.senha}
                      onChange={(e) => setUsuarioForm({...usuarioForm, senha: e.target.value})}
                      className="w-full px-3 py-2 border rounded"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-bold mb-1">Nome*</label>
                    <input
                      type="text"
                      value={usuarioForm.nome}
                      onChange={(e) => setUsuarioForm({...usuarioForm, nome: e.target.value})}
                      className="w-full px-3 py-2 border rounded"
                    />
                  </div>
                  <div className="flex items-end gap-4">
                    <label className="flex items-center">
                      <input
                        type="checkbox"
                        checked={usuarioForm.admin}
                        onChange={(e) => setUsuarioForm({...usuarioForm, admin: e.target.checked})}
                        className="mr-2"
                      />
                      <span className="text-sm font-bold">Administrador</span>
                    </label>
                    <label className="flex items-center">
                      <input
                        type="checkbox"
                        checked={usuarioForm.ativo}
                        onChange={(e) => setUsuarioForm({...usuarioForm, ativo: e.target.checked})}
                        className="mr-2"
                      />
                      <span className="text-sm font-bold">Ativo</span>
                    </label>
                  </div>
                </div>
                
                <div className="flex gap-2">
                  <button
                    onClick={salvarUsuario}
                    className="bg-primary text-white px-6 py-2 rounded hover:bg-green-600 font-bold"
                  >
                    Salvar
                  </button>
                  <button
                    onClick={() => setUsuarioForm({ login: '', senha: '', nome: '', admin: false, ativo: true })}
                    className="bg-gray-500 text-white px-6 py-2 rounded hover:bg-gray-600"
                  >
                    Limpar
                  </button>
                </div>

                <div className="mt-6">
                  <h4 className="font-bold mb-2">Usuários Cadastrados</h4>
                  <div className="border rounded max-h-96 overflow-y-auto">
                    <table className="w-full">
                      <thead className="bg-gray-200">
                        <tr>
                          <th className="p-2 text-left">Login</th>
                          <th className="p-2 text-left">Nome</th>
                          <th className="p-2 text-center">Admin</th>
                          <th className="p-2 text-center">Ativo</th>
                        </tr>
                      </thead>
                      <tbody>
                        {usuarios.map(u => (
                          <tr key={u.id} className="border-t hover:bg-gray-50">
                            <td className="p-2">{u.login}</td>
                            <td className="p-2">{u.nome}</td>
                            <td className="p-2 text-center">{u.admin ? '✓' : ''}</td>
                            <td className="p-2 text-center">{u.ativo ? '✓' : ''}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            )}

            {aba === 'categorias' && (
              <div>
                <h3 className="text-lg font-bold mb-4">Cadastro de Categoria</h3>
                <div className="grid grid-cols-2 gap-4 mb-4">
                  <div>
                    <label className="block text-sm font-bold mb-1">Descrição*</label>
                    <input
                      type="text"
                      value={categoriaForm.descricao}
                      onChange={(e) => setCategoriaForm({...categoriaForm, descricao: e.target.value})}
                      className="w-full px-3 py-2 border rounded"
                    />
                  </div>
                  <div className="flex items-end">
                    <label className="flex items-center">
                      <input
                        type="checkbox"
                        checked={categoriaForm.ativo}
                        onChange={(e) => setCategoriaForm({...categoriaForm, ativo: e.target.checked})}
                        className="mr-2"
                      />
                      <span className="text-sm font-bold">Ativo</span>
                    </label>
                  </div>
                </div>
                
                <div className="flex gap-2">
                  <button
                    onClick={salvarCategoria}
                    className="bg-primary text-white px-6 py-2 rounded hover:bg-green-600 font-bold"
                  >
                    Salvar
                  </button>
                  <button
                    onClick={() => setCategoriaForm({ descricao: '', ativo: true })}
                    className="bg-gray-500 text-white px-6 py-2 rounded hover:bg-gray-600"
                  >
                    Limpar
                  </button>
                </div>

                <div className="mt-6">
                  <h4 className="font-bold mb-2">Categorias Cadastradas</h4>
                  <div className="border rounded max-h-96 overflow-y-auto">
                    <table className="w-full">
                      <thead className="bg-gray-200">
                        <tr>
                          <th className="p-2 text-left">Descrição</th>
                          <th className="p-2 text-center">Ativo</th>
                        </tr>
                      </thead>
                      <tbody>
                        {categorias.map(c => (
                          <tr key={c.id} className="border-t hover:bg-gray-50">
                            <td className="p-2">{c.descricao}</td>
                            <td className="p-2 text-center">{c.ativo ? '✓' : ''}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            )}

            {aba === 'formasPagamento' && (
              <div>
                <h3 className="text-lg font-bold mb-4">Cadastro de Forma de Pagamento</h3>
                <div className="grid grid-cols-3 gap-4 mb-4">
                  <div>
                    <label className="block text-sm font-bold mb-1">Descrição*</label>
                    <input
                      type="text"
                      value={formaPagamentoForm.descricao}
                      onChange={(e) => setFormaPagamentoForm({...formaPagamentoForm, descricao: e.target.value})}
                      className="w-full px-3 py-2 border rounded"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-bold mb-1">Tipo de Pagamento*</label>
                    <select
                      value={formaPagamentoForm.tipoPagamento}
                      onChange={(e) => setFormaPagamentoForm({...formaPagamentoForm, tipoPagamento: e.target.value})}
                      className="w-full px-3 py-2 border rounded"
                    >
                      <option value="01">01 - Dinheiro</option>
                      <option value="02">02 - Cheque</option>
                      <option value="03">03 - Cartão de Crédito</option>
                      <option value="04">04 - Cartão de Débito</option>
                      <option value="05">05 - Crédito Loja</option>
                      <option value="10">10 - Vale Alimentação</option>
                      <option value="11">11 - Vale Refeição</option>
                      <option value="12">12 - Vale Presente</option>
                      <option value="13">13 - Vale Combustível</option>
                      <option value="15">15 - Boleto Bancário</option>
                      <option value="16">16 - Depósito Bancário</option>
                      <option value="17">17 - PIX</option>
                      <option value="18">18 - Transferência Bancária</option>
                      <option value="19">19 - Programa de Fidelidade</option>
                      <option value="90">90 - Sem pagamento</option>
                      <option value="99">99 - Outros</option>
                    </select>
                  </div>
                  <div className="flex items-end gap-4">
                    <label className="flex items-center">
                      <input
                        type="checkbox"
                        checked={formaPagamentoForm.permiteParcelamento}
                        onChange={(e) => setFormaPagamentoForm({...formaPagamentoForm, permiteParcelamento: e.target.checked})}
                        className="mr-2"
                      />
                      <span className="text-sm font-bold">Permite Parcelamento</span>
                    </label>
                    <label className="flex items-center">
                      <input
                        type="checkbox"
                        checked={formaPagamentoForm.ativo}
                        onChange={(e) => setFormaPagamentoForm({...formaPagamentoForm, ativo: e.target.checked})}
                        className="mr-2"
                      />
                      <span className="text-sm font-bold">Ativo</span>
                    </label>
                  </div>
                </div>
                
                <div className="flex gap-2">
                  <button
                    onClick={salvarFormaPagamento}
                    className="bg-primary text-white px-6 py-2 rounded hover:bg-green-600 font-bold"
                  >
                    Salvar
                  </button>
                  <button
                    onClick={() => setFormaPagamentoForm({ descricao: '', tipoPagamento: '99', permiteParcelamento: false, ativo: true })}
                    className="bg-gray-500 text-white px-6 py-2 rounded hover:bg-gray-600"
                  >
                    Limpar
                  </button>
                </div>

                <div className="mt-6">
                  <h4 className="font-bold mb-2">Formas de Pagamento Cadastradas</h4>
                  <div className="border rounded max-h-96 overflow-y-auto">
                    <table className="w-full">
                      <thead className="bg-gray-200">
                        <tr>
                          <th className="p-2 text-left">Descrição</th>
                          <th className="p-2 text-left">Tipo</th>
                          <th className="p-2 text-center">Parcelamento</th>
                          <th className="p-2 text-center">Ativo</th>
                        </tr>
                      </thead>
                      <tbody>
                        {formasPagamento.map(fp => (
                          <tr key={fp.id} className="border-t hover:bg-gray-50">
                            <td className="p-2">{fp.descricao}</td>
                            <td className="p-2">{fp.tipoPagamento}</td>
                            <td className="p-2 text-center">{fp.permiteParcelamento ? '✓' : ''}</td>
                            <td className="p-2 text-center">{fp.ativo ? '✓' : ''}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
