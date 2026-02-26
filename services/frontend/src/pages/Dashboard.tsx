import React, { useState, useEffect } from 'react';
import Sidebar from '../components/Sidebar';
import FilterBar from '../components/dashboard/FilterBar';
import KPICard from '../components/dashboard/KPICard';
import ChartsSection from '../components/dashboard/ChartsSection';
import DetailedList from '../components/dashboard/DetailedList';
import { Users, LayoutDashboard, AlertCircle, Loader, AlertTriangle, Settings } from 'lucide-react';
import { Link } from 'react-router-dom';
import type { DashboardDataDTO, DashboardFilters } from '../types/dashboard';
import { getDashboardData } from '../services/dashboardService';
import { usuarioConfigService } from '../services/usuarioConfigService';
import { useAuth } from '../contexts/AuthContext';

// Custom Hook for Dashboard Data Logic
const useDashboardData = () => {
  const [data, setData] = useState<DashboardDataDTO | null>(null);
  const [isConfigured, setIsConfigured] = useState<boolean>(true);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filters, setFilters] = useState<DashboardFilters>({
    day: null,
    month: null,
    year: 2026,
    ddd: null,
    status: 'Todos',
  });
  
  const { user } = useAuth();

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null); // Limpa o erro ao tentar buscar novamente
      try {
        if (user) {
          // Busca os dados do Dashboard e as Configurações em paralelo
          const [dashboardResult, configResult] = await Promise.all([
            getDashboardData(filters, user.id),
            usuarioConfigService.buscar(user.id).catch(() => null) // Não quebra se falhar
          ]);

          setData(dashboardResult);

          // Validação: Se não tem atributos ou mensagem, consideramos "Não Configurado"
          if (configResult) {
            // Adicionamos o !! por fora dos parênteses para garantir que seja sempre um Boolean (true/false)
            const temAtributos = !!(configResult.atributos_qualificacao && Object.keys(configResult.atributos_qualificacao).length > 0);
            
            const temMensagem = !!configResult.mensagem_direcionamento_vendedor;
            
            setIsConfigured(temAtributos && temMensagem);
          }
        }
      } catch (err: any) {
        // Captura a mensagem do backend interceptada pelo Axios, sem console.error!
        setError(err.message || 'Falha ao carregar os dados do dashboard.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [filters, user]);

  return { data, loading, error, filters, setFilters, isConfigured };
};

// Loading and Error States
const LoadingSpinner: React.FC = () => (
  <div className="flex items-center justify-center p-8 text-blue-600">
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
  const { data, loading, error, filters, setFilters, isConfigured } = useDashboardData();

  return (
    <div className="flex flex-col lg:flex-row min-h-screen bg-slate-50 font-sans text-slate-900">
      <Sidebar activeItem="Menu Principal" />

      <main className="flex-1 lg:ml-64 transition-all duration-300">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">

          <div className="flex items-center gap-3 mb-8">
            <div className="bg-blue-100 p-2 rounded-xl text-blue-600">
              <LayoutDashboard size={24} />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-slate-900 leading-tight">Menu Principal</h1>
              <p className="text-slate-500 text-sm mt-0.5 font-medium">Analytics de contatos e performance.</p>
            </div>
          </div>

          {/* BANNER DE AVISO DE CONFIGURAÇÃO INCOMPLETA */}
          {!loading && !isConfigured && (
            <div className="bg-amber-50 border border-amber-200 rounded-xl shadow-sm p-6 mb-8 flex flex-col md:flex-row items-start md:items-center justify-between gap-5 animate-in fade-in slide-in-from-top-4 duration-500">
              <div className="flex items-start gap-4">
                <div className="bg-amber-100 p-3 rounded-full text-amber-600 shrink-0 mt-1 md:mt-0">
                  <AlertTriangle size={24} />
                </div>
                <div>
                  <h2 className="text-lg font-bold text-amber-800">Finalize sua Configuração!</h2>
                  <p className="text-sm text-amber-700 mt-1 max-w-3xl">
                    Para que o Leadflow comece a conversar com seus leads e distribuir para os vendedores, é obrigatório cadastrar a <strong>Mensagem de Direcionamento</strong> e seus <strong>Atributos de Qualificação</strong>.
                  </p>
                </div>
              </div>
              <Link 
                to="/usuarios/configuracoes" 
                className="shrink-0 flex items-center justify-center gap-2 bg-amber-600 hover:bg-amber-700 text-white px-5 py-2.5 rounded-lg font-medium transition-colors shadow-sm w-full md:w-auto"
              >
                <Settings size={20} />
                Configurar Agora
              </Link>
            </div>
          )}

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
