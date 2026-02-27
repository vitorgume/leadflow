import React, { useState } from 'react';
import { Search } from 'lucide-react'; // Importamos o ícone de busca e removemos o MoreVertical
import type { Contact } from '../../types/dashboard';

interface DetailedListProps {
  contacts: Contact[];
}

// 1. Função Padrão Ouro para formatar o LocalDateTime que vem do Java
const formatarDataHorario = (dataString?: string) => {
  if (!dataString) return '-';
  try {
    const data = new Date(dataString);
    return new Intl.DateTimeFormat('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    }).format(data);
  } catch (e) {
    return dataString;
  }
};

const getStatusStyles = (status: string) => {
  switch (status) {
    case 'ATIVO':
      return 'bg-emerald-100 text-emerald-700 border-emerald-200';
    case 'ANDAMENTO':
      return 'bg-blue-100 text-blue-700 border-blue-200';
    case 'INATIVO_G1':
    case 'INATIVO_G2':
      return 'bg-slate-100 text-slate-600 border-slate-200';
    default:
      return 'bg-gray-100 text-gray-700 border-gray-200';
  }
};

const formatStatusName = (status: string) => {
  switch (status) {
    case 'ATIVO': return 'Ativo';
    case 'ANDAMENTO': return 'Em Andamento';
    case 'INATIVO_G1': return 'Inativo G1';
    case 'INATIVO_G2': return 'Inativo G2';
    default: return status;
  }
};

const DetailedList: React.FC<DetailedListProps> = ({ contacts }) => {
  // Estado para armazenar o termo de busca
  const [searchTerm, setSearchTerm] = useState('');

  if (contacts.length === 0) {
    return (
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-12 text-center">
        <h4 className="text-lg font-medium text-slate-500">Nenhum contato encontrado.</h4>
        <p className="text-sm text-slate-400 mt-2">Tente ajustar os filtros para encontrar mais resultados.</p>
      </div>
    );
  }

  // Filtramos os contatos com base no termo digitado
  const filteredContacts = contacts.filter((contact) => {
    const term = searchTerm.toLowerCase();
    const nomeMatch = contact.nome ? contact.nome.toLowerCase().includes(term) : false;
    const phoneMatch = contact.telefone ? contact.telefone.includes(term) : false;
    return nomeMatch || phoneMatch;
  });

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
      
      {/* Cabeçalho com Título e Barra de Busca */}
      <div className="p-6 border-b border-slate-100 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <h4 className="text-lg font-bold text-slate-900">Listagem de Contatos</h4>
        
        <div className="relative w-full sm:w-72">
          <input
            type="text"
            placeholder="Buscar por nome ou telefone..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-10 pr-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all text-sm"
          />
          <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
        </div>
      </div>

      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-slate-200">
          <thead className="bg-slate-50">
            <tr>
              <th scope="col" className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Nome do Cliente</th>
              <th scope="col" className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Telefone</th>
              <th scope="col" className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Data e Hora</th>
              <th scope="col" className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Status</th>
              {/* Removemos a coluna vazia de "Ações" */}
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-slate-200">
            {filteredContacts.length > 0 ? (
              filteredContacts.map((contact, index) => (
                <tr key={contact.id || index} className="hover:bg-slate-50 transition-colors group">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center gap-3">
                      <div className="h-9 w-9 rounded-full bg-blue-50 flex items-center justify-center text-blue-700 font-bold text-sm border border-blue-100">
                        {contact.nome ? contact.nome.charAt(0).toUpperCase() : '?'}
                      </div>
                      <span className="text-sm font-medium text-slate-900">{contact.nome}</span>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-600">{contact.telefone}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-500 font-medium">
                    {formatarDataHorario((contact as any).data_horario || (contact as any).dataHorario)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2.5 py-1 inline-flex text-xs leading-5 font-semibold rounded-full border ${getStatusStyles(contact.status)}`}>
                      {formatStatusName(contact.status)}
                    </span>
                  </td>
                  {/* Removemos a coluna do botão MoreVertical */}
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={4} className="px-6 py-8 text-center text-sm text-slate-500">
                  Nenhum contato encontrado para "<span className="font-semibold">{searchTerm}</span>".
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
      <div className="bg-white px-6 py-4 border-t border-slate-200 flex items-center justify-between">
        <div className="text-xs text-slate-500">
          Mostrando <span className="font-semibold text-slate-900">{filteredContacts.length > 0 ? 1 : 0}</span> a <span className="font-semibold text-slate-900">{filteredContacts.length}</span> de <span className="font-semibold text-slate-900">{contacts.length}</span> contatos
        </div>
      </div>
    </div>
  );
};

export default DetailedList;