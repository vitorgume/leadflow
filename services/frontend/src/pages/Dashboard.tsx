import React, { useState, useEffect } from 'react';
import Sidebar from '../components/Sidebar';
import FilterBar from '../components/dashboard/FilterBar';
import KPICard from '../components/dashboard/KPICard';
import ChartsSection from '../components/dashboard/ChartsSection';
import DetailedList from '../components/dashboard/DetailedList';
import { Users, LayoutDashboard, AlertCircle, Loader } from 'lucide-react';
import type { DashboardDataDTO, DashboardFilters } from '../types/dashboard';
import { getMockDashboardData, getDashboardData } from '../services/dashboardService'; // Using mock for now

// Custom Hook for Dashboard Data Logic
const useDashboardData = () => {
  const [data, setData] = useState<DashboardDataDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filters, setFilters] = useState<DashboardFilters>({
    day: null,
    month: null,
    year: 2026,
    ddd: null,
    status: 'Todos',
  });

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      try {
        // Replace with getDashboardData(filters) for real API calls
        const result = await getDashboardData(filters, '4736d2bb-ec07-4222-903c-78684b3f6872');
        setData(result);
      } catch (err) {
        setError('Falha ao carregar os dados do dashboard.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [filters]);

  return { data, loading, error, filters, setFilters };
};

// Loading and Error States
const LoadingSpinner: React.FC = () => (
  <div className="flex items-center justify-center p-8 text-indigo-600">
    <Loader className="animate-spin" size={32} />
    <span className="ml-4 text-lg font-medium text-slate-600">Carregando...</span>
  </div>
);

const ErrorMessage: React.FC<{ message: string }> = ({ message }) => (
  <div className="flex items-center justify-center p-8 bg-rose-50 border border-rose-200 rounded-xl">
    <AlertCircle className="text-rose-600" size={24} />
    <span className="ml-4 text-lg font-medium text-rose-700">{message}</span>
  </div>
);


const Dashboard: React.FC = () => {
  const { data, loading, error, filters, setFilters } = useDashboardData();

  return (
    <div className="flex flex-col lg:flex-row min-h-screen bg-slate-50 font-sans text-slate-900">
      <Sidebar activeItem="Menu Principal" />

      <main className="flex-1 lg:ml-64 transition-all duration-300">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          
          <div className="flex items-center gap-3 mb-8">
            <div className="bg-indigo-100 p-2 rounded-xl text-indigo-600">
              <LayoutDashboard size={24} />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-slate-900 leading-tight">Menu Principal</h1>
              <p className="text-slate-500 text-sm mt-0.5 font-medium">Analytics de contatos e performance.</p>
            </div>
          </div>

          <FilterBar filters={filters} onFilterChange={setFilters} />

          {loading && <LoadingSpinner />}
          {error && <ErrorMessage message={error} />}
          
          {data && !loading && !error && (
            <>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                <KPICard label="Total de Contatos" value={data.summary.totalContacts} icon={Users} />
                <KPICard label="Contatos Hoje" value={data.summary.contactsToday} icon={Users} />
                <KPICard label="Taxa de Resposta" value={`${data.summary.responseRate}%`} icon={Users} />
                <KPICard label="Média por Vendedor" value={data.summary.avgPerVendor} icon={Users} />
              </div>

              <ChartsSection 
                dailyData={data.contactsByDay} 
                hourlyData={data.contactsByHour} 
              />

              <DetailedList contacts={data.detailedContacts} />
            </>
          )}

          <footer className="mt-12 text-center text-slate-400 text-xs py-6 border-t border-slate-200">
            © 2026 Leadflow System • Todos os direitos reservados.
          </footer>
        </div>
      </main>
    </div>
  );
};

export default Dashboard;
