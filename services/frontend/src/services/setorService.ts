import api from './api';
import type { ResponseDto } from '../types/auth'; 
import type { SetorDTO } from '../types/outrosSetores';

export const setorService = {
  listar: async (idUsuario: string): Promise<SetorDTO[]> => {
    const response = await api.get<ResponseDto<SetorDTO[]>>(`/setores/${idUsuario}`);
    return response.data.dado;
  },

  cadastrar: async (payload: SetorDTO): Promise<SetorDTO> => {
    const response = await api.post<ResponseDto<SetorDTO>>('/setores', payload);
    return response.data.dado;
  },

  alterar: async (id: string, payload: SetorDTO): Promise<SetorDTO> => {
    const response = await api.put<ResponseDto<SetorDTO>>(`/setores/${id}`, payload);
    return response.data.dado;
  },

  deletar: async (id: string): Promise<void> => {
    await api.delete(`/setores/${id}`);
  }
};