import api from './api';
import type { DashboardFilters, DashboardDataDTO } from '../types/dashboard';

/**
 * Função utilitária para desempacotar o ResponseDto padrão do backend.
 * Lança um erro se o backend retornar a propriedade 'erro' preenchida.
 */
const extractDado = (response: any) => {
  if (response.data && response.data.erro) {
    throw new Error(response.data.erro.mensagens.join(', '));
  }
  return response.data.dado;
};

export const getDashboardData = async (filters: DashboardFilters, idUsuario: string): Promise<DashboardDataDTO> => {
  // 1. Monta os parâmetros que o Java espera no DashboardRequestDto
  const params: Record<string, any> = {
    idUsuario: idUsuario
  };

  if (filters.year) params.year = filters.year;
  if (filters.month) params.month = filters.month;
  if (filters.day) params.day = filters.day;
  if (filters.ddd) params.ddd = filters.ddd;
  if (filters.status && filters.status !== 'Todos') {
    params.status = filters.status;
  }

  try {
    // 2. Dispara TODAS as requisições simultaneamente
    const [
      totalRes,
      hojeRes,
      taxaRes,
      mediaRes,
      porDiaRes,
      porHoraRes,
      listaRes
    ] = await Promise.all([
      api.get('/dashboard/total-contatos', { params }),
      api.get(`/dashboard/contatos-hoje/${idUsuario}`),
      api.get('/dashboard/taxa-resposta', { params }),
      api.get('/dashboard/media-por-vendedor', { params }),
      api.get('/dashboard/contatos-por-dia', { params }),
      api.get('/dashboard/contatos-por-hora', { params }),
      api.get('/dashboard/contatos-paginado', {
        params: { ...params, page: 0, size: 10 }
      })
    ]);

    // 3. Desempacota o 'dado' de dentro do ResponseDto de cada requisição
    const totalDado = extractDado(totalRes);
    const hojeDado = extractDado(hojeRes);
    const taxaDado = extractDado(taxaRes);
    const mediaDado = extractDado(mediaRes);
    const porDiaDado = extractDado(porDiaRes);
    const porHoraDado = extractDado(porHoraRes);
    const listaDado = extractDado(listaRes);

    // Função ninja V2: Traduz e ORDENA os dados!
    const traduzirGrafico = (responseData: any) => {
      if (!responseData || !responseData.items) return [];

      return responseData.items
        .map((item: any) => ({
          name: String(item.label),
          value: item.value || 0
        }))
        .sort((a: any, b: any) => {
          const numA = parseInt(a.name.replace(/\D/g, ''), 10);
          const numB = parseInt(b.name.replace(/\D/g, ''), 10);
          return numA - numB;
        });
    };

    // Função ninja para traduzir a lista de contatos
    const traduzirContatos = (responseData: any) => {
      if (!responseData || !responseData.contacts) return [];

      return responseData.contacts.map((item: any, index: number) => {
        return {
          id: item.id || `temp-id-${index}`,
          nome: item.nome || 'Não informado',
          telefone: item.telefone || 'Sem telefone',
          horario: item.horario || '--:--',
          status: item.status || 'ANDAMENTO'
        };
      });
    };

    // 4. Monta o objeto final mapeando os dados desempacotados
    return {
      summary: {
        totalContacts: totalDado?.value || 0,
        contactsToday: hojeDado?.value || 0,
        responseRate: taxaDado?.value || 0,
        avgPerVendor: mediaDado?.value || 0,
      },
      contactsByDay: traduzirGrafico(porDiaDado),
      contactsByHour: traduzirGrafico(porHoraDado),
      detailedContacts: traduzirContatos(listaDado)
    };
  } catch (error: any) {
    console.error("Erro ao buscar dados integrados do dashboard:", error.message || error);
    throw error;
  }
};
