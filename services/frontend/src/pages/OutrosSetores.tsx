import React, { useState, useEffect, useCallback } from 'react';
import Sidebar from '../components/Sidebar';
import { 
  Building2, Users, Plus, Pencil, Trash2, X, Loader2, Search, AlertCircle, Contact
} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { membroService} from '../services/membroService';
import { setorService } from '../services/setorService';
import DeleteConfirmationModal from '../components/DeleteConfirmationModal';
import type { MembroDTO, SetorDTO } from '../types/outrosSetores';

export default function OutrosSetores() {
  const { user } = useAuth();
  
  // --- ESTADOS DE UI ---
  const [activeTab, setActiveTab] = useState<'membros' | 'setores'>('membros');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  
  // --- ESTADOS DE DADOS ---
  const [membros, setMembros] = useState<MembroDTO[]>([]);
  const [setores, setSetores] = useState<SetorDTO[]>([]);
  
  // --- ESTADOS DO MODAL (MEMBRO) ---
  const [isMembroModalOpen, setIsMembroModalOpen] = useState(false);
  const [membroFormData, setMembroFormData] = useState({ nome: '', telefone: '' });
  const [editingMembroId, setEditingMembroId] = useState<string | null>(null);

  // --- ESTADOS DO MODAL (SETOR) ---
  const [isSetorModalOpen, setIsSetorModalOpen] = useState(false);
  const [setorFormData, setSetorFormData] = useState({ nome: '', descricao: '', membrosIds: [] as string[] });
  const [editingSetorId, setEditingSetorId] = useState<string | null>(null);

  // --- ESTADOS GERAIS (MODALS) ---
  const [saving, setSaving] = useState(false);
  const [itemToDelete, setItemToDelete] = useState<{ id: string, tipo: 'membro' | 'setor' } | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  // --- CARREGAR DADOS ---
  const carregarDados = useCallback(async () => {
    if (!user) return;
    setLoading(true);
    setError(null);
    try {
      // Carrega ambos em paralelo para ser muito mais rápido!
      const [membrosDb, setoresDb] = await Promise.all([
        membroService.listar(user.id),
        setorService.listar(user.id)
      ]);
      setMembros(membrosDb || []);
      setSetores(setoresDb || []);
    } catch (err: any) {
      setError(err.message || 'Erro ao carregar as informações do servidor.');
    } finally {
      setLoading(false);
    }
  }, [user]);

  useEffect(() => {
    carregarDados();
  }, [carregarDados]);

  // --- HANDLERS: MEMBROS ---
  const abrirModalMembro = (membro?: MembroDTO) => {
    if (membro) {
      setEditingMembroId(membro.id || null);
      setMembroFormData({ nome: membro.nome, telefone: membro.telefone });
    } else {
      setEditingMembroId(null);
      setMembroFormData({ nome: '', telefone: '' });
    }
    setIsMembroModalOpen(true);
  };

  const handleMembroSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user || !membroFormData.nome || !membroFormData.telefone) return;
    setSaving(true);
    setError(null);
    try {
      const payload: MembroDTO = { ...membroFormData, usuario: { id: user.id } };
      if (editingMembroId) {
        await membroService.alterar(editingMembroId, payload);
      } else {
        await membroService.cadastrar(payload);
      }
      await carregarDados();
      setIsMembroModalOpen(false);
    } catch (err: any) {
      setError(err.message || 'Erro ao salvar o membro.');
    } finally {
      setSaving(false);
    }
  };

  // --- HANDLERS: SETORES ---
  const abrirModalSetor = (setor?: SetorDTO) => {
    if (setor) {
      setEditingSetorId(setor.id || null);
      setSetorFormData({ 
        nome: setor.nome, 
        descricao: setor.descricao || '',
        membrosIds: setor.membros?.map(m => m.id as string) || [] 
      });
    } else {
      setEditingSetorId(null);
      setSetorFormData({ nome: '', descricao: '', membrosIds: [] });
    }
    setIsSetorModalOpen(true);
  };

  const toggleMembroSetor = (idMembro: string) => {
    setSetorFormData(prev => {
      const isSelected = prev.membrosIds.includes(idMembro);
      return {
        ...prev,
        membrosIds: isSelected 
          ? prev.membrosIds.filter(id => id !== idMembro) 
          : [...prev.membrosIds, idMembro]
      };
    });
  };

  const handleSetorSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user || !setorFormData.nome) return;
    setSaving(true);
    setError(null);
    try {
      const payload: SetorDTO = { 
        nome: setorFormData.nome, 
        descricao: setorFormData.descricao,
        membros: setorFormData.membrosIds.map(id => ({ id, nome: '', telefone: '', usuario: { id: user.id } })), // Envia só os IDs
        usuario: { id: user.id } 
      };
      
      if (editingSetorId) {
        await setorService.alterar(editingSetorId, payload);
      } else {
        await setorService.cadastrar(payload);
      }
      await carregarDados();
      setIsSetorModalOpen(false);
    } catch (err: any) {
      setError(err.message || 'Erro ao salvar o setor.');
    } finally {
      setSaving(false);
    }
  };

  // --- HANDLER: DELETAR (GENÉRICO) ---
  const executeDelete = async () => {
    if (!itemToDelete) return;
    setIsDeleting(true);
    setError(null);
    try {
      if (itemToDelete.tipo === 'membro') {
        await membroService.deletar(itemToDelete.id);
      } else {
        await setorService.deletar(itemToDelete.id);
      }
      await carregarDados();
      setItemToDelete(null);
    } catch (err: any) {
      setError(err.message || `Erro ao excluir ${itemToDelete.tipo}.`);
      setItemToDelete(null);
    } finally {
      setIsDeleting(false);
    }
  };

  // --- FILTROS ---
  const membrosFiltrados = membros.filter(m => 
    m.nome.toLowerCase().includes(searchTerm.toLowerCase()) || m.telefone.includes(searchTerm)
  );

  const setoresFiltrados = setores.filter(s => 
    s.nome.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="flex flex-col lg:flex-row min-h-screen bg-slate-50 font-sans text-slate-900">
      <Sidebar activeItem="Outros Setores" />

      <main className="flex-1 lg:ml-64 p-4 sm:p-6 lg:p-8 transition-all duration-300">
        <div className="w-full max-w-6xl mx-auto">
          
          {/* HEADER */}
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
            <div className="flex items-center gap-3">
              <div className="bg-blue-100 p-2 rounded-xl text-blue-600">
                <Contact size={24} />
              </div>
              <div>
                <h1 className="text-2xl font-bold text-slate-900 leading-tight">Outros Setores</h1>
                <p className="text-slate-500 text-sm mt-0.5 font-medium">Gerencie membros e configure as regras de outros departamentos.</p>
              </div>
            </div>
          </div>

          {error && (
            <div className="mb-6 p-4 bg-rose-50 border border-rose-200 rounded-xl flex items-start gap-3 text-rose-700 shadow-sm animate-in fade-in slide-in-from-top-2 duration-300">
              <AlertCircle className="shrink-0 mt-0.5" size={20} />
              <p className="text-sm font-medium">{error}</p>
            </div>
          )}

          {/* TAB NAVEGAÇÃO */}
          <div className="flex border-b border-slate-200 mb-6">
            <button
              onClick={() => { setActiveTab('membros'); setSearchTerm(''); }}
              className={`flex items-center gap-2 px-6 py-3 border-b-2 font-medium text-sm transition-colors ${
                activeTab === 'membros' 
                  ? 'border-blue-600 text-blue-600' 
                  : 'border-transparent text-slate-500 hover:text-slate-700 hover:border-slate-300'
              }`}
            >
              <Users size={18} /> Membros
            </button>
            <button
              onClick={() => { setActiveTab('setores'); setSearchTerm(''); }}
              className={`flex items-center gap-2 px-6 py-3 border-b-2 font-medium text-sm transition-colors ${
                activeTab === 'setores' 
                  ? 'border-blue-600 text-blue-600' 
                  : 'border-transparent text-slate-500 hover:text-slate-700 hover:border-slate-300'
              }`}
            >
              <Building2 size={18} /> Setores
            </button>
          </div>

          {/* ÁREA DE CONTEÚDO */}
          <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
            
            {/* Toolbar Comum */}
            <div className="p-4 sm:p-6 border-b border-slate-100 bg-slate-50/50 flex flex-col sm:flex-row gap-4 items-center justify-between">
              <div className="relative w-full sm:w-96">
                <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                <input 
                  type="text" 
                  placeholder={`Buscar ${activeTab}...`}
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-sm"
                />
              </div>
              <button 
                onClick={() => activeTab === 'membros' ? abrirModalMembro() : abrirModalSetor()}
                className="flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors shadow-sm w-full sm:w-auto shrink-0"
              >
                <Plus size={18} /> {activeTab === 'membros' ? 'Adicionar Membro' : 'Criar Setor'}
              </button>
            </div>

            {/* Listas */}
            <div className="p-0">
              {loading ? (
                <div className="flex flex-col items-center justify-center py-16 text-slate-500">
                  <Loader2 className="animate-spin text-blue-600 mb-4" size={32} />
                  <p>Carregando {activeTab}...</p>
                </div>
              ) : activeTab === 'membros' ? (
                // --- TABELA DE MEMBROS ---
                membrosFiltrados.length === 0 ? (
                  <div className="flex flex-col items-center justify-center py-16 text-slate-500">
                    <Users size={48} className="text-slate-300 mb-4" />
                    <p className="text-lg font-medium text-slate-900">Nenhum membro encontrado</p>
                  </div>
                ) : (
                  <div className="overflow-x-auto">
                    <table className="w-full text-left text-sm text-slate-600">
                      <thead className="bg-slate-50 text-slate-700 border-b border-slate-200 font-medium">
                        <tr>
                          <th className="px-6 py-4">Nome do Membro</th>
                          <th className="px-6 py-4">WhatsApp / Telefone</th>
                          <th className="px-6 py-4 text-right">Ações</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-slate-100">
                        {membrosFiltrados.map((membro) => (
                          <tr key={membro.id} className="hover:bg-slate-50/80 transition-colors">
                            <td className="px-6 py-4 font-medium text-slate-900">
                              <div className="flex items-center gap-3">
                                <div className="h-8 w-8 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-bold text-xs">
                                  {membro.nome.charAt(0).toUpperCase()}
                                </div>
                                {membro.nome}
                              </div>
                            </td>
                            <td className="px-6 py-4">{membro.telefone}</td>
                            <td className="px-6 py-4 text-right">
                              <div className="flex items-center justify-end gap-2">
                                <button onClick={() => abrirModalMembro(membro)} className="p-2 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg">
                                  <Pencil size={18} />
                                </button>
                                <button onClick={() => setItemToDelete({ id: membro.id!, tipo: 'membro' })} className="p-2 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg">
                                  <Trash2 size={18} />
                                </button>
                              </div>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )
              ) : (
                // --- TABELA DE SETORES ---
                setoresFiltrados.length === 0 ? (
                  <div className="flex flex-col items-center justify-center py-16 text-slate-500">
                    <Building2 size={48} className="text-slate-300 mb-4" />
                    <p className="text-lg font-medium text-slate-900">Nenhum setor encontrado</p>
                  </div>
                ) : (
                  <div className="overflow-x-auto">
                    <table className="w-full text-left text-sm text-slate-600">
                      <thead className="bg-slate-50 text-slate-700 border-b border-slate-200 font-medium">
                        <tr>
                          <th className="px-6 py-4">Nome do Setor</th>
                          <th className="px-6 py-4">Descrição</th>
                          <th className="px-6 py-4">Membros</th>
                          <th className="px-6 py-4 text-right">Ações</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-slate-100">
                        {setoresFiltrados.map((setor) => (
                          <tr key={setor.id} className="hover:bg-slate-50/80 transition-colors">
                            <td className="px-6 py-4 font-bold text-slate-900">{setor.nome}</td>
                            <td className="px-6 py-4 text-slate-500">{setor.descricao || '-'}</td>
                            <td className="px-6 py-4">
                              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                                {setor.membros?.length || 0} membro(s)
                              </span>
                            </td>
                            <td className="px-6 py-4 text-right">
                              <div className="flex items-center justify-end gap-2">
                                <button onClick={() => abrirModalSetor(setor)} className="p-2 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg">
                                  <Pencil size={18} />
                                </button>
                                <button onClick={() => setItemToDelete({ id: setor.id!, tipo: 'setor' })} className="p-2 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg">
                                  <Trash2 size={18} />
                                </button>
                              </div>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )
              )}
            </div>
          </div>
        </div>
      </main>

      {/* --- MODAL DE MEMBRO --- */}
      {isMembroModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-sm animate-in fade-in">
          <div className="bg-white rounded-xl shadow-xl w-full max-w-md overflow-hidden animate-in zoom-in-95">
            <div className="flex items-center justify-between p-5 border-b border-slate-100">
              <h2 className="text-lg font-bold text-slate-900">{editingMembroId ? 'Editar Membro' : 'Novo Membro'}</h2>
              <button onClick={() => setIsMembroModalOpen(false)} className="text-slate-400 hover:text-slate-600 p-1"><X size={20} /></button>
            </div>
            <form onSubmit={handleMembroSubmit} className="p-5 space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Nome Completo</label>
                <input 
                  type="text" required value={membroFormData.nome} onChange={(e) => setMembroFormData({...membroFormData, nome: e.target.value})}
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" placeholder="Ex: João Silva"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Telefone</label>
                <input 
                  type="text" required value={membroFormData.telefone} onChange={(e) => setMembroFormData({...membroFormData, telefone: e.target.value})}
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" placeholder="Ex: 5511999999999"
                />
              </div>
              <div className="pt-4 flex items-center justify-end gap-3 border-t border-slate-100 mt-6">
                <button type="button" onClick={() => setIsMembroModalOpen(false)} className="bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 px-4 py-2 rounded-lg font-medium">Cancelar</button>
                <button type="submit" disabled={saving} className="flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg font-medium disabled:opacity-70">
                  {saving ? <Loader2 size={18} className="animate-spin" /> : 'Salvar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* --- MODAL DE SETOR --- */}
      {isSetorModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-sm animate-in fade-in">
          <div className="bg-white rounded-xl shadow-xl w-full max-w-lg overflow-hidden animate-in zoom-in-95">
            <div className="flex items-center justify-between p-5 border-b border-slate-100">
              <h2 className="text-lg font-bold text-slate-900">{editingSetorId ? 'Editar Setor' : 'Novo Setor'}</h2>
              <button onClick={() => setIsSetorModalOpen(false)} className="text-slate-400 hover:text-slate-600 p-1"><X size={20} /></button>
            </div>
            <form onSubmit={handleSetorSubmit} className="p-5 space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Nome do Setor</label>
                <input 
                  type="text" required value={setorFormData.nome} onChange={(e) => setSetorFormData({...setorFormData, nome: e.target.value})}
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" placeholder="Ex: Comercial, Financeiro..."
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Descrição</label>
                <textarea 
                  value={setorFormData.descricao} onChange={(e) => setSetorFormData({...setorFormData, descricao: e.target.value})}
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 min-h-[80px]" placeholder="Breve descrição do setor (opcional)"
                />
              </div>
              
              {/* Seleção de Membros para o Setor */}
              <div className="pt-2">
                <label className="block text-sm font-medium text-slate-700 mb-2">Membros do Setor</label>
                {membros.length === 0 ? (
                  <p className="text-sm text-slate-500 italic bg-slate-50 p-3 rounded-lg border border-slate-200">
                    Nenhum membro cadastrado. Vá na aba "Membros" para adicioná-rlos primeiro.
                  </p>
                ) : (
                  <div className="max-h-40 overflow-y-auto border border-slate-200 rounded-lg bg-slate-50 divide-y divide-slate-100">
                    {membros.map(m => (
                      <label key={m.id} className="flex items-center gap-3 p-3 hover:bg-slate-100 cursor-pointer transition-colors">
                        <input 
                          type="checkbox" 
                          checked={setorFormData.membrosIds.includes(m.id!)}
                          onChange={() => toggleMembroSetor(m.id!)}
                          className="w-4 h-4 text-blue-600 rounded focus:ring-blue-500 border-slate-300"
                        />
                        <span className="text-sm font-medium text-slate-700">{m.nome}</span>
                        <span className="text-xs text-slate-400 ml-auto">{m.telefone}</span>
                      </label>
                    ))}
                  </div>
                )}
              </div>

              <div className="pt-4 flex items-center justify-end gap-3 border-t border-slate-100 mt-6">
                <button type="button" onClick={() => setIsSetorModalOpen(false)} className="bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 px-4 py-2 rounded-lg font-medium">Cancelar</button>
                <button type="submit" disabled={saving} className="flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg font-medium disabled:opacity-70">
                  {saving ? <Loader2 size={18} className="animate-spin" /> : 'Salvar Setor'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* --- MODAL CONFIRMAÇÃO DE DELETAR (REUTILIZÁVEL) --- */}
      <DeleteConfirmationModal
        isOpen={itemToDelete !== null}
        onClose={() => setItemToDelete(null)}
        onConfirm={executeDelete}
        isDeleting={isDeleting}
        title={`Excluir ${itemToDelete?.tipo === 'membro' ? 'Membro' : 'Setor'}`}
        message={`Tem certeza que deseja excluir este ${itemToDelete?.tipo}? Esta ação não pode ser desfeita e pode afetar regras de distribuição ativas.`}
      />
    </div>
  );
}