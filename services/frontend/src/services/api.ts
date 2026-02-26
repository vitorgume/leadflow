import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true // Mantém o envio do nosso cookie seguro
});

// --- INTERCEPTOR DE RESPOSTAS ---
// Ele captura tudo que volta do backend ANTES de chegar no seu componente
api.interceptors.response.use(
  (response) => {
    // Se a requisição deu 200 OK, apenas passa adiante
    return response;
  },
  (error) => {
    // 1. O backend respondeu com erro (caiu no seu HandlerMiddleware)
    if (error.response && error.response.data && error.response.data.erro) {
      const mensagens = error.response.data.erro.mensagens;
      
      const mensagemAmigavel = mensagens.join(', ');
      return Promise.reject(new Error(mensagemAmigavel));
    }


    if (error.code === 'ERR_NETWORK') {
      return Promise.reject(new Error('Não foi possível conectar ao servidor. Verifique sua internet ou se o sistema está online.'));
    }

    return Promise.reject(new Error('Ocorreu um erro inesperado. Tente novamente mais tarde.'));
  }
);

export default api;
