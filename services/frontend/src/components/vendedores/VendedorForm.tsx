import React, { useState } from 'react';
import { Loader } from 'lucide-react';
import type { Vendedor, VendedorCreateDTO, VendedorUpdateDTO } from '../../types/vendedor';

interface VendedorFormProps {
  vendedor?: Vendedor | null;
  onSave: (vendedor: VendedorCreateDTO | VendedorUpdateDTO) => Promise<void>;
  onClose: () => void;
  availableUsers?: { id: string; nome: string }[];
  isSaving: boolean;
  idUsuario: string
}

const VendedorForm: React.FC<VendedorFormProps> = ({ vendedor, onSave, onClose, isSaving, idUsuario }) => {
  const [formData, setFormData] = useState<VendedorCreateDTO | VendedorUpdateDTO>(
    vendedor
      ? {
          nome: vendedor.nome,
          telefone: vendedor.telefone,
          inativo: vendedor.inativo,
          id_vendedor_crm: vendedor.id_vendedor_crm,
          padrao: vendedor.padrao,
        }
      : {
          nome: '',
          telefone: '',
          inativo: false,
          id_vendedor_crm: 0,
          padrao: false,
          usuario: { id: idUsuario },
        }
  );

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    const checked = (e.target as HTMLInputElement).checked;

    setFormData((prev) => ({
      ...prev,
      [name]: e.target.type === 'checkbox' ? checked : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await onSave(formData);
  };

  return (
    <div className="fixed inset-0 bg-slate-900/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-xl shadow-md w-full max-w-lg p-6 font-sans">
        <h2 className="text-xl font-bold text-slate-900 mb-4">{vendedor ? 'Editar Vendedor' : 'Adicionar Vendedor'}</h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label htmlFor="nome" className="block text-sm font-medium text-slate-700 mb-1">Nome</label>
            <input
              type="text"
              id="nome"
              name="nome"
              value={formData.nome}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all"
              required
            />
          </div>
          <div>
            <label htmlFor="telefone" className="block text-sm font-medium text-slate-700 mb-1">Telefone</label>
            <input
              type="text"
              id="telefone"
              name="telefone"
              value={formData.telefone}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all"
              required
            />
          </div>
          <div>
            <label htmlFor="id_vendedor_crm" className="block text-sm font-medium text-slate-700 mb-1">ID Vendedor CRM</label>
            <input
              type="number"
              id="id_vendedor_crm"
              name="id_vendedor_crm"
              value={formData.id_vendedor_crm}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all"
              required
            />
          </div>
          <div className="flex items-center gap-2">
            <input
              type="checkbox"
              id="inativo"
              name="inativo"
              checked={formData.inativo}
              onChange={handleChange}
              className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-slate-300 rounded"
            />
            <label htmlFor="inativo" className="text-sm font-medium text-slate-700">Inativo</label>
          </div>
          <div className="flex items-center gap-2">
            <input
              type="checkbox"
              id="padrao"
              name="padrao"
              checked={formData.padrao}
              onChange={handleChange}
              className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-slate-300 rounded"
            />
            <label htmlFor="padrao" className="text-sm font-medium text-slate-700">Padrão</label>
          </div>
          <div className="flex justify-end gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="flex items-center justify-center gap-2 bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 px-4 py-2 rounded-lg font-medium transition-colors"
              disabled={isSaving}
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="flex items-center justify-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg font-medium transition-colors shadow-sm"
              disabled={isSaving}
            >
              {isSaving ? (
                <Loader size={20} className="animate-spin" />
              ) : (
                vendedor ? 'Salvar Alterações' : 'Adicionar Vendedor'
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default VendedorForm;