import { useRef } from 'react';
import { useReactToPrint } from 'react-to-print';

interface RelatorioProps {
  caixa: any;
  relatorio: any;
  onClose: () => void;
}

export default function RelatorioFechamentoCaixa({ caixa, relatorio, onClose }: RelatorioProps) {
  const componentRef = useRef<HTMLDivElement>(null);

  const handlePrint = useReactToPrint({
    content: () => componentRef.current,
  });

  const calcularSaldoFinal = () => {
    return caixa.valorAbertura + caixa.valorVendas + caixa.valorSuprimentos - caixa.valorSangrias;
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-4xl max-h-[90vh] overflow-y-auto">
        <div className="p-4 border-b flex justify-between items-center bg-primary text-white">
          <h2 className="text-xl font-bold">Relatório de Fechamento de Caixa</h2>
          <button onClick={onClose} className="text-2xl hover:text-gray-300">&times;</button>
        </div>

        <div className="p-6">
          {/* Botões de ação */}
          <div className="flex gap-2 mb-4">
            <button
              onClick={handlePrint}
              className="bg-primary text-white px-6 py-2 rounded hover:bg-green-600 font-bold"
            >
              🖨️ Imprimir
            </button>
            <button
              onClick={onClose}
              className="bg-gray-500 text-white px-6 py-2 rounded hover:bg-gray-600"
            >
              Fechar
            </button>
          </div>

          {/* Conteúdo para impressão */}
          <div ref={componentRef} className="p-8">
            <div className="text-center mb-6 border-b-2 pb-4">
              <h1 className="text-2xl font-bold">RELATÓRIO DE FECHAMENTO DE CAIXA</h1>
              <p className="text-gray-600 mt-2">
                Data: {new Date(caixa.dataHoraFechamento).toLocaleDateString('pt-BR')}
              </p>
            </div>

            {/* Informações do Caixa */}
            <div className="mb-6 bg-gray-50 p-4 rounded">
              <h3 className="font-bold text-lg mb-3">Informações do Caixa</h3>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-gray-600">Operador</p>
                  <p className="font-bold">{caixa.usuario.nome}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Número do Caixa</p>
                  <p className="font-bold">#{caixa.id}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Abertura</p>
                  <p className="font-bold">
                    {new Date(caixa.dataHoraAbertura).toLocaleString('pt-BR')}
                  </p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Fechamento</p>
                  <p className="font-bold">
                    {new Date(caixa.dataHoraFechamento).toLocaleString('pt-BR')}
                  </p>
                </div>
              </div>
            </div>

            {/* Resumo Geral */}
            <div className="mb-6">
              <h3 className="font-bold text-lg mb-3 border-b pb-2">Resumo Geral</h3>
              <table className="w-full">
                <tbody>
                  <tr className="border-b">
                    <td className="py-2">Valor de Abertura</td>
                    <td className="text-right font-bold">R$ {caixa.valorAbertura.toFixed(2)}</td>
                  </tr>
                  <tr className="border-b">
                    <td className="py-2 text-green-600">+ Vendas</td>
                    <td className="text-right font-bold text-green-600">R$ {caixa.valorVendas.toFixed(2)}</td>
                  </tr>
                  <tr className="border-b">
                    <td className="py-2 text-blue-600">+ Suprimentos</td>
                    <td className="text-right font-bold text-blue-600">R$ {caixa.valorSuprimentos.toFixed(2)}</td>
                  </tr>
                  <tr className="border-b">
                    <td className="py-2 text-red-600">- Sangrias</td>
                    <td className="text-right font-bold text-red-600">R$ {caixa.valorSangrias.toFixed(2)}</td>
                  </tr>
                  <tr className="border-t-2 border-black">
                    <td className="py-3 font-bold text-lg">= SALDO FINAL</td>
                    <td className="text-right font-bold text-lg text-primary">
                      R$ {calcularSaldoFinal().toFixed(2)}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            {/* Vendas por Forma de Pagamento */}
            {relatorio.vendasPorForma && Object.keys(relatorio.vendasPorForma).length > 0 && (
              <div className="mb-6">
                <h3 className="font-bold text-lg mb-3 border-b pb-2">Vendas por Forma de Pagamento</h3>
                <table className="w-full">
                  <thead className="bg-gray-100">
                    <tr>
                      <th className="text-left py-2 px-3">Forma de Pagamento</th>
                      <th className="text-right py-2 px-3">Valor</th>
                    </tr>
                  </thead>
                  <tbody>
                    {Object.entries(relatorio.vendasPorForma).map(([forma, valor]: [string, any]) => (
                      <tr key={forma} className="border-b">
                        <td className="py-2 px-3">{forma}</td>
                        <td className="text-right py-2 px-3 font-bold">R$ {valor.toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
                  <tfoot className="bg-gray-100 font-bold">
                    <tr>
                      <td className="py-2 px-3">TOTAL VENDAS</td>
                      <td className="text-right py-2 px-3">R$ {caixa.valorVendas.toFixed(2)}</td>
                    </tr>
                  </tfoot>
                </table>
              </div>
            )}

            {/* Suprimentos por Forma de Pagamento */}
            {relatorio.suprimentosPorForma && Object.keys(relatorio.suprimentosPorForma).length > 0 && (
              <div className="mb-6">
                <h3 className="font-bold text-lg mb-3 border-b pb-2">Suprimentos por Forma de Pagamento</h3>
                <table className="w-full">
                  <thead className="bg-gray-100">
                    <tr>
                      <th className="text-left py-2 px-3">Forma de Pagamento</th>
                      <th className="text-right py-2 px-3">Valor</th>
                    </tr>
                  </thead>
                  <tbody>
                    {Object.entries(relatorio.suprimentosPorForma).map(([forma, valor]: [string, any]) => (
                      <tr key={forma} className="border-b">
                        <td className="py-2 px-3">{forma}</td>
                        <td className="text-right py-2 px-3 font-bold">R$ {valor.toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
                  <tfoot className="bg-gray-100 font-bold">
                    <tr>
                      <td className="py-2 px-3">TOTAL SUPRIMENTOS</td>
                      <td className="text-right py-2 px-3">R$ {caixa.valorSuprimentos.toFixed(2)}</td>
                    </tr>
                  </tfoot>
                </table>
              </div>
            )}

            {/* Sangrias por Forma de Pagamento */}
            {relatorio.sangriasPorForma && Object.keys(relatorio.sangriasPorForma).length > 0 && (
              <div className="mb-6">
                <h3 className="font-bold text-lg mb-3 border-b pb-2">Sangrias por Forma de Pagamento</h3>
                <table className="w-full">
                  <thead className="bg-gray-100">
                    <tr>
                      <th className="text-left py-2 px-3">Forma de Pagamento</th>
                      <th className="text-right py-2 px-3">Valor</th>
                    </tr>
                  </thead>
                  <tbody>
                    {Object.entries(relatorio.sangriasPorForma).map(([forma, valor]: [string, any]) => (
                      <tr key={forma} className="border-b">
                        <td className="py-2 px-3">{forma}</td>
                        <td className="text-right py-2 px-3 font-bold">R$ {valor.toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
                  <tfoot className="bg-gray-100 font-bold">
                    <tr>
                      <td className="py-2 px-3">TOTAL SANGRIAS</td>
                      <td className="text-right py-2 px-3">R$ {caixa.valorSangrias.toFixed(2)}</td>
                    </tr>
                  </tfoot>
                </table>
              </div>
            )}

            {/* Observações */}
            {caixa.observacoesFechamento && (
              <div className="mb-6 bg-yellow-50 p-4 rounded border border-yellow-200">
                <h3 className="font-bold text-lg mb-2">Observações do Fechamento</h3>
                <p className="text-gray-700">{caixa.observacoesFechamento}</p>
              </div>
            )}

            {/* Rodapé */}
            <div className="mt-8 pt-4 border-t text-center text-sm text-gray-600">
              <p>Sistema VendeJá PDV - Relatório gerado em {new Date().toLocaleString('pt-BR')}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
