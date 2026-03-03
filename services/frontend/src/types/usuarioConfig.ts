export const CrmType = {
  KOMMO: 'KOMMO',
  MOSKIT: 'MOSKIT', // Atualizado com o seu backend
  NENHUM: 'NENHUM'
} as const;

export type CrmType = typeof CrmType[keyof typeof CrmType];

export interface ConfiguracaoCrmDTO {
  crm_type?: CrmType | string;
  acess_token?: string;
  crm_url?: string; // Novo campo
  id_tag_ativo?: string;
  id_tag_inativo?: string;
  id_etapa_ativos?: string;
  id_etapa_inativos?: string;
  mapeamento_campos?: Record<string, string>; // Novo campo
}

export interface UsuarioCompletoDTO {
  id: string;
  nome: string;
  email: string;
  telefone: string;
  senha?: string;
  telefone_conectado: string;
  mensagem_direcionamento_vendedor?: string;
  mensagem_recontato_g1?: string;
  atributos_qualificacao?: Record<string, any>;
  configuracao_crm?: ConfiguracaoCrmDTO;
  whatsapp_token?: string;
  whatsapp_id_instance?: string;
  agente_api_key?: string;
}
