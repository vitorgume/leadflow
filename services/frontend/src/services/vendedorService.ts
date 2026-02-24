// src/services/vendedorService.ts
import api from './api';
import type { Vendedor, VendedorCreateDTO, VendedorUpdateDTO, ResponseDto } from '../types/vendedor';

const BASE_URL = '/vendedores';

/**
 * Busca todos os vendedores.
 * Retorna uma lista de Vendedor, encapsulada em ResponseDto.
 */
export const getVendedores = async (idUsuario: string): Promise<Vendedor[]> => {
  const response = await api.get<ResponseDto<Vendedor[]>>(`${BASE_URL}/${idUsuario}`);
  if (response.data.erro) {
    throw new Error(response.data.erro.mensagens.join(', '));
  }
  return response.data.dado;
};

/**
 * Cria um novo vendedor.
 * Retorna o Vendedor criado, encapsulado em ResponseDto.
 */
export const createVendedor = async (vendedorData: VendedorCreateDTO): Promise<Vendedor> => {
  const response = await api.post<ResponseDto<Vendedor>>(BASE_URL, vendedorData);
  if (response.data.erro) {
    throw new Error(response.data.erro.mensagens.join(', '));
  }
  return response.data.dado;
};

/**
 * Atualiza um vendedor existente.
 * Retorna o Vendedor atualizado, encapsulado em ResponseDto.
 */
export const updateVendedor = async (id: number, vendedorData: VendedorUpdateDTO): Promise<Vendedor> => {
  const response = await api.put<ResponseDto<Vendedor>>(`${BASE_URL}/${id}`, vendedorData);
  if (response.data.erro) {
    throw new Error(response.data.erro.mensagens.join(', '));
  }
  return response.data.dado;
};

/**
 * Deleta um vendedor.
 * Retorna void, verifica por erros no ResponseDto.
 */
export const deleteVendedor = async (id: number): Promise<void> => {
  const response = await api.delete<ResponseDto<void>>(`${BASE_URL}/${id}`);
  if (response.data.erro) {
    throw new Error(response.data.erro.mensagens.join(', '));
  }
};
