import api from './api';
import type { DashboardFilters, DashboardDataDTO } from '../types/dashboard';

/**
 * Fetches the dashboard data from the API based on the provided filters.
 * @param filters - The filter object to be sent as query parameters.
 * @returns A promise that resolves to the dashboard data.
 */
export const getDashboardData = async (filters: DashboardFilters): Promise<DashboardDataDTO> => {
  // Clean up the filters object, removing null or "Todos" values before sending
  const params = new URLSearchParams();
  
  if (filters.year) params.append('year', String(filters.year));
  if (filters.month) params.append('month', String(filters.month));
  if (filters.day) params.append('day', String(filters.day));
  if (filters.ddd) params.append('ddd', filters.ddd);
  if (filters.status && filters.status !== 'Todos') {
    params.append('status', filters.status);
  }

  try {
    const response = await api.get('/dashboard', { params });
    
    // You might need to map the response from your Java backend to the DTO
    // For now, we assume the backend returns data in the shape of DashboardDataDTO
    return response.data;
  } catch (error) {
    console.error("Error fetching dashboard data:", error);
    // You could re-throw the error or return a default structure
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
          { id: '1', nome: "João Victor (API)", telefone: "(11) 98765-4321", horario: "10:45", status: "Ativo" },
          { id: '2', nome: "Maria Eduarda (API)", telefone: "(21) 91234-5678", horario: "11:20", status: "Finalizado" },
          { id: '3', nome: "Ricardo Almeida (API)", telefone: "(31) 97766-5544", horario: "13:05", status: "Pendente" },
        ]
      });
    }, 800); // Simulate network delay
  });
};
