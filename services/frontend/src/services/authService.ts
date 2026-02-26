import api from './api';
import type { LoginDTO, LoginResponseDTO, ResponseDto } from '../types/auth';

const extractDado = (response: any) => {
  if (response.data && response.data.erro) {
    throw new Error(response.data.erro.mensagens.join(', '));
  }
  return response.data.dado;
};

export const login = async (data: LoginDTO): Promise<LoginResponseDTO> => {
  const response = await api.post<ResponseDto<LoginResponseDTO>>('/login', data);
  return extractDado(response);
};
