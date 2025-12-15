import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import axios from 'axios';
import { getApiBaseUrl } from '../utils/apiConfig';

interface HeaderPadraoProps {
  titulo: string;
  mostrarVoltar?: boolean;
  acoes?: React.ReactNode;
}

export default function HeaderPadrao({ titulo, mostrarVoltar = true, acoes }: HeaderPadraoProps) {
  const navigate = useNavigate();
  const [empresa, setEmpresa] = useState<any>(null);
  const [usuario, setUsuario] = useState<any>(null);

  useEffect(() => {
    const usuarioStr = localStorage.getItem('usuario');
    if (usuarioStr) {
      setUsuario(JSON.parse(usuarioStr));
    }

    axios.get(`${getApiBaseUrl()}/configuracao`)
      .then((res) => setEmpresa(res.data))
      .catch(() => {});
  }, []);

  return (
    <header className="flex items-center justify-between px-6 py-3 bg-white border-b border-gray-200 shadow-sm">
      <div className="flex items-center gap-2">
        <div className="w-8 h-8 rounded-md bg-emerald-500 flex items-center justify-center text-white">
          🧾
        </div>
        <div className="flex flex-col">
          <span className="text-xl font-semibold">
            {empresa?.nomeFantasia || 'Caixa Fácil'}
          </span>
          <span className="text-xs text-gray-500">{titulo}</span>
        </div>
      </div>

      <div className="flex items-center gap-4">
        {usuario && (
          <span className="text-sm text-gray-700">
            Usuário: <span className="font-medium">{usuario.nome}</span>
          </span>
        )}
        
        {acoes}

        {mostrarVoltar && (
          <button
            type="button"
            onClick={() => navigate('/pdv')}
            className="px-4 py-2 rounded-lg border border-gray-300 bg-white hover:bg-gray-50 text-sm font-medium transition-colors"
          >
            Voltar ao PDV
          </button>
        )}
      </div>
    </header>
  );
}
