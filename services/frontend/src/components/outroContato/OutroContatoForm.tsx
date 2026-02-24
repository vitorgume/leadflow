import React, { useState } from 'react';
import { Loader } from 'lucide-react';
import type { OutroContato, OutroContatoCreateDTO, OutroContatoUpdateDTO } from '../../types/outroContato';

interface OutroContatoFormProps {
  contato?: OutroContato | null;
  onSave: (data: OutroContatoCreateDTO | OutroContatoUpdateDTO) => Promise<void>;
  onClose: () => void;
  isSaving: boolean;
}

const OutroContatoForm: React.FC<OutroContatoFormProps> = ({ contato, onSave, onClose, isSaving }) => {
  const [formData, setFormData] = useState<OutroContatoCreateDTO | OutroContatoUpdateDTO>(
    contato
      ? {
          nome: contato.nome,
          telefone: contato.telefone,
          descricao: contato.descricao,
          tipo_contato: contato.tipo_contato,
          usuario: { id: 'e75fabfa-ece2-40c3-9a8e-f15e6109d867' }
        }
      : {
          nome: '',
          telefone: '',
          descricao: '',
          tipo_contato: 'PADRAO',
          usuario: { id: 'e75fabfa-ece2-40c3-9a8e-f15e6109d867' }, // Mock do usuário
        }
  );

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await onSave(formData);
  };

  return (
    <div className="fixed inset-0 bg-slate-900/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-xl shadow-md w-full max-w-lg p-6 font-sans">
        <h2 className="text-xl font-bold text-slate-900 mb-4">{contato ? 'Editar Contato' : 'Adicionar Contato'}</h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">Nome</label>
            <input type="text" name="nome" value={formData.nome} onChange={handleChange} required className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all" />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">Telefone</label>
            <input type="text" name="telefone" value={formData.telefone} onChange={handleChange} required className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all" />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">Tipo de Contato</label>
            <select name="tipo_contato" value={formData.tipo_contato} onChange={handleChange} className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all bg-white cursor-pointer">
              <option value="PADRAO">Padrão</option>
              <option value="GERENTE">Gerente</option>
              <option value="CONSULTOR">Consultor</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">Descrição</label>
            <textarea name="descricao" value={formData.descricao} onChange={handleChange} rows={3} className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all" />
          </div>
          
          <div className="flex justify-end gap-3 pt-4">
            <button type="button" onClick={onClose} disabled={isSaving} className="flex items-center justify-center gap-2 bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 px-4 py-2 rounded-lg font-medium transition-colors">
              Cancelar
            </button>
            <button type="submit" disabled={isSaving} className="flex items-center justify-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg font-medium transition-colors shadow-sm">
              {isSaving ? <Loader size={20} className="animate-spin" /> : contato ? 'Salvar Alterações' : 'Adicionar Contato'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default OutroContatoForm;