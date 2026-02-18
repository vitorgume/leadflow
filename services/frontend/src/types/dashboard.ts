export interface DashboardFilters {
  day: number | null;
  month: number | null;
  year: number | null;
  ddd: string | null;
  status: 'Todos' | 'Ativo' | 'Finalizado';
}

export interface Contact {
  id: string;
  nome: string;
  telefone: string;
  horario: string;
  status: 'Ativo' | 'Finalizado' | 'Pendente';
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
