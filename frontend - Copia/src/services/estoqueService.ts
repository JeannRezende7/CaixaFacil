import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
});

export const estoqueService = {
  entrada: (produtoId: number, quantidade: number, observacao?: string) =>
    api.post("/estoque/entrada", null, {
      params: { produtoId, quantidade, observacao },
    }),

  ajuste: (produtoId: number, quantidade: number, motivo: string) =>
    api.post("/estoque/ajuste", null, {
      params: { produtoId, quantidade, motivo },
    }),

  consultar: (produtoId: number) =>
    api.get(`/estoque/consultar`, { params: { produtoId } }),
};
