import React, { useState, useEffect, useCallback } from 'react';
import Sidebar from '../components/Sidebar';
import { Settings2, Plus, Trash2, Edit, Loader2, GitBranch, AlertCircle, Check } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { configVendedorService } from '../services/configuracaoVendedorService';
import { OperadorLogico, ConectorLogico, type ConfiguracaoEscolhaVendedorDTO, type CondicaoDTO } from '../types/configuracaoVendedor';
import DeleteConfirmationModal from '../components/DeleteConfirmationModal';

// Dicionário para traduzir os operadores lógicos para o usuário
const OPERADORES_LABELS: Record<string, string> = {
  [OperadorLogico.EQUAL]: 'Igual a (=)',
  [OperadorLogico.NOT_EQUAL]: 'Diferente de (≠)',
  [OperadorLogico.CONTAINS]: 'Contém',
  [OperadorLogico.IS_GREATER_THAN]: 'Maior que (>)',
  [OperadorLogico.IS_LESS_THAN]: 'Menor que (<)',
  [OperadorLogico.IS_GREATER_THAN_OR_EQUAL_TO]: 'Maior ou igual (≥)',
  [OperadorLogico.IS_LESS_THAN_OR_EQUAL_TO]: 'Menor ou igual (≤)',
};

export default function RegrasDistribuicao() {
  const { user } = useAuth();
  
  // Estados de Dados
  const [configuracoes, setConfiguracoes] = useState<ConfiguracaoEscolhaVendedorDTO[]>([]);
  const [vendedoresOptions, setVendedoresOptions] = useState<any[]>([]);
  const [camposQualificacao, setCamposQualificacao] = useState<string[]>([]);
  
  // Estados de Tela
  const [loading, setLoading] = useState(true);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [configToDelete, setConfigToDelete] = useState<string | null>(null);

  // Estado do Formulário
  const [formData, setFormData] = useState<ConfiguracaoEscolhaVendedorDTO>({
    usuario: { id: '' },
    vendedores: [],
    condicoes: [],
    prioridade: 1
  });

  const fetchData = useCallback(async () => {
    if (!user) return;
    setLoading(true);
    try {
      const [configs, usuarioDb, vends] = await Promise.all([
        configVendedorService.listar(user.id),
        configVendedorService.buscarUsuario(user.id),
        configVendedorService.buscarVendedores(user.id)
      ]);
      
      setConfiguracoes(configs || []);
      setVendedoresOptions(vends || []);
      
      // Extrai as chaves do Map de atributos de qualificação do usuário
      if (usuarioDb?.atributos_qualificacao) {
        setCamposQualificacao(Object.keys(usuarioDb.atributos_qualificacao));
      }
    } catch (error) {
      console.error("Erro ao buscar dados", error);
    } finally {
      setLoading(false);
    }
  }, [user]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  // --- HANDLERS DO FORMULÁRIO ---
  const handleOpenForm = (config?: ConfiguracaoEscolhaVendedorDTO) => {
    if (config) {
      setFormData(config);
    } else {
      setFormData({
        usuario: { id: user!.id },
        vendedores: [],
        condicoes: [{ campo: '', operador_logico: OperadorLogico.EQUAL, valor: '', conector_logico: null }],
        prioridade: 1
      });
    }
    setIsFormOpen(true);
  };

  const handleToggleVendedor = (vendedorId: number) => {
    setFormData(prev => {
      const isSelected = prev.vendedores.some(v => v.id === vendedorId);
      if (isSelected) {
        return { ...prev, vendedores: prev.vendedores.filter(v => v.id !== vendedorId) };
      } else {
        return { ...prev, vendedores: [...prev.vendedores, { id: vendedorId }] };
      }
    });
  };

  const handleAddCondicao = () => {
    setFormData(prev => {
      const novasCondicoes = [...prev.condicoes];
      // Se já existe uma anterior, força ela a ter um conector
      if (novasCondicoes.length > 0) {
        novasCondicoes[novasCondicoes.length - 1].conector_logico = ConectorLogico.AND;
      }
      novasCondicoes.push({ campo: '', operador_logico: OperadorLogico.EQUAL, valor: '', conector_logico: null });
      return { ...prev, condicoes: novasCondicoes };
    });
  };

  const handleRemoveCondicao = (index: number) => {
    setFormData(prev => {
      const novas = prev.condicoes.filter((_, i) => i !== index);
      if (novas.length > 0) {
        novas[novas.length - 1].conector_logico = null; // O último nunca tem conector
      }
      return { ...prev, condicoes: novas };
    });
  };

  const handleChangeCondicao = (index: number, field: keyof CondicaoDTO, value: string) => {
    setFormData(prev => {
      const novas = [...prev.condicoes];
      novas[index] = { ...novas[index], [field]: value };
      return { ...prev, condicoes: novas };
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (formData.vendedores.length === 0) {
      alert("Selecione pelo menos um vendedor para esta regra.");
      return;
    }
    setIsSaving(true);
    try {
      // Garante que a última condição tenha conector NULL
      const payload = { ...formData };
      if (payload.condicoes.length > 0) {
        payload.condicoes[payload.condicoes.length - 1].conector_logico = null;
      }

      if (payload.id) {
        await configVendedorService.alterar(payload.id, payload);
      } else {
        await configVendedorService.cadastrar(payload);
      }
      setIsFormOpen(false);
      fetchData();
    } catch (error) {
      console.error("Erro ao salvar", error);
    } finally {
      setIsSaving(false);
    }
  };

  const executeDelete = async () => {
    if (!configToDelete) return;
    try {
      await configVendedorService.deletar(configToDelete);
      setConfigToDelete(null);
      fetchData();
    } catch (error) {
      console.error("Erro ao deletar", error);
    }
  };

  return (
    <div className="flex flex-col lg:flex-row min-h-screen bg-slate-50 font-sans text-slate-900">
      <Sidebar activeItem="Regras de Distribuição" />

      <main className="flex-1 lg:ml-64 p-4 sm:p-6 lg:p-8 transition-all duration-300">
        <div className="max-w-6xl mx-auto">
          
          <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
            <div className="flex items-center gap-3">
              <div className="bg-indigo-100 p-2 rounded-xl text-indigo-600">
                <GitBranch size={24} />
              </div>
              <div>
                <h1 className="text-2xl font-bold text-slate-900 leading-tight">Regras de Distribuição</h1>
                <p className="text-slate-500 text-sm mt-0.5 font-medium">Configure as condições para roteamento de leads entre os vendedores.</p>
              </div>
            </div>
            
            {!isFormOpen && (
              <button
                onClick={() => handleOpenForm()}
                className="flex items-center justify-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg font-medium transition-colors shadow-sm"
              >
                <Plus size={20} />
                Nova Regra
              </button>
            )}
          </div>

          {loading ? (
             <div className="flex items-center justify-center p-12 text-indigo-600">
               <Loader2 className="animate-spin" size={32} />
             </div>
          ) : isFormOpen ? (
            
            /* FORMULÁRIO DE CADASTRO / EDIÇÃO */
            <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6 sm:p-8">
              <div className="flex justify-between items-center mb-6 border-b border-slate-100 pb-4">
                <h2 className="text-lg font-bold text-slate-900">{formData.id ? 'Editar Regra' : 'Criar Nova Regra'}</h2>
              </div>

              <form onSubmit={handleSubmit} className="space-y-8">
                
                {/* Prioridade */}
                <div className="w-full md:w-1/3">
                  <label className="block text-sm font-medium text-slate-700 mb-1">Nível de Prioridade</label>
                  <p className="text-xs text-slate-500 mb-2">Quanto menor o número, maior a prioridade (Ex: 1 é prioridade máxima).</p>
                  <input
                    type="number"
                    min="1"
                    required
                    value={formData.prioridade}
                    onChange={(e) => setFormData({...formData, prioridade: parseInt(e.target.value)})}
                    className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all text-sm"
                  />
                </div>

                {/* Vendedores Participantes */}
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">Vendedores Destino</label>
                  <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
                    {vendedoresOptions.map(vend => {
                      const isSelected = formData.vendedores.some(v => v.id === vend.id);
                      return (
                        <div 
                          key={vend.id}
                          onClick={() => handleToggleVendedor(vend.id)}
                          className={`cursor-pointer border rounded-lg p-3 flex items-center justify-between transition-colors ${isSelected ? 'bg-indigo-50 border-indigo-300' : 'bg-white border-slate-200 hover:border-indigo-300'}`}
                        >
                          <span className={`text-sm font-medium ${isSelected ? 'text-indigo-800' : 'text-slate-700'}`}>{vend.nome}</span>
                          {isSelected && <Check size={18} className="text-indigo-600" />}
                        </div>
                      );
                    })}
                  </div>
                </div>

                {/* Motor de Regras (Condições) */}
                <div className="bg-slate-50 rounded-xl p-6 border border-slate-200">
                  <div className="flex justify-between items-center mb-4">
                    <label className="block text-sm font-bold text-slate-800">Condições (Gatilhos)</label>
                    <button type="button" onClick={handleAddCondicao} className="text-sm font-medium text-indigo-600 hover:text-indigo-800 flex items-center gap-1">
                      <Plus size={16} /> Adicionar Condição
                    </button>
                  </div>

                  {camposQualificacao.length === 0 && (
                    <div className="mb-4 p-3 bg-amber-50 border border-amber-200 rounded-lg text-amber-700 text-sm flex gap-2 items-center">
                      <AlertCircle size={18} />
                      Você não possui atributos de qualificação cadastrados para gerar regras.
                    </div>
                  )}

                  <div className="space-y-4">
                    {formData.condicoes.map((condicao, index) => (
                      <div key={index} className="flex flex-col gap-3 relative bg-white p-4 rounded-lg border border-slate-200 shadow-sm">
                        <div className="flex flex-col md:flex-row gap-3 items-end">
                          
                          <div className="flex-1 w-full">
                            <label className="block text-xs font-medium text-slate-500 mb-1">Campo (Atributo)</label>
                            <select 
                              required
                              value={condicao.campo}
                              onChange={(e) => handleChangeCondicao(index, 'campo', e.target.value)}
                              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 text-sm"
                            >
                              <option value="">Selecione...</option>
                              {camposQualificacao.map(campo => (
                                <option key={campo} value={campo}>{campo}</option>
                              ))}
                            </select>
                          </div>

                          <div className="flex-1 w-full">
                            <label className="block text-xs font-medium text-slate-500 mb-1">Operador</label>
                            <select 
                              required
                              value={condicao.operador_logico}
                              onChange={(e) => handleChangeCondicao(index, 'operador_logico', e.target.value)}
                              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 text-sm"
                            >
                              {Object.entries(OPERADORES_LABELS).map(([key, label]) => (
                                <option key={key} value={key}>{label}</option>
                              ))}
                            </select>
                          </div>

                          <div className="flex-1 w-full">
                            <label className="block text-xs font-medium text-slate-500 mb-1">Valor de Comparação</label>
                            <input 
                              type="text"
                              required
                              value={condicao.valor}
                              onChange={(e) => handleChangeCondicao(index, 'valor', e.target.value)}
                              placeholder="Ex: Qualificado"
                              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 text-sm"
                            />
                          </div>

                          <button 
                            type="button" 
                            onClick={() => handleRemoveCondicao(index)}
                            disabled={formData.condicoes.length === 1}
                            className="p-2.5 text-rose-500 hover:bg-rose-50 rounded-lg transition-colors disabled:opacity-30 disabled:hover:bg-transparent"
                            title="Remover regra"
                          >
                            <Trash2 size={18} />
                          </button>
                        </div>

                        {/* Conector lógico (Não mostra no último item) */}
                        {index < formData.condicoes.length - 1 && (
                          <div className="flex justify-center mt-2 relative">
                            <div className="absolute inset-0 flex items-center" aria-hidden="true"><div className="w-full border-t border-slate-200"></div></div>
                            <div className="relative bg-white px-4">
                              <select 
                                value={condicao.conector_logico || ConectorLogico.AND}
                                onChange={(e) => handleChangeCondicao(index, 'conector_logico', e.target.value)}
                                className="bg-slate-100 border border-slate-300 text-slate-700 text-xs font-bold rounded-full px-3 py-1 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                              >
                                <option value={ConectorLogico.AND}>E (AND)</option>
                                <option value={ConectorLogico.OR}>OU (OR)</option>
                              </select>
                            </div>
                          </div>
                        )}
                      </div>
                    ))}
                  </div>
                </div>

                <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
                  <button
                    type="button"
                    onClick={() => setIsFormOpen(false)}
                    className="flex items-center justify-center gap-2 bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 px-4 py-2 rounded-lg font-medium transition-colors"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    disabled={isSaving}
                    className="flex items-center justify-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-2 rounded-lg font-medium transition-colors shadow-sm disabled:opacity-70"
                  >
                    {isSaving ? <Loader2 size={18} className="animate-spin" /> : <Settings2 size={18} />}
                    Salvar Regra
                  </button>
                </div>
              </form>
            </div>

          ) : (
            
            /* LISTAGEM DE REGRAS */
            <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
              {configuracoes.length === 0 ? (
                <div className="text-center py-12">
                  <GitBranch className="mx-auto h-12 w-12 text-slate-300 mb-3" />
                  <h3 className="text-lg font-medium text-slate-900">Nenhuma regra configurada</h3>
                  <p className="text-sm text-slate-500 mt-1">Crie sua primeira regra de distribuição para automatizar as vendas.</p>
                </div>
              ) : (
                <div className="grid grid-cols-1 gap-4">
                  {configuracoes.map((config) => (
                    <div key={config.id} className="border border-slate-200 rounded-xl p-5 hover:border-indigo-200 transition-colors flex flex-col md:flex-row justify-between md:items-center gap-4">
                      
                      <div className="flex-1">
                        <div className="flex items-center gap-3 mb-2">
                          <span className="px-2.5 py-0.5 bg-indigo-100 text-indigo-800 text-xs font-bold rounded-md">Prioridade: {config.prioridade}</span>
                          <span className="text-sm font-medium text-slate-600">
                            {config.vendedores.length} Vendedor(es) Vinculado(s)
                          </span>
                        </div>
                        
                        <div className="flex flex-wrap items-center gap-2 text-sm text-slate-800 font-mono mt-3">
                          {config.condicoes.map((cond, idx) => (
                            <React.Fragment key={cond.id || idx}>
                              <span className="px-2 py-1 bg-slate-100 rounded-md border border-slate-200">
                                {cond.campo} <strong className="text-indigo-600 mx-1">{OPERADORES_LABELS[cond.operador_logico as string]}</strong> '{cond.valor}'
                              </span>
                              {cond.conector_logico && (
                                <span className="text-xs font-bold text-slate-400 mx-1 uppercase">{cond.conector_logico}</span>
                              )}
                            </React.Fragment>
                          ))}
                        </div>
                      </div>

                      <div className="flex items-center justify-end gap-2 md:w-auto w-full md:border-l border-slate-100 md:pl-4">
                        <button onClick={() => handleOpenForm(config)} className="p-2 text-slate-400 hover:bg-indigo-50 hover:text-indigo-600 rounded-lg transition-colors">
                          <Edit size={20} />
                        </button>
                        <button onClick={() => setConfigToDelete(config.id!)} className="p-2 text-slate-400 hover:bg-rose-50 hover:text-rose-600 rounded-lg transition-colors">
                          <Trash2 size={20} />
                        </button>
                      </div>

                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

        </div>
      </main>

      <DeleteConfirmationModal
        isOpen={configToDelete !== null}
        onClose={() => setConfigToDelete(null)}
        onConfirm={executeDelete}
        isDeleting={false}
        title="Excluir Regra"
        message="Tem certeza que deseja excluir esta regra de distribuição? Seus leads deixarão de seguir essa condição imediatamente."
      />
    </div>
  );
}