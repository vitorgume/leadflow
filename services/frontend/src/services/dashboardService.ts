import api from './api';
import type { DashboardFilters, DashboardDataDTO } from '../types/dashboard';

/**
 * Fetches the dashboard data from the API based on the provided filters.
 * @param filters - The filter object to be sent as query parameters.
 * @returns A promise that resolves to the dashboard data.
 */
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
    // 2. Dispara TODAS as requisições simultaneamente (Alta performance)
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
      api.get(`/dashboard/contatos-hoje/${idUsuario}`), // Este usa PathVariable
      api.get('/dashboard/taxa-resposta', { params }),
      api.get('/dashboard/media-por-vendedor', { params }),
      api.get('/dashboard/contatos-por-dia', { params }),
      api.get('/dashboard/contatos-por-hora', { params }),
      api.get('/dashboard/contatos-paginado', {
        params: { ...params, page: 0, size: 10 }
      })
    ]);

    // Função ninja para traduzir o DTO do Java para a Interface do React
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

    // NOVA: Função ninja para traduzir a lista de contatos
    const traduzirContatos = (responseData: any) => {
      // 1. Agora procuramos no atributo 'contacts' (como definido no seu Java)
      if (!responseData || !responseData.contacts) return [];

      return responseData.contacts.map((item: any, index: number) => {
        return {
          id: item.id || `temp-id-${index}`,
          nome: item.nome || 'Não informado',
          telefone: item.telefone || 'Sem telefone',
          horario: item.horario || '--:--',
          // O Java já manda o enum exato, basta repassar!
          status: item.status || 'ANDAMENTO'
        };
      });
    };

    // 3. Desempacota os DTOs do Java e monta o objeto
    return {
      summary: {
        totalContacts: totalRes.data.value || 0,
        contactsToday: hojeRes.data.value || 0,
        responseRate: taxaRes.data.value || 0,
        avgPerVendor: mediaRes.data.value || 0,
      },
      contactsByDay: traduzirGrafico(porDiaRes.data),
      contactsByHour: traduzirGrafico(porHoraRes.data),

      // Aplicamos o tradutor na lista paginada! 👇
      detailedContacts: traduzirContatos(listaRes.data)
    };
  } catch (error) {
    console.error("Erro ao buscar dados integrados do dashboard:", error);
    throw error;
  }
};

// As a placeholder, we'll create a mock function that simulates a network request
// so the UI can be built without a real backend.
export const getMockDashboardData = (filters: DashboardFilters): Promise<DashboardDataDTO> => {
  console.log("Fetching MOCK data with filters:", filters);
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({
        summary: {
          totalContacts: 1345,
          contactsToday: 62,
          responseRate: 78,
          avgPerVendor: 121,
        },
        contactsByDay: [
          { name: 'Seg', value: 45 },
          { name: 'Ter', value: 52 },
          { name: 'Qua', value: 38 },
          { name: 'Qui', value: 65 },
          { name: 'Sex', value: 48 },
          { name: 'Sáb', value: 24 },
          { name: 'Dom', value: 15 },
        ],
        contactsByHour: [
          { name: '08h', value: 18 },
          { name: '10h', value: 35 },
          { name: '12h', value: 28 },
          { name: '14h', value: 42 },
          { name: '16h', value: 50 },
          { name: '18h', value: 32 },
        ],
        detailedContacts: [
          { id: '1', nome: "João Victor (API)", telefone: "(11) 98765-4321", horario: "10:45", status: 'ATIVO' },
          { id: '2', nome: "Maria Eduarda (API)", telefone: "(21) 91234-5678", horario: "11:20", status: 'ATIVO' },
          { id: '3', nome: "Ricardo Almeida (API)", telefone: "(31) 97766-5544", horario: "13:05", status: 'INATIVO_G1' },
        ]
      });
    }, 800); // Simulate network delay
  });
};
