import React, { useState, useEffect, useCallback, useRef } from 'react';
import Sidebar from '../components/Sidebar';
import { Contact, Plus, Search, Edit, Trash2, Upload, Loader2 } from 'lucide-react';
import type { OutroContato, OutroContatoCreateDTO, OutroContatoUpdateDTO } from '../types/outroContato';
import { getOutrosContatos, createOutroContato, updateOutroContato, deleteOutroContato } from '../services/outroContatoService';

import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';
import DeleteConfirmationModal from '../components/DeleteConfirmationModal';
import OutroContatoForm from '../components/outroContato/OutroContatoForm';
import { useAuth } from '../contexts/AuthContext';

// Limite seguro de contatos para importação via Frontend (requisições sequenciais)
const MAX_CSV_ROWS = 100;

export const OutrosContatos: React.FC = () => {
  const [contatos, setContatos] = useState<OutroContato[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');

  // Modais e Upload
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingContato, setEditingContato] = useState<OutroContato | null>(null);
  const [isSaving, setIsSaving] = useState(false);
  
  const [contatoToDelete, setContatoToDelete] = useState<number | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  // Estados e Referências para o CSV
  const [isImporting, setIsImporting] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const { user } = useAuth();

  const fetchContatos = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      if (user) {
        const data = await getOutrosContatos(user.id);
        setContatos(data);
      }
    } catch (err: any) {
      setError(err.message || 'Falha ao carregar a lista de contatos.');
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
    setError(null); 
    try {
      if (editingContato) {
        await updateOutroContato(editingContato.id, data as OutroContatoUpdateDTO);
      } else {
        await createOutroContato(data as OutroContatoCreateDTO);
      }
      setIsFormOpen(false);
      fetchContatos();
    } catch (err: any) {
      setError(err.message || 'Falha ao salvar o contato.');
    } finally {
      setIsSaving(false);
    }
  };

  const executeDelete = async () => {
    if (contatoToDelete === null) return;
    setIsDeleting(true);
    setError(null);
    try {
      await deleteOutroContato(contatoToDelete);
      setContatoToDelete(null);
      fetchContatos();
    } catch (err: any) {
      setError(err.message || 'Falha ao deletar o contato.');
      setContatoToDelete(null);
    } finally {
      setIsDeleting(false);
    }
  };

  // --- LÓGICA DE IMPORTAÇÃO CSV COM TRAVA DE SEGURANÇA ---
  const handleImportCSV = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file || !user) return;

    setIsImporting(true);
    setError(null);

    try {
      const text = await file.text();
      // Divide por quebras de linha e limpa linhas vazias
      const rows = text.split('\n').filter(row => row.trim() !== '');
      
      if (rows.length <= 1) {
        throw new Error("O arquivo CSV está vazio ou não possui dados válidos na segunda linha.");
      }

      // 🛑 TRAVA DE SEGURANÇA AQUI 🛑
      // Subtraímos 1 para não contar o cabeçalho
      const totalContatos = rows.length - 1;
      if (totalContatos > MAX_CSV_ROWS) {
        throw new Error(`O arquivo é muito grande (${totalContatos} contatos). O limite máximo é de ${MAX_CSV_ROWS} contatos por vez.`);
      }

      // Mapeia do CSV para a DTO (Ignorando a linha 0 que é o cabeçalho)
      const contatosParaCriar: OutroContatoCreateDTO[] = rows.slice(1).map(row => {
        // Assume o padrão: nome,telefone,descricao,tipo_contato
        const [nome, telefone, descricao, tipo_contato] = row.split(',');
        
        return {
          nome: nome?.trim() || '',
          telefone: telefone?.trim() || '',
          descricao: descricao?.trim() || '',
          tipo_contato: (tipo_contato?.trim().toUpperCase() as 'PADRAO' | 'GERENTE' | 'CONSULTOR') || 'PADRAO',
          usuario: { id: user.id }
        };
      }).filter(c => c.nome && c.telefone); // Garante que tem os dados obrigatórios

      // Dispara a criação em sequência
      for (const contato of contatosParaCriar) {
        await createOutroContato(contato);
      }

      // Recarrega a tela com os dados novos
      fetchContatos();
    } catch (err: any) {
      setError(err.message || 'Falha ao processar o arquivo CSV. Verifique a formatação.');
    } finally {
      setIsImporting(false);
      if (fileInputRef.current) {
        fileInputRef.current.value = ''; // Reseta o input para permitir upload do mesmo arquivo novamente
      }
    }
  };

  const getTipoBadge = (tipo: string) => {
    switch (tipo) {
      case 'GERENTE': return 'bg-blue-100 text-blue-800';
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
            <div className="bg-blue-100 p-2 rounded-xl text-blue-600">
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
                  className="w-full pl-10 pr-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
                <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
              </div>
              
              <div className="flex flex-col sm:flex-row gap-3 w-full md:w-auto">
                <div className="flex flex-col sm:flex-row gap-3 w-full justify-end">
                  {/* Input escondido para capturar o arquivo */}
                  <input 
                    type="file" 
                    accept=".csv" 
                    ref={fileInputRef} 
                    onChange={handleImportCSV} 
                    className="hidden" 
                  />
                  
                  <button
                    onClick={() => fileInputRef.current?.click()}
                    disabled={isImporting}
                    className="flex items-center justify-center gap-2 bg-slate-100 hover:bg-slate-200 text-slate-700 px-4 py-2 rounded-lg font-medium transition-colors shadow-sm w-full sm:w-auto disabled:opacity-70"
                  >
                    {isImporting ? <Loader2 size={20} className="animate-spin" /> : <Upload size={20} />}
                    Importar CSV
                  </button>

                  <button
                    onClick={() => { setEditingContato(null); setIsFormOpen(true); setError(null); }}
                    className="flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors shadow-sm w-full sm:w-auto"
                  >
                    <Plus size={20} />
                    Adicionar Contato
                  </button>
                </div>
              </div>

            </div>

            {loading ? (
              <LoadingSpinner />
            ) : (
              <>
                {error && (
                  <div className="mb-6">
                    <ErrorMessage message={error} />
                  </div>
                )}

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
                            <button onClick={() => { setEditingContato(contato); setIsFormOpen(true); setError(null); }} className="text-blue-600 hover:text-blue-900 mr-3 transition-colors" title="Editar">
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
                  
                  {filteredContatos.length === 0 && (
                    <p className="text-center text-slate-500 mt-4 py-4">Nenhum contato encontrado.</p>
                  )}
                </div>
              </>
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