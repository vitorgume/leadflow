import api from './api';
import type { UsuarioCompletoDTO } from '../types/usuarioConfig';
import type { ResponseDto } from '../types/auth'; // Reaproveitando a interface base

export const usuarioConfigService = {
  buscar: async (id: string): Promise<UsuarioCompletoDTO> => {
    const response = await api.get<ResponseDto<UsuarioCompletoDTO>>(`/usuarios/${id}`);
    return response.data.dado;
  },

  alterar: async (id: string, payload: UsuarioCompletoDTO): Promise<UsuarioCompletoDTO> => {
    const response = await api.put<ResponseDto<UsuarioCompletoDTO>>(`/usuarios/${id}`, payload);
    return response.data.dado;
  }
};
