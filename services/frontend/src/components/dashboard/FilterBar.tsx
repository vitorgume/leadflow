import React from 'react';
import type { DashboardFilters } from '../../types/dashboard';

interface FilterBarProps {
  filters: DashboardFilters;
  onFilterChange: (newFilters: DashboardFilters) => void;
}

const FilterBar: React.FC<FilterBarProps> = ({ filters, onFilterChange }) => {

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    onFilterChange({
      ...filters,
      [name]: value
    });
  };

  // Padrão Ouro: Gera os anos dinamicamente baseados no relógio do sistema (do ano atual até 2024)
  const currentYear = new Date().getFullYear();
  const years = Array.from({ length: currentYear - 2024 + 1 }, (_, i) => currentYear - i);

  // Lista completa de meses para o cliente não ficar travado
  const months = [
    { value: "1", label: "Janeiro" }, { value: "2", label: "Fevereiro" },
    { value: "3", label: "Março" }, { value: "4", label: "Abril" },
    { value: "5", label: "Maio" }, { value: "6", label: "Junho" },
    { value: "7", label: "Julho" }, { value: "8", label: "Agosto" },
    { value: "9", label: "Setembro" }, { value: "10", label: "Outubro" },
    { value: "11", label: "Novembro" }, { value: "12", label: "Dezembro" },
  ];

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6 mb-8 flex flex-wrap items-end gap-4">
      {/* Mês (Agora com os 12 meses disponíveis) */}
      <div className="w-32">
        <label className="block text-xs font-semibold text-slate-500 uppercase mb-2">Mês</label>
        <select
          name="month"
          value={filters.month || ''}
          onChange={handleInputChange}
          className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all text-sm bg-white cursor-pointer"
        >
          <option value="">Todos</option>
          {months.map(m => (
            <option key={m.value} value={m.value}>{m.label}</option>
          ))}
        </select>
      </div>

      {/* Ano (Gerado Dinamicamente) */}
      <div className="w-28">
        <label className="block text-xs font-semibold text-slate-500 uppercase mb-2">Ano</label>
        <select
          name="year"
          value={filters.year || ''}
          onChange={handleInputChange}
          className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all text-sm bg-white cursor-pointer"
        >
          {years.map(y => (
            <option key={y} value={y}>{y}</option>
          ))}
        </select>
      </div>

      {/* Status (Note o name="status" que adicionei para o handleInputChange funcionar automaticamente) */}
      <div className="flex flex-col">
        <label className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">Status</label>
        <select
          name="status"
          value={filters.status}
          onChange={handleInputChange}
          className="bg-slate-50 border-0 text-slate-700 text-sm rounded-lg focus:ring-2 focus:ring-blue-500 block w-full p-2.5 font-medium transition-colors cursor-pointer"
        >
          <option value="Todos">Todos Status</option>
          <option value="ATIVO">Conversa Ativa</option>
          <option value="ANDAMENTO">Em Andamento</option>
          <option value="INATIVO_G1">Inativo Grau 1</option>
          <option value="INATIVO_G2">Inativo Grau 2</option>
        </select>
      </div>
    </div>
  );
};

export default FilterBar;