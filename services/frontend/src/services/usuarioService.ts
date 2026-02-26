import api from './api';
import type { UsuarioCreateDTO, Usuario, ResponseDto } from '../types/usuario';

const extractDado = (response: any) => {
  if (response.data && response.data.erro) {
    throw new Error(response.data.erro.mensagens.join(', '));
  }
  return response.data.dado;
};

export const cadastrarUsuario = async (data: UsuarioCreateDTO): Promise<Usuario> => {
  const response = await api.post<ResponseDto<Usuario>>('/usuarios/cadastro', data);
  return extractDado(response);
};
