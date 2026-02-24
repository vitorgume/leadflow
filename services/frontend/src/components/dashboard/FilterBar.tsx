import React from 'react';
import { Filter, Search } from 'lucide-react';
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

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6 mb-8 flex flex-wrap items-end gap-4">
      {/* Mês */}
      <div className="w-32">
        <label className="block text-xs font-semibold text-slate-500 uppercase mb-2">Mês</label>
        <select
          name="month"
          value={filters.month || ''}
          onChange={handleInputChange}
          className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-all text-sm bg-white cursor-pointer"
        >
          <option value="">Todos</option>
          <option value="1">Janeiro</option>
          <option value="2">Fevereiro</option>
          <option value="3">Março</option>
          <option value="4">Abril</option>
        </select>
      </div>

      {/* Ano */}
      <div className="w-28">
        <label className="block text-xs font-semibold text-slate-500 uppercase mb-2">Ano</label>
        <select
          name="year"
          value={filters.year || ''}
          onChange={handleInputChange}
          className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-all text-sm bg-white cursor-pointer"
        >
          <option value="2026">2026</option>
          <option value="2025">2025</option>
          <option value="2024">2024</option>
        </select>
      </div>

      {/* Status */}
      <div className="flex flex-col">
        <label className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">Status</label>
        <select
          value={filters.status}
          onChange={(e) => onFilterChange({ ...filters, status: e.target.value as any })}
          className="bg-slate-50 border-0 text-slate-700 text-sm rounded-lg focus:ring-2 focus:ring-indigo-500 block w-full p-2.5 font-medium transition-colors cursor-pointer"
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
