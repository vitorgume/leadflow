import api from './api';
import type { ConfiguracaoEscolhaVendedorDTO } from '../types/configuracaoVendedor';

export const configVendedorService = {
  // Lista as configurações paginadas
  listar: async (idUsuario: string) => {
    const response = await api.get(`/configuracoes-escolha-vendedores/listar/${idUsuario}?size=100`);
    return response.data.dado.content; // Supondo retorno paginado do Spring (Page<T>)
  },
  
  cadastrar: async (data: ConfiguracaoEscolhaVendedorDTO) => {
    const response = await api.post('/configuracoes-escolha-vendedores', data);
    return response.data.dado;
  },
  
  alterar: async (id: string, data: ConfiguracaoEscolhaVendedorDTO) => {
    const response = await api.put(`/configuracoes-escolha-vendedores/${id}`, data);
    return response.data.dado;
  },
  
  deletar: async (id: string) => {
    await api.delete(`/configuracoes-escolha-vendedores/${id}`);
  },

  // Busca o usuário para pegarmos os atributos de qualificação
  buscarUsuario: async (idUsuario: string) => {
    const response = await api.get(`/usuarios/${idUsuario}`);
    return response.data.dado;
  },

  // Busca a lista de vendedores disponíveis para vincular
  buscarVendedores: async (idUsuario: string) => {
    const response = await api.get(`/vendedores/${idUsuario}`);
    return response.data.dado || response.data; // Depende se o seu controller envelopa com ResponseDto
  }
};