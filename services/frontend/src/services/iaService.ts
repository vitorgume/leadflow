import api from './api'; // Importando a nossa API configurada com os cookies!
import type { PromptDto, BaseConhecimentoDto } from '../types/ia';

export const promptService = {
  buscar: async (idUsuario: string): Promise<PromptDto | null> => {
    // Agora usamos api.get em vez de axios.get
    const response = await api.get(`/prompts/${idUsuario}`);
    // O Spring envia ResponseDto<List<PromptDto>>
    const prompts = response.data.dado || response.data; 
    return prompts && prompts.length > 0 ? prompts[0] : null;
  },
  salvar: async (payload: PromptDto): Promise<PromptDto> => {
    if (payload.id) {
      const response = await api.put(`/prompts/${payload.id}`, payload);
      return response.data.dado || response.data;
    } else {
      const response = await api.post(`/prompts`, payload);
      return response.data.dado || response.data;
    }
  },
  deletar: async (idPrompt: string): Promise<void> => {
    await api.delete(`/prompts/${idPrompt}`);
  }
};

export const baseConhecimentoService = {
  buscar: async (idUsuario: string): Promise<BaseConhecimentoDto | null> => {
    const response = await api.get(`/base-conhecimento/${idUsuario}`);
    // O Spring envia ResponseDto<List<BaseConhecimentoDto>>
    const bases = response.data.dado || response.data;
    return bases && bases.length > 0 ? bases[0] : null;
  },
  salvar: async (payload: BaseConhecimentoDto): Promise<BaseConhecimentoDto> => {
    if (payload.id) {
      const response = await api.put(`/base-conhecimento/${payload.id}`, payload);
      return response.data.dado || response.data;
    } else {
      const response = await api.post(`/base-conhecimento`, payload);
      return response.data.dado || response.data;
    }
  },
  deletar: async (idBase: string): Promise<void> => {
    await api.delete(`/base-conhecimento/${idBase}`);
  }
};
