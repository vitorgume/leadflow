export interface MembroDTO {
  id?: string;
  nome: string;
  telefone: string;
  usuario?: {
    id: string;
  };
}