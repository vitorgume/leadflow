import React from 'react';
import { 
  LayoutDashboard, 
  Users, 
  UserCog, 
  Contact, 
  Settings, 
  LogOut,
  Menu,
  X
} from 'lucide-react';

interface SidebarProps {
  activeItem?: string;
}

const Sidebar: React.FC<SidebarProps> = ({ activeItem = 'Menu Principal' }) => {
  const [isOpen, setIsOpen] = React.useState(false);

  const menuItems = [
    { label: 'Menu Principal', icon: LayoutDashboard },
    { label: 'Vendedores', icon: Users },
    { label: 'Configuração Vendedor', icon: UserCog },
    { label: 'Outros Contatos', icon: Contact },
    { label: 'Configurações', icon: Settings },
  ];

  const toggleSidebar = () => setIsOpen(!isOpen);

  return (
    <>
      {/* Mobile Header */}
      <div className="lg:hidden flex items-center justify-between p-4 bg-white border-b border-slate-200 sticky top-0 z-50">
        <div className="flex items-center gap-2">
          <div className="bg-indigo-600 p-1.5 rounded-lg">
            <LayoutDashboard className="h-5 w-5 text-white" />
          </div>
          <span className="font-bold text-lg text-slate-900">Leadflow</span>
        </div>
        <button onClick={toggleSidebar} className="p-2 text-slate-600">
          {isOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </div>

      {/* Sidebar Overlay */}
      {isOpen && (
        <div 
          className="fixed inset-0 bg-slate-900/50 z-40 lg:hidden" 
          onClick={toggleSidebar}
        />
      )}

      {/* Sidebar Content */}
      <aside className={`
        fixed top-0 left-0 bottom-0 z-50 w-64 bg-white border-r border-slate-200 transition-transform duration-300 ease-in-out
        lg:translate-x-0 ${isOpen ? 'translate-x-0' : '-translate-x-full'}
      `}>
        <div className="flex flex-col h-full">
          {/* Logo */}
          <div className="p-6 hidden lg:flex items-center gap-2">
            <div className="bg-indigo-600 p-1.5 rounded-lg">
              <LayoutDashboard className="h-6 w-6 text-white" />
            </div>
            <span className="font-bold text-xl text-slate-900">Leadflow</span>
          </div>

          {/* Menu Items */}
          <nav className="flex-1 px-4 py-4 space-y-1">
            {menuItems.map((item) => (
              <button
                key={item.label}
                className={`
                  w-full flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors
                  ${activeItem === item.label 
                    ? 'bg-indigo-50 text-indigo-700' 
                    : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'}
                `}
              >
                <item.icon size={20} className={activeItem === item.label ? 'text-indigo-600' : ''} />
                {item.label}
              </button>
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
              Sair
            </button>
          </div>
        </div>
      </aside>
    </>
  );
};

export default Sidebar;
