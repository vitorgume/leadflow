import api from './api';
import type { OutroContato, OutroContatoCreateDTO, OutroContatoUpdateDTO, ResponseDto, SpringPage } from '../types/outroContato';

const BASE_URL = '/outros-contatos';

const extractDado = (response: any) => {
  if (response.data && response.data.erro) {
    throw new Error(response.data.erro.mensagens.join(', '));
  }
  return response.data.dado;
};

export const getOutrosContatos = async (idUsuario: string, page = 0, size = 100): Promise<OutroContato[]> => {
  const response = await api.get<ResponseDto<SpringPage<OutroContato>>>(`${BASE_URL}/listar/${idUsuario}`, {
    params: { page, size }
  });
  const pageData = extractDado(response);
  return pageData.content; // O Spring envelopa a lista dentro de "content"
};

export const createOutroContato = async (data: OutroContatoCreateDTO): Promise<OutroContato> => {
  const response = await api.post<ResponseDto<OutroContato>>(BASE_URL, data);
  return extractDado(response);
};

export const updateOutroContato = async (id: number, data: OutroContatoUpdateDTO): Promise<OutroContato> => {
  const response = await api.put<ResponseDto<OutroContato>>(`${BASE_URL}/${id}`, data);
  return extractDado(response);
};

export const deleteOutroContato = async (id: number): Promise<void> => {
  await api.delete(`${BASE_URL}/${id}`);
};
