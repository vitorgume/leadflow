export interface DashboardFilters {
  day: number | null;
  month: number | null;
  year: number | null;
  ddd: string | null;
  // Atualizado para bater com o Java
  status: 'Todos' | 'INATIVO_G1' | 'INATIVO_G2' | 'ATIVO' | 'ANDAMENTO'; 
}

export interface Contact {
  id: string;
  nome: string;
  telefone: string;
  horario: string;
  // Atualizado para bater com o Java
  status: 'INATIVO_G1' | 'INATIVO_G2' | 'ATIVO' | 'ANDAMENTO'; 
}

export interface ChartPoint {
  name: string;
  value: number;
}

export interface DashboardSummary {
  totalContacts: number;
  contactsToday: number;
  responseRate: number;
  avgPerVendor: number;
}

// Main DTO for the dashboard data
export interface DashboardDataDTO {
  summary: DashboardSummary;
  contactsByDay: ChartPoint[];
  contactsByHour: ChartPoint[];
  detailedContacts: Contact[];
}
