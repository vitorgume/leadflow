export interface UsuarioCreateDTO {
  nome: string;
  telefone: string;
  senha: string;
  email: string;
  // Mapeado com snake_case para bater com o @JsonProperty do Java
  telefone_conectado: string; 
}

export interface Usuario {
  id: string;
  nome: string;
  telefone: string;
  email: string;
  telefone_conectado: string;
}

export interface ResponseDto<T> {
  dado: T;
  erro?: {
    mensagens: string[];
  };
}
