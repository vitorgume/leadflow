import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { 
  LayoutDashboard, 
  Users, 
  Contact, // Ele voltou!
  Building2, // O novato na área!
  Settings, 
  Brain,
  LogOut,
  Menu,
  X,
  GitBranch
} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';

interface SidebarProps {
  activeItem?: string; 
}

const Sidebar: React.FC<SidebarProps> = () => {
  const [isOpen, setIsOpen] = useState(false);
  const { user, signOut } = useAuth();

  const menuItems = [
    { label: 'Menu Principal', icon: LayoutDashboard, path: '/dashboard' },
    { label: 'Vendedores', icon: Users, path: '/vendedores' },
    { label: 'Outros Contatos', icon: Contact, path: '/outros-contatos' }, // O original intacto
    { label: 'Outros Setores', icon: Building2, path: '/outros-setores' }, // A nova tela adicionada!
    { label: 'IA e Prompts', icon: Brain, path: '/configuracao-ia' }, 
    { label: 'Regras Distribuição', icon: GitBranch, path: '/regras-distribuicao' },
    { label: 'Configurações', icon: Settings, path: '/usuarios/configuracoes' }, 
  ];

  const toggleSidebar = () => setIsOpen(!isOpen);

  // Fallbacks de segurança
  const userName = user?.nome || 'Usuário';
  const userEmail = user?.email || 'Carregando...';
  const userInitials = userName.substring(0, 2).toUpperCase();

  return (
    <>
      {/* Mobile Header */}
      <div className="lg:hidden flex items-center justify-between p-4 bg-white border-b border-slate-200 sticky top-0 z-50">
        <div className="flex items-center gap-2">
          <img src="/logo.svg" alt="Leadflow Logo" className="h-8 w-auto" />
        </div>
        <button onClick={toggleSidebar} className="p-2 text-slate-500 hover:bg-slate-50 rounded-lg">
          {isOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </div>

      {/* Overlay para Mobile */}
      {isOpen && (
        <div 
          className="fixed inset-0 bg-slate-900/50 z-40 lg:hidden"
          onClick={() => setIsOpen(false)}
        />
      )}

      {/* Sidebar Content */}
      <aside className={`
        fixed inset-y-0 left-0 z-50 w-64 bg-white border-r border-slate-200 flex flex-col transition-transform duration-300 ease-in-out lg:translate-x-0
        ${isOpen ? 'translate-x-0' : '-translate-x-full'}
      `}>
        <div className="p-6 hidden lg:flex items-center gap-2">
          <img src="/logo.svg" alt="Leadflow Logo" className="h-10 w-auto" />
        </div>

        <nav className="flex-1 px-4 pb-4 space-y-1 overflow-y-auto mt-4 lg:mt-0">
          {menuItems.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) => `
                flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors
                ${isActive 
                  ? 'bg-blue-50 text-blue-700' 
                  : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'}
              `}
              onClick={() => setIsOpen(false)}
            >
              {({ isActive }) => (
                <>
                  <item.icon size={20} className={isActive ? 'text-blue-600' : 'text-slate-400'} />
                  {item.label}
                </>
              )}
            </NavLink>
          ))}
        </nav>

        {/* Footer - Profile/Logout Dinâmico */}
        <div className="p-4 border-t border-slate-100">
          <div className="flex items-center gap-3 px-3 py-2 mb-2">
            <div className="h-8 w-8 rounded-full bg-blue-100 flex items-center justify-center text-blue-700 font-bold text-xs border border-blue-200">
              {userInitials}
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-slate-900 truncate">{userName}</p>
              <p className="text-xs text-slate-500 truncate">{userEmail}</p>
            </div>
          </div>
          <button 
            onClick={signOut}
            className="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium text-rose-600 hover:bg-rose-50 transition-colors"
          >
            <LogOut size={20} />
            Sair da conta
          </button>
        </div>
      </aside>
    </>
  );
};

export default Sidebar;