import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  LayoutDashboard, 
  Users, 
  UserCog, 
  Contact, 
  Settings, 
  Brain,
  LogOut,
  Menu,
  X
} from 'lucide-react';

interface SidebarProps {
  activeItem?: string; // This prop might become redundant with NavLink's isActive
}

const Sidebar: React.FC<SidebarProps> = ({ activeItem = 'Menu Principal' }) => {
  const [isOpen, setIsOpen] = React.useState(false);

  const menuItems = [
    { label: 'Menu Principal', icon: LayoutDashboard, path: '/' },
    { label: 'Vendedores', icon: Users, path: '/vendedores' },
    { label: 'Configuração Vendedor', icon: UserCog, path: '/configuracao-vendedor' }, // Placeholder path
    { label: 'Outros Contatos', icon: Contact, path: '/outros-contatos' },
    { label: 'IA e Prompts', icon: Brain, path: '/configuracao-ia' }, // Placeholder path
    { label: 'Configurações', icon: Settings, path: '/configuracoes' }, // Placeholder path
  ];

  const toggleSidebar = () => setIsOpen(!isOpen);

  return (
    <>
      {/* Mobile Header */}
      <div className="lg:hidden flex items-center justify-between p-4 bg-white border-b border-slate-200 sticky top-0 z-50">
        <div className="flex items-center gap-2">
          <div className="h-8 w-8 bg-indigo-600 rounded-lg flex items-center justify-center text-white font-bold">
            LF
          </div>
          <span className="font-semibold text-slate-900">Leadflow</span>
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
        fixed inset-y-0 left-0 z-50 w-64 bg-white border-r border-slate-200 flex flex-col transition-transform duration-300 ease-in-out lg:static lg:translate-x-0
        ${isOpen ? 'translate-x-0' : '-translate-x-full'}
      `}>
        <div className="p-6 hidden lg:flex items-center gap-2">
          <div className="h-8 w-8 bg-indigo-600 rounded-lg flex items-center justify-center text-white font-bold">
            LF
          </div>
          <span className="text-xl font-bold text-slate-900 tracking-tight">Leadflow</span>
        </div>

        <nav className="flex-1 px-4 pb-4 space-y-1 overflow-y-auto mt-4 lg:mt-0">
          {menuItems.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) => `
                flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors
                ${isActive 
                  ? 'bg-indigo-50 text-indigo-700' 
                  : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'}
              `}
              onClick={() => setIsOpen(false)}
            >
              {({ isActive }) => (
                <>
                  <item.icon size={20} className={isActive ? 'text-indigo-600' : 'text-slate-400'} />
                  {item.label}
                </>
              )}
            </NavLink>
          ))}
        </nav>

        {/* Footer - Profile/Logout */}
        <div className="p-4 border-t border-slate-100">
          <div className="flex items-center gap-3 px-3 py-2 mb-2">
            <div className="h-8 w-8 rounded-full bg-slate-100 flex items-center justify-center text-slate-700 font-bold text-xs border border-slate-200">
              VM
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-slate-900 truncate">Vitor M.</p>
              <p className="text-xs text-slate-500 truncate">Admin</p>
            </div>
          </div>
          <button className="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium text-rose-600 hover:bg-rose-50 transition-colors">
            <LogOut size={20} />
            Sair da conta
          </button>
        </div>
      </aside>
    </>
  );
};

export default Sidebar;
