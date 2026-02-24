// src/pages/Vendedores.tsx
import React, { useState, useEffect, useCallback } from 'react';
import Sidebar from '../components/Sidebar';
import { Users, Plus, Search, Edit, Trash2 } from 'lucide-react';
import type { Vendedor, VendedorCreateDTO, VendedorUpdateDTO } from '../types/vendedor';
import { getVendedores, createVendedor, updateVendedor, deleteVendedor } from '../services/vendedorService';

// Importando os componentes que extraímos!
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';
import DeleteConfirmationModal from '../components/DeleteConfirmationModal';
import VendedorForm from '../components/vendedores/VendedorForm';

export const Vendedores: React.FC = () => {
  const [vendedores, setVendedores] = useState<Vendedor[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  
  // Form Modal States
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingVendedor, setEditingVendedor] = useState<Vendedor | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  // Delete Confirmation States
  const [vendedorToDelete, setVendedorToDelete] = useState<number | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const fetchVendedores = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getVendedores('e75fabfa-ece2-40c3-9a8e-f15e6109d867');
      setVendedores(data);
    } catch (err) {
      setError('Falha ao carregar a lista de vendedores.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchVendedores();
  }, [fetchVendedores]);

  const filteredVendedores = vendedores.filter(
    (vendedor) =>
      vendedor.nome.toLowerCase().includes(searchTerm.toLowerCase()) ||
      vendedor.telefone.includes(searchTerm)
  );

  const handleAddVendedor = () => {
    setEditingVendedor(null);
    setIsFormOpen(true);
  };

  const handleEditVendedor = (vendedor: Vendedor) => {
    setEditingVendedor(vendedor);
    setIsFormOpen(true);
  };

  const handleSaveVendedor = async (data: VendedorCreateDTO | VendedorUpdateDTO) => {
    setIsSaving(true);
    try {
      if (editingVendedor) {
        await updateVendedor(editingVendedor.id, data as VendedorUpdateDTO);
      } else {
        await createVendedor(data as VendedorCreateDTO);
      }
      setIsFormOpen(false);
      fetchVendedores();
    } catch (err) {
      setError('Falha ao salvar o vendedor.');
      console.error(err);
    } finally {
      setIsSaving(false);
    }
  };

  const confirmDelete = (id: number) => {
    setVendedorToDelete(id);
  };

  const executeDelete = async () => {
    if (vendedorToDelete === null) return;

    setIsDeleting(true);
    try {
      await deleteVendedor(vendedorToDelete);
      setVendedorToDelete(null);
      fetchVendedores();
    } catch (err) {
      setError('Falha ao deletar o vendedor.');
      setVendedorToDelete(null);
      console.error(err);
    } finally {
      setIsDeleting(false);
    }
  };

  return (
    <div className="flex flex-col lg:flex-row min-h-screen bg-slate-50 font-sans text-slate-900">
      <Sidebar activeItem="Vendedores" />

      <main className="flex-1 lg:ml-64 transition-all duration-300">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          
          <div className="flex items-center gap-3 mb-8">
            <div className="bg-indigo-100 p-2 rounded-xl text-indigo-600">
              <Users size={24} />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-slate-900 leading-tight">Gerenciamento de Vendedores</h1>
              <p className="text-slate-500 text-sm mt-0.5 font-medium">Cadastre, edite e gerencie seus vendedores.</p>
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
                onClick={handleAddVendedor}
                className="flex items-center justify-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg font-medium transition-colors shadow-sm w-full md:w-auto"
              >
                <Plus size={20} />
                Adicionar Vendedor
              </button>
            </div>

            {loading && <LoadingSpinner />}
            {error && <ErrorMessage message={error} />}

            {!loading && !error && (
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-slate-200">
                  <thead className="bg-slate-50">
                    <tr>
                      <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Nome</th>
                      <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Telefone</th>
                      <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Status</th>
                      <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Padrão</th>
                      <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">ID CRM</th>
                      <th scope="col" className="px-6 py-3 text-right text-xs font-medium text-slate-500 uppercase tracking-wider">Ações</th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-slate-200">
                    {filteredVendedores.map((vendedor) => (
                      <tr key={vendedor.id}>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-slate-900">{vendedor.nome}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-500">{vendedor.telefone}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm">
                          <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${vendedor.inativo ? 'bg-rose-100 text-rose-800' : 'bg-emerald-100 text-emerald-800'}`}>
                            {vendedor.inativo ? 'Inativo' : 'Ativo'}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm">
                          {vendedor.padrao && (
                             <span className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-indigo-100 text-indigo-800">Sim</span>
                          )}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-500">{vendedor.id_vendedor_crm}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                          <button onClick={() => handleEditVendedor(vendedor)} className="text-indigo-600 hover:text-indigo-900 mr-3 transition-colors" title="Editar">
                            <Edit size={18} />
                          </button>
                          <button onClick={() => confirmDelete(vendedor.id)} className="text-rose-600 hover:text-rose-900 transition-colors" title="Deletar">
                            <Trash2 size={18} />
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {!loading && !error && filteredVendedores.length === 0 && (
              <p className="text-center text-slate-500 mt-4 py-4">Nenhum vendedor encontrado.</p>
            )}

          </div>
        </div>
      </main>

      {/* Uso dos Componentes Extraídos */}
      {isFormOpen && (
        <VendedorForm
          vendedor={editingVendedor}
          onSave={handleSaveVendedor}
          onClose={() => setIsFormOpen(false)}
          isSaving={isSaving}
        />
      )}

      <DeleteConfirmationModal
        isOpen={vendedorToDelete !== null}
        onClose={() => setVendedorToDelete(null)}
        onConfirm={executeDelete}
        isDeleting={isDeleting}
        title="Excluir Vendedor"
        message="Tem certeza que deseja excluir este vendedor? Esta ação não pode ser desfeita e ele perderá acesso."
      />
    </div>
  );
};

export default Vendedores;