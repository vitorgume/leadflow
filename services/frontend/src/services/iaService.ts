import axios from 'axios';
import type { PromptDto, BaseConhecimentoDto } from '../types/ia';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export const promptService = {
  buscar: async (idUsuario: string): Promise<PromptDto | null> => {
    const response = await axios.get(`${API_URL}/prompts/${idUsuario}`);
    // O Spring envia ResponseDto<List<PromptDto>>
    const prompts = response.data.dado || response.data; 
    return prompts && prompts.length > 0 ? prompts[0] : null;
  },
  salvar: async (payload: PromptDto): Promise<PromptDto> => {
    if (payload.id) {
      const response = await axios.put(`${API_URL}/prompts/${payload.id}`, payload);
      return response.data.dado || response.data;
    } else {
      const response = await axios.post(`${API_URL}/prompts`, payload);
      return response.data.dado || response.data;
    }
  },
  deletar: async (idPrompt: string): Promise<void> => {
    await axios.delete(`${API_URL}/prompts/${idPrompt}`);
  }
};

export const baseConhecimentoService = {
  buscar: async (idUsuario: string): Promise<BaseConhecimentoDto | null> => {
    const response = await axios.get(`${API_URL}/base-conhecimento/${idUsuario}`);
    // O Spring envia ResponseDto<List<BaseConhecimentoDto>>
    const bases = response.data.dado || response.data;
    return bases && bases.length > 0 ? bases[0] : null;
  },
  salvar: async (payload: BaseConhecimentoDto): Promise<BaseConhecimentoDto> => {
    if (payload.id) {
      const response = await axios.put(`${API_URL}/base-conhecimento/${payload.id}`, payload);
      return response.data.dado || response.data;
    } else {
      const response = await axios.post(`${API_URL}/base-conhecimento`, payload);
      return response.data.dado || response.data;
    }
  },
  deletar: async (idBase: string): Promise<void> => {
    await axios.delete(`${API_URL}/base-conhecimento/${idBase}`);
  }
};