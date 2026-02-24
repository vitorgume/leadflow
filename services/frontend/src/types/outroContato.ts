export type TipoContato = 'PADRAO' | 'GERENTE' | 'CONSULTOR';

export interface OutroContato {
  id: number;
  nome: string;
  telefone: string;
  descricao: string;
  tipo_contato: TipoContato;
  usuario?: { id: string };
}

export interface OutroContatoCreateDTO {
  nome: string;
  telefone: string;
  descricao: string;
  tipo_contato: TipoContato;
  usuario: { id: string };
}

export interface OutroContatoUpdateDTO extends Partial<OutroContatoCreateDTO> {}

// Interface genérica para paginação do Spring Boot
export interface SpringPage<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface ResponseDto<T> {
  dado: T;
  erro?: {
    mensagens: string[];
  };
}