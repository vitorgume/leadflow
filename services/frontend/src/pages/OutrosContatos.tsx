import React, { useState, useEffect, useCallback } from 'react';
import Sidebar from '../components/Sidebar';
import { Contact, Plus, Search, Edit, Trash2 } from 'lucide-react';
import type { OutroContato, OutroContatoCreateDTO, OutroContatoUpdateDTO } from '../types/outroContato';
import { getOutrosContatos, createOutroContato, updateOutroContato, deleteOutroContato } from '../services/outroContatoService';

import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';
import DeleteConfirmationModal from '../components/DeleteConfirmationModal';
import OutroContatoForm from '../components/outroContato/OutroContatoForm';
import { useAuth } from '../contexts/AuthContext';

export const OutrosContatos: React.FC = () => {
  const [contatos, setContatos] = useState<OutroContato[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');

  // Modais
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingContato, setEditingContato] = useState<OutroContato | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  const [contatoToDelete, setContatoToDelete] = useState<number | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const { user } = useAuth();

  const fetchContatos = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      if (user) {
        const data = await getOutrosContatos(user.id);
        setContatos(data);
      }
    } catch (err) {
      setError('Falha ao carregar a lista de contatos.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [user]);

  useEffect(() => {
    fetchContatos();
  }, [fetchContatos]);

  const filteredContatos = contatos.filter(
    (c) => c.nome.toLowerCase().includes(searchTerm.toLowerCase()) || c.telefone.includes(searchTerm)
  );

  const handleSaveContato = async (data: OutroContatoCreateDTO | OutroContatoUpdateDTO) => {
    setIsSaving(true);
    try {
      if (editingContato) {
        await updateOutroContato(editingContato.id, data as OutroContatoUpdateDTO);
      } else {
        await createOutroContato(data as OutroContatoCreateDTO);
      }
      setIsFormOpen(false);
      fetchContatos();
    } catch (err) {
      setError('Falha ao salvar o contato.');
      console.error(err);
    } finally {
      setIsSaving(false);
    }
  };

  const executeDelete = async () => {
    if (contatoToDelete === null) return;
    setIsDeleting(true);
    try {
      await deleteOutroContato(contatoToDelete);
      setContatoToDelete(null);
      fetchContatos();
    } catch (err) {
      setError('Falha ao deletar o contato.');
      setContatoToDelete(null);
    } finally {
      setIsDeleting(false);
    }
  };

  // Cores dinâmicas para o Badge de Tipo de Contato
  const getTipoBadge = (tipo: string) => {
    switch (tipo) {
      case 'GERENTE': return 'bg-indigo-100 text-indigo-800';
      case 'CONSULTOR': return 'bg-emerald-100 text-emerald-800';
      default: return 'bg-slate-100 text-slate-800';
    }
  };

  return (
    <div className="flex flex-col lg:flex-row min-h-screen bg-slate-50 font-sans text-slate-900">
      <Sidebar activeItem="Outros Contatos" />

      <main className="flex-1 lg:ml-64 transition-all duration-300">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">

          <div className="flex items-center gap-3 mb-8">
            <div className="bg-indigo-100 p-2 rounded-xl text-indigo-600">
              <Contact size={24} />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-slate-900 leading-tight">Outros Contatos</h1>
              <p className="text-slate-500 text-sm mt-0.5 font-medium">Gerencie gerentes, consultores e contatos secundários.</p>
            </div>
          </div>

          <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6 mb-8">
            <div className="flex flex-col md:flex-row justify-between items-center gap-4 mb-6">
              <div className="relative w-full md:w-1/2">
                <input
                  type="text"
                  placeholder="Buscar por nome ou telefone..."
                  className="w-full pl-10 pr-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
                <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
              </div>
              <button
                onClick={() => { setEditingContato(null); setIsFormOpen(true); }}
                className="flex items-center justify-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg font-medium transition-colors shadow-sm w-full md:w-auto"
              >
                <Plus size={20} />
                Adicionar Contato
              </button>
            </div>

            {loading && <LoadingSpinner />}
            {error && <ErrorMessage message={error} />}

            {!loading && !error && (
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-slate-200">
                  <thead className="bg-slate-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Nome</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Telefone</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Tipo</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Descrição</th>
                      <th className="px-6 py-3 text-right text-xs font-medium text-slate-500 uppercase tracking-wider">Ações</th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-slate-200">
                    {filteredContatos.map((contato) => (
                      <tr key={contato.id}>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-slate-900">{contato.nome}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-500">{contato.telefone}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm">
                          <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getTipoBadge(contato.tipo_contato)}`}>
                            {contato.tipo_contato}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-500 truncate max-w-xs" title={contato.descricao}>
                          {contato.descricao || '-'}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                          <button onClick={() => { setEditingContato(contato); setIsFormOpen(true); }} className="text-indigo-600 hover:text-indigo-900 mr-3 transition-colors" title="Editar">
                            <Edit size={18} />
                          </button>
                          <button onClick={() => setContatoToDelete(contato.id)} className="text-rose-600 hover:text-rose-900 transition-colors" title="Deletar">
                            <Trash2 size={18} />
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {!loading && !error && filteredContatos.length === 0 && (
              <p className="text-center text-slate-500 mt-4 py-4">Nenhum contato encontrado.</p>
            )}
          </div>
        </div>
      </main>

      {isFormOpen && (
        <OutroContatoForm
          contato={editingContato}
          onSave={handleSaveContato}
          onClose={() => setIsFormOpen(false)}
          isSaving={isSaving}
          idUsuario={user == null ? '' : user.id}
        />
      )}

      <DeleteConfirmationModal
        isOpen={contatoToDelete !== null}
        onClose={() => setContatoToDelete(null)}
        onConfirm={executeDelete}
        isDeleting={isDeleting}
        title="Excluir Contato"
        message="Tem certeza que deseja excluir este contato? Esta ação não pode ser desfeita."
      />
    </div>
  );
};

export default OutrosContatos;