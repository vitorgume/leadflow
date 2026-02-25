import React, { createContext, useContext, useState, type ReactNode } from 'react';
import api from '../services/api';

// Tipagem dos dados que queremos ter em todas as telas
interface User {
  id: string;
  nome: string;
  email: string;
}

interface AuthContextData {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  signIn: (token: string, user: User) => void;
  signOut: () => void;
}

// Criação do Contexto
const AuthContext = createContext<AuthContextData>({} as AuthContextData);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  // Estado GUARDADO EM MEMÓRIA (seguro contra XSS de leitura de disco)
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);

  const signIn = (newToken: string, loggedUser: User) => {
    setToken(newToken);
    setUser(loggedUser);
    
    // Injeta o token automaticamente no Axios para as próximas requisições!
    api.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;
  };

  const signOut = () => {
    setToken(null);
    setUser(null);
    delete api.defaults.headers.common['Authorization'];
  };

  return (
    <AuthContext.Provider value={{ user, token, isAuthenticated: !!token, signIn, signOut }}>
      {children}
    </AuthContext.Provider>
  );
};

// Hook customizado para facilitar o uso nas telas
export const useAuth = () => {
  return useContext(AuthContext);
};