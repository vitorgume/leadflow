// src/types/auth.ts
export interface LoginDTO {
  email: string;
  senha: string;
}

// CORREÇÃO: O seu backend devolve 'id' e 'token' na raiz!
export interface LoginResponseDTO {
  token?: string;
  id?: string; 
}

export interface ResponseDto<T> {
  dado: T;
  erro?: {
    mensagens: string[];
  };
}
