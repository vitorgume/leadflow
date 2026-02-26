import React, { useState } from 'react';
import { User, Phone, Mail, Lock, Smartphone, Loader, AlertCircle, CheckCircle2 } from 'lucide-react';
import { useNavigate } from 'react-router-dom'; // Importando o hook de navegação
import type { UsuarioCreateDTO } from '../types/usuario';
import { cadastrarUsuario } from '../services/usuarioService';

export default function CadastroUsuario() {
  const navigate = useNavigate(); // Instanciando o hook

  const [formData, setFormData] = useState<UsuarioCreateDTO>({
    nome: '',
    email: '',
    senha: '',
    telefone: '',
    telefone_conectado: ''
  });

  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<boolean>(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);
    setSuccess(false);

    try {
      await cadastrarUsuario(formData);
      setSuccess(true);
      
      // Limpa o formulário
      setFormData({
        nome: '',
        email: '',
        senha: '',
        telefone: '',
        telefone_conectado: ''
      });

      // Redireciona para a tela de login após 2 segundos
      setTimeout(() => {
        navigate('/'); // Assumindo que a raiz '/' é a sua tela de login
      }, 2000);

    } catch (err: any) {
      setError(err.message || 'Ocorreu um erro ao cadastrar o usuário.');
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
            <User size={32} />
          </div>
          <h1 className="text-2xl font-bold text-slate-900">Criar Nova Conta</h1>
          <p className="text-slate-500 text-sm mt-2 font-medium">Preencha os dados básicos para iniciar no Leadflow.</p>
        </div>

        {/* Card do Formulário */}
        <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6 sm:p-8">
          
          {error && (
            <div className="mb-6 p-4 bg-rose-50 border border-rose-200 rounded-lg flex items-start gap-3 text-rose-700">
              <AlertCircle className="shrink-0 mt-0.5" size={20} />
              <p className="text-sm font-medium">{error}</p>
            </div>
          )}

          {success && (
            <div className="mb-6 p-4 bg-emerald-50 border border-emerald-200 rounded-lg flex items-start gap-3 text-emerald-700">
              <CheckCircle2 className="shrink-0 mt-0.5" size={20} />
              <p className="text-sm font-medium">Usuário cadastrado com sucesso! Redirecionando para o login...</p>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Nome Completo</label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                  <User size={18} />
                </div>
                <input
                  type="text"
                  name="nome"
                  required
                  value={formData.nome}
                  onChange={handleChange}
                  placeholder="Ex: João da Silva"
                  className="w-full pl-10 pr-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all text-sm"
                />
              </div>
            </div>

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
              <label className="block text-sm font-medium text-slate-700 mb-1">Telefone (Contato)</label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                  <Phone size={18} />
                </div>
                <input
                  type="text"
                  name="telefone"
                  required
                  value={formData.telefone}
                  onChange={handleChange}
                  placeholder="5511999999999"
                  className="w-full pl-10 pr-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all text-sm"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Telefone Conectado (WhatsApp)</label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                  <Smartphone size={18} />
                </div>
                <input
                  type="text"
                  name="telefone_conectado"
                  required
                  value={formData.telefone_conectado}
                  onChange={handleChange}
                  placeholder="5511888888888"
                  className="w-full pl-10 pr-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all text-sm"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Senha</label>
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
              disabled={isLoading || success} // Desabilita também se já estiver redirecionando
              className="w-full flex items-center justify-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2.5 rounded-lg font-medium transition-colors shadow-sm disabled:opacity-70 mt-6"
            >
              {isLoading ? (
                <Loader size={20} className="animate-spin" />
              ) : (
                'Cadastrar Usuário'
              )}
            </button>
          </form>

        </div>
        
        <footer className="mt-8 text-center text-slate-400 text-xs">
          © 2026 Leadflow System • Todos os direitos reservados.
        </footer>
      </div>
    </div>
  );
}