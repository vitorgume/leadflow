export const OperadorLogico = {
  EQUAL: 'EQUAL',
  NOT_EQUAL: 'NOT_EQUAL',
  IS_GREATER_THAN: 'IS_GREATER_THAN',
  IS_LESS_THAN: 'IS_LESS_THAN',
  IS_GREATER_THAN_OR_EQUAL_TO: 'IS_GREATER_THAN_OR_EQUAL_TO',
  IS_LESS_THAN_OR_EQUAL_TO: 'IS_LESS_THAN_OR_EQUAL_TO',
  CONTAINS: 'CONTAINS'
} as const;

export type OperadorLogico = typeof OperadorLogico[keyof typeof OperadorLogico];

export const ConectorLogico = {
  AND: 'AND',
  OR: 'OR'
} as const;

export type ConectorLogico = typeof ConectorLogico[keyof typeof ConectorLogico];

export interface CondicaoDTO {
  id?: string;
  campo: string;
  operador_logico: OperadorLogico | string;
  valor: string;
  conector_logico?: ConectorLogico | string | null;
}

export interface ConfiguracaoEscolhaVendedorDTO {
  id?: string;
  usuario: { id: string };
  vendedores: { id: number; nome?: string }[];
  condicoes: CondicaoDTO[];
  prioridade: number;
}