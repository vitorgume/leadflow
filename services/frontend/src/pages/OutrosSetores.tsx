import React, { useState, useEffect, useCallback } from 'react';
import Sidebar from '../components/Sidebar';
import { 
  Building2, Users, Plus, Pencil, Trash2, X, Loader2, Search, AlertCircle, Contact
} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { membroService } from '../services/membroService';
import DeleteConfirmationModal from '../components/DeleteConfirmationModal';
import type { MembroDTO } from '../types/outrosSetores';

export default function OutrosSetores() {
  const { user } = useAuth();
  
  // --- ESTADOS DE UI ---
  const [activeTab, setActiveTab] = useState<'membros' | 'setores'>('membros');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  
  // --- ESTADOS DE DADOS ---
  const [membros, setMembros] = useState<MembroDTO[]>([]);
  
  // --- ESTADOS DO MODAL (CRIAR/EDITAR) ---
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [formData, setFormData] = useState({ nome: '', telefone: '' });

  // --- ESTADOS DO MODAL (DELETAR) ---
  const [membroToDelete, setMembroToDelete] = useState<string | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  // --- CARREGAR DADOS ---
  const carregarMembros = useCallback(async () => {
    if (!user) return;
    setLoading(true);
    setError(null);
    try {
      const dados = await membroService.listar(user.id);
      setMembros(dados || []);
    } catch (err: any) {
      setError(err.message || 'Erro ao carregar a lista de membros.');
    } finally {
      setLoading(false);
    }
  }, [user]);

  useEffect(() => {
    if (activeTab === 'membros') {
      carregarMembros();
    }
  }, [activeTab, carregarMembros]);

  // --- HANDLERS DO MODAL DE CADASTRO ---
  const abrirModalNovo = () => {
    setEditingId(null);
    setFormData({ nome: '', telefone: '' });
    setIsModalOpen(true);
  };

  const abrirModalEditar = (membro: MembroDTO) => {
    setEditingId(membro.id || null);
    setFormData({ nome: membro.nome, telefone: membro.telefone });
    setIsModalOpen(true);
  };

  const fecharModal = () => {
    setIsModalOpen(false);
    setFormData({ nome: '', telefone: '' });
    setEditingId(null);
  };

  // --- SUBMIT E DELETE ---
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user || !formData.nome || !formData.telefone) return;

    setSaving(true);
    setError(null);

    try {
      const payload: MembroDTO = {
        nome: formData.nome,
        telefone: formData.telefone,
        usuario: { id: user.id }
      };

      if (editingId) {
        await membroService.alterar(editingId, payload);
      } else {
        await membroService.cadastrar(payload);
      }
      
      await carregarMembros();
      fecharModal();
    } catch (err: any) {
      setError(err.message || 'Erro ao salvar o membro.');
    } finally {
      setSaving(false);
    }
  };

  const executeDelete = async () => {
    if (!membroToDelete) return;
    
    setIsDeleting(true);
    setError(null);
    
    try {
      await membroService.deletar(membroToDelete);
      setMembros(prev => prev.filter(m => m.id !== membroToDelete));
      setMembroToDelete(null); // Fecha o modal após o sucesso
    } catch (err: any) {
      setError(err.message || 'Erro ao excluir o membro.');
      setMembroToDelete(null); // Fecha o modal mesmo com erro para exibir o alerta na tela
    } finally {
      setIsDeleting(false);
    }
  };

  // --- FILTRO ---
  const membrosFiltrados = membros.filter(m => 
    m.nome.toLowerCase().includes(searchTerm.toLowerCase()) || 
    m.telefone.includes(searchTerm)
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
              onClick={() => setActiveTab('membros')}
              className={`flex items-center gap-2 px-6 py-3 border-b-2 font-medium text-sm transition-colors ${
                activeTab === 'membros' 
                  ? 'border-blue-600 text-blue-600' 
                  : 'border-transparent text-slate-500 hover:text-slate-700 hover:border-slate-300'
              }`}
            >
              <Users size={18} /> Membros
            </button>
            <button
              onClick={() => setActiveTab('setores')}
              className={`flex items-center gap-2 px-6 py-3 border-b-2 font-medium text-sm transition-colors ${
                activeTab === 'setores' 
                  ? 'border-blue-600 text-blue-600' 
                  : 'border-transparent text-slate-500 hover:text-slate-700 hover:border-slate-300'
              }`}
            >
              <Building2 size={18} /> Setores
            </button>
          </div>

          {/* CONTEÚDO: MEMBROS */}
          {activeTab === 'membros' && (
            <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
              {/* Toolbar */}
              <div className="p-4 sm:p-6 border-b border-slate-100 bg-slate-50/50 flex flex-col sm:flex-row gap-4 items-center justify-between">
                <div className="relative w-full sm:w-96">
                  <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                  <input 
                    type="text" 
                    placeholder="Buscar por nome ou telefone..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-full pl-10 pr-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-sm"
                  />
                </div>
                <button 
                  onClick={abrirModalNovo}
                  className="flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors shadow-sm w-full sm:w-auto shrink-0"
                >
                  <Plus size={18} /> Adicionar Membro
                </button>
              </div>

              {/* Lista */}
              <div className="p-0">
                {loading ? (
                  <div className="flex flex-col items-center justify-center py-16 text-slate-500">
                    <Loader2 className="animate-spin text-blue-600 mb-4" size={32} />
                    <p>Carregando membros...</p>
                  </div>
                ) : membrosFiltrados.length === 0 ? (
                  <div className="flex flex-col items-center justify-center py-16 text-slate-500">
                    <Users size={48} className="text-slate-300 mb-4" />
                    <p className="text-lg font-medium text-slate-900">Nenhum membro encontrado</p>
                    <p className="text-sm mt-1">Clique em "Adicionar Membro" para cadastrar o primeiro.</p>
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
                                <button 
                                  onClick={() => abrirModalEditar(membro)}
                                  className="p-2 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                                  title="Editar"
                                >
                                  <Pencil size={18} />
                                </button>
                                <button 
                                  onClick={() => setMembroToDelete(membro.id || null)}
                                  className="p-2 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
                                  title="Excluir"
                                >
                                  <Trash2 size={18} />
                                </button>
                              </div>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            </div>
          )}

          {/* CONTEÚDO: SETORES (Placeholder) */}
          {activeTab === 'setores' && (
            <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-12 text-center">
              <Building2 size={48} className="mx-auto text-slate-300 mb-4" />
              <h3 className="text-xl font-bold text-slate-900 mb-2">Gerenciamento de Setores</h3>
              <p className="text-slate-500 max-w-md mx-auto">
                Em breve você poderá criar setores específicos e vincular os membros a eles, definindo regras de distribuição.
              </p>
            </div>
          )}

        </div>
      </main>

      {/* MODAL CRIAR/EDITAR MEMBRO */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-sm animate-in fade-in duration-200">
          <div className="bg-white rounded-xl shadow-xl w-full max-w-md overflow-hidden animate-in zoom-in-95 duration-200">
            <div className="flex items-center justify-between p-5 border-b border-slate-100">
              <h2 className="text-lg font-bold text-slate-900">
                {editingId ? 'Editar Membro' : 'Novo Membro'}
              </h2>
              <button 
                onClick={fecharModal}
                className="text-slate-400 hover:text-slate-600 transition-colors p-1"
              >
                <X size={20} />
              </button>
            </div>
            
            <form onSubmit={handleSubmit} className="p-5 space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Nome Completo</label>
                <input 
                  type="text" 
                  required
                  value={formData.nome}
                  onChange={(e) => setFormData({...formData, nome: e.target.value})}
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
                  placeholder="Ex: João Silva"
                />
              </div>
              
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Telefone (WhatsApp)</label>
                <input 
                  type="text" 
                  required
                  value={formData.telefone}
                  onChange={(e) => setFormData({...formData, telefone: e.target.value})}
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
                  placeholder="Ex: 5511999999999"
                />
              </div>

              <div className="pt-4 flex items-center justify-end gap-3 border-t border-slate-100 mt-6">
                <button
                  type="button"
                  onClick={fecharModal}
                  className="flex items-center justify-center gap-2 bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 px-4 py-2 rounded-lg font-medium transition-colors"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={saving}
                  className="flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg font-medium transition-colors shadow-sm disabled:opacity-70"
                >
                  {saving ? <Loader2 size={18} className="animate-spin" /> : (editingId ? 'Salvar Alterações' : 'Cadastrar')}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL CONFIRMAÇÃO DE DELETAR */}
      <DeleteConfirmationModal
        isOpen={membroToDelete !== null}
        onClose={() => setMembroToDelete(null)}
        onConfirm={executeDelete}
        isDeleting={isDeleting}
        title="Excluir Membro"
        message="Tem certeza que deseja excluir este membro? Esta ação não pode ser desfeita."
      />
    </div>
  );
}