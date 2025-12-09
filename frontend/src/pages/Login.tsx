import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/api';

export default function Login() {
  const [login, setLogin] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErro('');

    try {
      const response = await authService.login(login, senha);
      localStorage.setItem('usuario', JSON.stringify(response.data));
      navigate('/pdv');
    } catch (error) {
      setErro('Login ou senha inválidos');
    }
  };

  return (
    <div className="min-h-screen bg-[#f5f7fa] text-gray-900 flex items-center justify-center px-4">
      <div className="w-full max-w-md">
        {/* Logo / título no mesmo clima do PDV */}
        <div className="flex items-center justify-center mb-6">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-lg bg-emerald-500 flex items-center justify-center text-white text-xl shadow-sm">
              🧾
            </div>
            <div className="flex flex-col">
              <span className="text-2xl font-semibold">Caixa Fácil</span>
              <span className="text-sm text-gray-500">Sistema PDV</span>
            </div>
          </div>
        </div>

        {/* Card de login */}
        <div className="bg-white border border-[#e4e7ec] rounded-2xl shadow-sm px-8 py-8">
          <h2 className="text-lg font-semibold mb-1 text-gray-800">
            Acesse sua conta
          </h2>
          <p className="text-sm text-gray-500 mb-6">
            Informe seu usuário e senha para entrar no PDV
          </p>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-gray-700 text-sm font-medium mb-1">
                Login
              </label>
              <input
                type="text"
                value={login}
                onChange={(e) => setLogin(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm
                           focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500
                           bg-white"
                autoFocus
              />
            </div>

            <div>
              <label className="block text-gray-700 text-sm font-medium mb-1">
                Senha
              </label>
              <input
                type="password"
                value={senha}
                onChange={(e) => setSenha(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm
                           focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500
                           bg-white"
              />
            </div>

            {erro && (
              <div className="p-3 bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg">
                {erro}
              </div>
            )}

            <button
              type="submit"
              className="w-full bg-emerald-600 hover:bg-emerald-500 text-white font-semibold 
                         py-3 rounded-lg text-sm shadow-sm transition-colors"
            >
              Entrar
            </button>
          </form>

          <div className="mt-4 text-xs text-gray-500 text-center">
            Pressione <span className="font-semibold">Enter</span> após informar a senha
          </div>
        </div>
      </div>
    </div>
  );
}
