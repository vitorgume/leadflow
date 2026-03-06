export interface MembroDTO {
  id?: string;
  nome: string;
  telefone: string;
  usuario?: {
    id: string;
  };
}

export interface SetorDTO {
  id?: string;
  nome: string;
  descricao?: string;
  membros?: MembroDTO[]; 
  usuario?: {
    id: string;
  };
}