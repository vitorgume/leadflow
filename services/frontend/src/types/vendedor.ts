// src/types/vendedor.ts

export interface Vendedor {
  id: number;
  nome: string;
  telefone: string;
  inativo: boolean;
  id_vendedor_crm: number;
  padrao: boolean;
  usuario: { // Apenas para referência no frontend
    id: string;
    nome?: string; // Opcional, para exibição
  };
}

export interface VendedorCreateDTO {
  nome: string;
  telefone: string;
  inativo: boolean;
  id_vendedor_crm: number;
  padrao: boolean;
  usuario: {
    id: string;
  };
}

export interface VendedorUpdateDTO {
  nome: string;
  telefone: string;
  inativo: boolean;
  id_vendedor_crm: number;
  padrao: boolean;
}

export interface ErroDto {
  mensagens: string[];
}

export interface ResponseDto<D> {
  dado: D;
  erro: ErroDto;
}
