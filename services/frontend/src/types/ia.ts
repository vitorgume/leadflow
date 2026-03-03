export interface UsuarioDto {
  id: string;
}

export interface PromptDto {
  id?: string;
  usuario: UsuarioDto;
  titulo: string;
  prompt: string;
}

export interface BaseConhecimentoDto {
  id?: string;
  usuario: UsuarioDto;
  titulo: string;
  conteudo: string;
}

// Assumindo a estrutura do ResponseDto do seu backend
export interface ResponseDto<T> {
  data?: T; 
  // Caso o retorno no Spring Boot coloque o dado direto no body, ajustamos o service.
}
