import api from './api';
import type { ResponseDto } from '../types/auth'; // Reaproveitando sua interface base
import type { MembroDTO } from '../types/outrosSetores';

export const membroService = {
  listar: async (idUsuario: string): Promise<MembroDTO[]> => {
    const response = await api.get<ResponseDto<MembroDTO[]>>(`/membros/${idUsuario}`);
    return response.data.dado;
  },

  cadastrar: async (payload: MembroDTO): Promise<MembroDTO> => {
    const response = await api.post<ResponseDto<MembroDTO>>('/membros', payload);
    return response.data.dado;
  },

  alterar: async (id: string, payload: MembroDTO): Promise<MembroDTO> => {
    const response = await api.put<ResponseDto<MembroDTO>>(`/membros/${id}`, payload);
    return response.data.dado;
  },

  deletar: async (id: string): Promise<void> => {
    await api.delete(`/membros/${id}`);
  }
};