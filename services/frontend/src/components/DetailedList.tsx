import React from 'react';
import { MoreVertical } from 'lucide-react';
import { Contact } from '../types/dashboard';

interface DetailedListProps {
  contacts: Contact[];
}

const getStatusBadge = (status: Contact['status']) => {
  switch(status) {
    case 'Ativo': 
      return 'bg-emerald-100 text-emerald-700 border-emerald-200';
    case 'Finalizado': 
      return 'bg-slate-100 text-slate-700 border-slate-200';
    case 'Pendente': 
      return 'bg-amber-100 text-amber-700 border-amber-200';
    default: 
      return 'bg-slate-100 text-slate-700 border-slate-200';
  }
};

const DetailedList: React.FC<DetailedListProps> = ({ contacts }) => {
  if (contacts.length === 0) {
    return (
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-12 text-center">
        <h4 className="text-lg font-medium text-slate-500">Nenhum contato encontrado.</h4>
        <p className="text-sm text-slate-400 mt-2">Tente ajustar os filtros para encontrar mais resultados.</p>
      </div>
    )
  }

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
      <div className="p-6 border-b border-slate-100">
        <h4 className="text-lg font-bold text-slate-900">Listagem de Contatos</h4>
      </div>
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-slate-200">
          <thead className="bg-slate-50">
            <tr>
              <th scope="col" className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Nome do Cliente</th>
              <th scope="col" className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Telefone</th>
              <th scope="col" className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Horário</th>
              <th scope="col" className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Status</th>
              <th scope="col" className="relative px-6 py-3"><span className="sr-only">Ações</span></th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-slate-200">
            {contacts.map((contact) => (
              <tr key={contact.id} className="hover:bg-slate-50 transition-colors group">
                <td className="px-6 py-4 whitespace-nowrap">
                  <div className="flex items-center gap-3">
                    <div className="h-9 w-9 rounded-full bg-indigo-50 flex items-center justify-center text-indigo-700 font-bold text-sm border border-indigo-100">
                      {contact.nome.charAt(0)}
                    </div>
                    <span className="text-sm font-medium text-slate-900">{contact.nome}</span>
                  </div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-600">{contact.telefone}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-500">{contact.horario}</td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2.5 py-0.5 inline-flex text-xs leading-5 font-semibold rounded-full border ${getStatusBadge(contact.status)}`}>
                    {contact.status}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <button className="text-slate-400 hover:text-slate-900 transition-colors"><MoreVertical size={20} /></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <div className="bg-white px-6 py-4 border-t border-slate-200 flex items-center justify-between">
        <div className="text-xs text-slate-500">
          Mostrando <span className="font-semibold text-slate-900">1</span> a <span className="font-semibold text-slate-900">{contacts.length}</span> de <span className="font-semibold text-slate-900">{contacts.length}</span> contatos
        </div>
      </div>
    </div>
  );
};

export default DetailedList;
