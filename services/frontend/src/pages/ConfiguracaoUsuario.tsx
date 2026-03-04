import React, { useState, useEffect, useCallback } from 'react';
import Sidebar from '../components/Sidebar';
// Adicionamos o ícone Power aqui nos imports
import { Settings, MessageSquare, Database, Link as LinkIcon, Plus, Trash2, Save, Loader2, CheckCircle2, AlertCircle, Smartphone, Power } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { usuarioConfigService } from '../services/usuarioConfigService';
import { type UsuarioCompletoDTO, CrmType } from '../types/usuarioConfig';

interface AtributoUI {
  chave: string;
  valor: string;
}

export default function ConfiguracaoUsuario() {
  const { user } = useAuth();
  
  const [formData, setFormData] = useState<UsuarioCompletoDTO | null>(null);
  const [atributosList, setAtributosList] = useState<AtributoUI[]>([]);
  
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const carregarDados = useCallback(async () => {
    if (!user) return;
    setLoading(true);
    setError(null);
    try {
      const dadosDb = await usuarioConfigService.buscar(user.id);
      
      if (dadosDb.configuracao_crm?.acess_token) {
        dadosDb.configuracao_crm.acess_token = '******************';
      }
      
      // Garante defaults para os booleanos caso venham nulos do banco antigo
      if (dadosDb.enviar_contato === undefined) {
          dadosDb.enviar_contato = true;
      }
      if (dadosDb.software_ligado === undefined) {
          dadosDb.software_ligado = true;
      }
      
      setFormData(dadosDb);

      if (dadosDb.atributos_qualificacao) {
        const arr = Object.entries(dadosDb.atributos_qualificacao).map(([chave, valor]) => {
          const chaveComEspaco = chave.replace(/_/g, ' ');
          const chaveFormatada = chaveComEspaco.charAt(0).toUpperCase() + chaveComEspaco.slice(1);
          
          return {
            chave: chaveFormatada,
            valor: String(valor)
          };
        });
        setAtributosList(arr);
      }
    } catch (err: any) {
      setError(err.message || "Erro ao carregar as configurações do usuário.");
    } finally {
      setLoading(false);
    }
  }, [user]);

  useEffect(() => {
    carregarDados();
  }, [carregarDados]);

  // --- HANDLERS DO FORMULÁRIO ---
  const handleTextChange = (field: keyof UsuarioCompletoDTO, value: string) => {
    setFormData(prev => prev ? { ...prev, [field]: value } : null);
  };

  const handleToggleChange = (field: keyof UsuarioCompletoDTO, value: boolean) => {
    setFormData(prev => prev ? { ...prev, [field]: value } : null);
  };

  const handleCrmChange = (field: string, value: string) => {
    setFormData(prev => {
      if (!prev) return null;
      return {
        ...prev,
        configuracao_crm: {
          ...prev.configuracao_crm,
          [field]: value
        }
      };
    });
  };

  const handleMapeamentoChange = (chaveOriginal: string, idCrm: string) => {
    const chaveFormatada = chaveOriginal.trim().replace(/\s+/g, '_').toLowerCase();
    
    setFormData(prev => {
      if (!prev) return null;
      return {
        ...prev,
        configuracao_crm: {
          ...prev.configuracao_crm,
          mapeamento_campos: {
            ...prev.configuracao_crm?.mapeamento_campos,
            [chaveFormatada]: idCrm
          }
        }
      };
    });
  };

  // --- HANDLERS DOS ATRIBUTOS DINÂMICOS ---
  const addAtributo = () => {
    setAtributosList(prev => [...prev, { chave: '', valor: '' }]);
  };

  const removeAtributo = (index: number) => {
    setAtributosList(prev => prev.filter((_, i) => i !== index));
  };

  const updateAtributo = (index: number, field: 'chave' | 'valor', val: string) => {
    const novaLista = [...atributosList];
    novaLista[index][field] = val;
    setAtributosList(novaLista);
  };

  // --- SUBMIT ---
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData || !user) return;

    setSaving(true);
    setSuccess(false);
    setError(null);

    try {
      const novoMapaAtributos: Record<string, string> = {};
      atributosList.forEach(attr => {
        if (attr.chave.trim()) {
          const chaveFormatada = attr.chave.trim().replace(/\s+/g, '_').toLowerCase();
          novoMapaAtributos[chaveFormatada] = attr.valor.trim();
        }
      });

      const payloadFinal: UsuarioCompletoDTO = {
        ...formData,
        atributos_qualificacao: novoMapaAtributos
      };

      if (payloadFinal.configuracao_crm?.acess_token === '******************') {
        payloadFinal.configuracao_crm.acess_token = '';
      }

      await usuarioConfigService.alterar(user.id, payloadFinal);
      setSuccess(true);
      setTimeout(() => setSuccess(false), 3000);
    } catch (err: any) {
      setError(err.message || "Falha ao salvar as configurações.");
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="flex h-screen bg-slate-50 items-center justify-center">
        <Loader2 className="animate-spin text-blue-600" size={40} />
      </div>
    );
  }

  return (
    <div className="flex flex-col lg:flex-row min-h-screen bg-slate-50 font-sans text-slate-900">
      <Sidebar activeItem="Configurações" />

      <main className="flex-1 lg:ml-64 p-4 sm:p-6 lg:p-8 transition-all duration-300">
        <div className="w-full">
          
          <div className="flex items-center justify-between mb-8">
            <div className="flex items-center gap-3">
              <div className="bg-blue-100 p-2 rounded-xl text-blue-600">
                <Settings size={24} />
              </div>
              <div>
                <h1 className="text-2xl font-bold text-slate-900 leading-tight">Configurações do Sistema</h1>
                <p className="text-slate-500 text-sm mt-0.5 font-medium">Gerencie mensagens da IA, integrações e atributos.</p>
              </div>
            </div>
            
            {success && (
              <span className="flex items-center gap-1 px-3 py-1.5 bg-emerald-100 text-emerald-700 text-sm font-bold rounded-lg animate-in fade-in duration-300">
                <CheckCircle2 size={16} /> Salvo!
              </span>
            )}
          </div>

          {error && (
            <div className="mb-6 p-4 bg-rose-50 border border-rose-200 rounded-xl flex items-start gap-3 text-rose-700 shadow-sm animate-in fade-in slide-in-from-top-2 duration-300">
              <AlertCircle className="shrink-0 mt-0.5" size={20} />
              <p className="text-sm font-medium">{error}</p>
            </div>
          )}

          {formData && (
            <form onSubmit={handleSubmit} className="space-y-8 flex flex-col">
              
              {/* NOVA SESSÃO: STATUS DO SISTEMA (ON/OFF) */}
              <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
                <div className="p-6 border-b border-slate-100 bg-slate-50/50 flex items-center gap-2">
                  <Power size={20} className="text-blue-600" />
                  <h2 className="text-lg font-bold text-slate-900">Status do Sistema</h2>
                </div>
                <div className="p-6">
                  <div className="flex items-center justify-between p-4 bg-slate-50 rounded-lg border border-slate-200">
                    <div className="flex flex-col">
                      <span className="text-sm font-bold text-slate-900">Software Ativo</span>
                      <span className="text-xs text-slate-500 mt-1 max-w-[80%]">
                        Liga ou desliga completamente o processamento da IA e o envio de mensagens. Se desligado, o robô irá pausar o atendimento.
                      </span>
                    </div>
                    
                    {/* Toggle Switch UI */}
                    <button
                      type="button"
                      role="switch"
                      aria-checked={formData.software_ligado}
                      onClick={() => handleToggleChange('software_ligado', !formData.software_ligado)}
                      className={`${
                        formData.software_ligado ? 'bg-blue-600' : 'bg-slate-300'
                      } relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-600 focus:ring-offset-2`}
                    >
                      <span
                        aria-hidden="true"
                        className={`${
                          formData.software_ligado ? 'translate-x-5' : 'translate-x-0'
                        } pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out`}
                      />
                    </button>
                  </div>
                </div>
              </div>

              {/* SESSÃO DE PREFERÊNCIAS DE NOTIFICAÇÃO (WhatsApp) */}
              <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
                <div className="p-6 border-b border-slate-100 bg-slate-50/50 flex items-center gap-2">
                  <Smartphone size={20} className="text-blue-600" />
                  <h2 className="text-lg font-bold text-slate-900">Preferências de Notificação</h2>
                </div>
                <div className="p-6">
                  <div className="flex items-center justify-between p-4 bg-slate-50 rounded-lg border border-slate-200">
                    <div className="flex flex-col">
                      <span className="text-sm font-bold text-slate-900">Enviar Contato via WhatsApp</span>
                      <span className="text-xs text-slate-500 mt-1 max-w-[80%]">
                        Habilite para que a IA envie o VCard (cartão de contato clicável) do Lead diretamente para o WhatsApp do vendedor responsável.
                      </span>
                    </div>
                    
                    {/* Toggle Switch UI */}
                    <button
                      type="button"
                      role="switch"
                      aria-checked={formData.enviar_contato}
                      onClick={() => handleToggleChange('enviar_contato', !formData.enviar_contato)}
                      className={`${
                        formData.enviar_contato ? 'bg-blue-600' : 'bg-slate-300'
                      } relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-600 focus:ring-offset-2`}
                    >
                      <span
                        aria-hidden="true"
                        className={`${
                          formData.enviar_contato ? 'translate-x-5' : 'translate-x-0'
                        } pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out`}
                      />
                    </button>
                  </div>
                </div>
              </div>

              {/* SESSÃO 1: MENSAGENS E AUTOMAÇÃO */}
              <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
                <div className="p-6 border-b border-slate-100 bg-slate-50/50 flex items-center gap-2">
                  <MessageSquare size={20} className="text-blue-600" />
                  <h2 className="text-lg font-bold text-slate-900">Mensagens de Automação</h2>
                </div>
                <div className="p-6 space-y-5">
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Mensagem de Direcionamento ao Vendedor</label>
                    <p className="text-xs text-slate-500 mb-2">Mensagem enviada pelo robô instantes antes de transferir o lead para um humano.</p>
                    <textarea 
                      value={formData.mensagem_direcionamento_vendedor || ''}
                      onChange={(e) => handleTextChange('mensagem_direcionamento_vendedor', e.target.value)}
                      className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 min-h-[100px] text-sm"
                      placeholder="Ex: Certo! Vou te transferir para um de nossos especialistas agora mesmo. Aguarde um instante."
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Mensagem de Recontato (G1)</label>
                    <p className="text-xs text-slate-500 mb-2">Mensagem enviada para engajar leads antigos ou que pararam de responder.</p>
                    <textarea 
                      value={formData.mensagem_recontato_g1 || ''}
                      onChange={(e) => handleTextChange('mensagem_recontato_g1', e.target.value)}
                      className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 min-h-[100px] text-sm"
                      placeholder="Ex: Olá! Vi que não conseguimos concluir nossa conversa anterior. Ainda tem interesse?"
                    />
                  </div>
                </div>
              </div>

              {/* SESSÃO 2: ATRIBUTOS DE QUALIFICAÇÃO */}
              <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
                <div className="p-6 border-b border-slate-100 bg-slate-50/50 flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <Database size={20} className="text-blue-600" />
                    <h2 className="text-lg font-bold text-slate-900">Atributos de Qualificação</h2>
                  </div>
                  <button 
                    type="button" 
                    onClick={addAtributo}
                    className="text-sm font-medium text-blue-600 hover:text-blue-800 flex items-center gap-1 transition-colors"
                  >
                    <Plus size={16} /> Adicionar
                  </button>
                </div>
                <div className="p-6">
                  <p className="text-sm text-slate-500 mb-4">
                    Defina os campos customizados que a IA usará para classificar os leads (Ex: "Renda Mensal", "Nível de Interesse").
                  </p>
                  
                  {atributosList.length === 0 ? (
                    <div className="text-center py-6 border-2 border-dashed border-slate-200 rounded-lg">
                      <p className="text-sm text-slate-500">Nenhum atributo cadastrado. Adicione o primeiro!</p>
                    </div>
                  ) : (
                    <div className="space-y-3">
                      {atributosList.map((attr, index) => (
                        <div key={index} className="flex flex-col sm:flex-row items-center gap-3">
                          <div className="w-full sm:w-1/2">
                            <input 
                              type="text" 
                              placeholder="Nome do Atributo (Ex: Cargo ou Interesse)"
                              value={attr.chave}
                              onChange={(e) => updateAtributo(index, 'chave', e.target.value)}
                              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm font-medium"
                            />
                          </div>
                          <div className="w-full sm:w-1/2 flex items-center gap-2">
                            <input 
                              type="text" 
                              placeholder="Valor Padrão / Exemplo"
                              value={attr.valor}
                              onChange={(e) => updateAtributo(index, 'valor', e.target.value)}
                              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm text-slate-600"
                            />
                            <button 
                              type="button" 
                              onClick={() => removeAtributo(index)}
                              className="p-2 text-rose-500 hover:bg-rose-50 rounded-lg transition-colors shrink-0"
                              title="Remover"
                            >
                              <Trash2 size={18} />
                            </button>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>

              {/* SESSÃO 3: CONFIGURAÇÃO DE CRM */}
              <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
                <div className="p-6 border-b border-slate-100 bg-slate-50/50 flex items-center gap-2">
                  <LinkIcon size={20} className="text-blue-600" />
                  <h2 className="text-lg font-bold text-slate-900">Integração CRM</h2>
                </div>
                <div className="p-6 space-y-5">
                  
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                    <div>
                      <label className="block text-sm font-medium text-slate-700 mb-1">Sistema CRM</label>
                      <select 
                        value={formData.configuracao_crm?.crm_type || CrmType.NENHUM}
                        onChange={(e) => handleCrmChange('crm_type', e.target.value)}
                        className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                      >
                        <option value={CrmType.NENHUM}>Nenhum</option>
                        <option value={CrmType.KOMMO}>Kommo CRM</option>
                        <option value={CrmType.MOSKIT}>Moskit CRM</option>
                      </select>
                    </div>
                    
                    {formData.configuracao_crm?.crm_type && formData.configuracao_crm.crm_type !== CrmType.NENHUM && (
                      <div>
                        <label className="block text-sm font-medium text-slate-700 mb-1">URL da API do CRM</label>
                        <input 
                          type="url"
                          value={formData.configuracao_crm?.crm_url || ''}
                          onChange={(e) => handleCrmChange('crm_url', e.target.value)}
                          placeholder="https://suaconta.kommo.com"
                          className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                        />
                      </div>
                    )}
                  </div>

                  {formData.configuracao_crm?.crm_type && formData.configuracao_crm.crm_type !== CrmType.NENHUM && (
                    <>
                      <div>
                        <label className="block text-sm font-medium text-slate-700 mb-1">Access Token da API (Criptografado)</label>
                        <input 
                          type="password"
                          value={formData.configuracao_crm.acess_token || ''}
                          onChange={(e) => handleCrmChange('acess_token', e.target.value)}
                          placeholder="Deixe em branco para manter o atual, ou cole um novo token"
                          className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                        />
                      </div>

                      <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                        <div>
                          <label className="block text-sm font-medium text-slate-700 mb-1">ID da Tag (Ativos)</label>
                          <input 
                            type="text"
                            value={formData.configuracao_crm.id_tag_ativo || ''}
                            onChange={(e) => handleCrmChange('id_tag_ativo', e.target.value)}
                            className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                          />
                        </div>
                        <div>
                          <label className="block text-sm font-medium text-slate-700 mb-1">ID da Tag (Inativos)</label>
                          <input 
                            type="text"
                            value={formData.configuracao_crm.id_tag_inativo || ''}
                            onChange={(e) => handleCrmChange('id_tag_inativo', e.target.value)}
                            className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                          />
                        </div>
                        <div>
                          <label className="block text-sm font-medium text-slate-700 mb-1">ID da Etapa (Pipeline Ativos)</label>
                          <input 
                            type="text"
                            value={formData.configuracao_crm.id_etapa_ativos || ''}
                            onChange={(e) => handleCrmChange('id_etapa_ativos', e.target.value)}
                            className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                          />
                        </div>
                        <div>
                          <label className="block text-sm font-medium text-slate-700 mb-1">ID da Etapa (Pipeline Inativos)</label>
                          <input 
                            type="text"
                            value={formData.configuracao_crm.id_etapa_inativos || ''}
                            onChange={(e) => handleCrmChange('id_etapa_inativos', e.target.value)}
                            className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                          />
                        </div>
                      </div>

                      {atributosList.length > 0 && (
                        <div className="mt-6 pt-6 border-t border-slate-200">
                          <h3 className="text-sm font-bold text-slate-900 mb-3">Mapeamento de Campos (ID no CRM)</h3>
                          <p className="text-xs text-slate-500 mb-4">Insira o ID interno do campo correspondente lá no seu CRM para que o sistema consiga exportar o lead corretamente.</p>
                          
                          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            {atributosList.map((attr, index) => {
                              if (!attr.chave.trim()) return null;
                              const chaveFormatada = attr.chave.trim().replace(/\s+/g, '_').toLowerCase();
                              const valorAtual = formData.configuracao_crm?.mapeamento_campos?.[chaveFormatada] || '';
                              
                              return (
                                <div key={index} className="flex flex-col">
                                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                                    {attr.chave} <span className="text-slate-400 font-normal">({chaveFormatada})</span>
                                  </label>
                                  <input 
                                    type="text"
                                    placeholder="ID do campo customizado no CRM"
                                    value={valorAtual}
                                    onChange={(e) => handleMapeamentoChange(attr.chave, e.target.value)}
                                    className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm bg-slate-50"
                                  />
                                </div>
                              );
                            })}
                          </div>
                        </div>
                      )}
                    </>
                  )}
                </div>
              </div>

              {/* BOTÃO FLUTUANTE DE SALVAR */}
              <div className="sticky bottom-4 mt-6 flex justify-end">
                <button
                  type="submit"
                  disabled={saving}
                  className="flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-8 py-3 rounded-lg font-medium transition-colors shadow-lg shadow-blue-200 disabled:opacity-70"
                >
                  {saving ? (
                    <><Loader2 size={20} className="animate-spin" /> Salvando...</>
                  ) : (
                    <><Save size={20} /> Salvar Configurações</>
                  )}
                </button>
              </div>

            </form>
          )}
        </div>
      </main>
    </div>
  );
}