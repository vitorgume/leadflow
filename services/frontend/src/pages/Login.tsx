import React, { useState } from 'react';
import { Mail, Lock, LogIn, Loader, AlertCircle } from 'lucide-react';
import { useNavigate } from 'react-router-dom'; // Importação do hook de navegação
import type { LoginDTO } from '../types/auth';
import { login } from '../services/authService';
import { useAuth } from '../contexts/AuthContext'; // Importação do nosso contexto

export default function Login() {
  const [formData, setFormData] = useState<LoginDTO>({
    email: '',
    senha: ''
  });

  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Instanciando os hooks
  const { signIn } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);

    try {
      // O response agora sabe que tem a propriedade .id
      const response = await login(formData);
      
      // Pegamos o ID diretamente da raiz da resposta do seu backend
      const userId = response.id || '';
      
      // Salvamos na memória (Context API)
      signIn(response.token || '', { 
        id: userId, 
        nome: 'Usuário', // Como o Login do backend não devolve o nome, deixamos um fallback
        email: formData.email 
      });
      
      // Redireciona para o dashboard
      navigate('/dashboard');
    } catch (err: any) {
      setError(err.message || 'Credenciais inválidas. Tente novamente.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center p-4 font-sans text-slate-900">
      
      <div className="w-full max-w-md">
        {/* Cabeçalho */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-indigo-600 text-white shadow-md mb-4">
            <LogIn size={32} />
          </div>
          <h1 className="text-2xl font-bold text-slate-900">Bem-vindo de volta!</h1>
          <p className="text-slate-500 text-sm mt-2 font-medium">Faça login para acessar o Leadflow.</p>
        </div>

        {/* Card do Formulário */}
        <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6 sm:p-8">
          
          {error && (
            <div className="mb-6 p-4 bg-rose-50 border border-rose-200 rounded-lg flex items-start gap-3 text-rose-700">
              <AlertCircle className="shrink-0 mt-0.5" size={20} />
              <p className="text-sm font-medium">{error}</p>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">E-mail</label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                  <Mail size={18} />
                </div>
                <input
                  type="email"
                  name="email"
                  required
                  value={formData.email}
                  onChange={handleChange}
                  placeholder="joao@empresa.com"
                  className="w-full pl-10 pr-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all text-sm"
                />
              </div>
            </div>

            <div>
              <div className="flex items-center justify-between mb-1">
                <label className="block text-sm font-medium text-slate-700">Senha</label>
              </div>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                  <Lock size={18} />
                </div>
                <input
                  type="password"
                  name="senha"
                  required
                  value={formData.senha}
                  onChange={handleChange}
                  placeholder="••••••••"
                  className="w-full pl-10 pr-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all text-sm"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={isLoading || !formData.email || !formData.senha}
              className="w-full flex items-center justify-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2.5 rounded-lg font-medium transition-colors shadow-sm disabled:opacity-70 mt-6"
            >
              {isLoading ? (
                <Loader size={20} className="animate-spin" />
              ) : (
                'Entrar'
              )}
            </button>
          </form>

          <div className="mt-8 text-center text-sm text-slate-500">
            Ainda não tem uma conta?{' '}
            <a href="/cadastro" className="font-medium text-indigo-600 hover:text-indigo-700 transition-colors">
              Cadastre-se aqui
            </a>
          </div>

        </div>
        
        <footer className="mt-8 text-center text-slate-400 text-xs">
          © 2026 Leadflow System • Todos os direitos reservados.
        </footer>
      </div>
    </div>
  );
}