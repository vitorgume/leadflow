import React, { createContext, useContext, useState, type ReactNode } from 'react';

// Tipagem dos dados que queremos ter em todas as telas
interface User {
  id: string;
  nome: string;
  email: string;
}

interface AuthContextData {
  user: User | null;
  isAuthenticated: boolean;
  signIn: (token: string, user: User) => void;
  signOut: () => void;
}

// Criação do Contexto
const AuthContext = createContext<AuthContextData>({} as AuthContextData);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  
  // LAZY INITIALIZATION: O React roda essa função antes de renderizar a tela pela primeira vez
  const [user, setUser] = useState<User | null>(() => {
    const storedUser = localStorage.getItem('@Leadflow:user');
    
    if (storedUser) {
      return JSON.parse(storedUser); // Transforma a string salva de volta em Objeto
    }
    
    return null;
  });

  const signIn = (newToken: string, loggedUser: User) => {
    setUser(loggedUser);
    
    // Salva APENAS os dados do usuário no LocalStorage (O token continua seguro no Cookie!)
    localStorage.setItem('@Leadflow:user', JSON.stringify(loggedUser));
  };

  const signOut = () => {
    setUser(null);
    localStorage.removeItem('@Leadflow:user');
    // Força o redirecionamento para a tela de login
    window.location.href = '/'; 
  };

  return (
    // Agora o isAuthenticated confia na existência do user, e não mais na variável token
    <AuthContext.Provider value={{ user, isAuthenticated: !!user, signIn, signOut }}>
      {children}
    </AuthContext.Provider>
  );
};

// Hook customizado para facilitar o uso nas telas
export const useAuth = () => {
  return useContext(AuthContext);
};