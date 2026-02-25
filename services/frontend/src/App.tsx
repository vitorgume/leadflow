import { Routes, Route } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import { Vendedores } from './pages/Vendedores'; // Temporário, será criado a seguir
import OutrosContatos from './pages/OutrosContatos';
import ConfiguracaoIA from './pages/ConfiguracaoIA';
import CadastroUsuario from './pages/CadastroUsuario';

function App() {
  return (
    <div className="antialiased text-slate-900 bg-slate-50 min-h-screen">
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/vendedores" element={<Vendedores />} />
        <Route path="/outros-contatos" element={<OutrosContatos/>}/>
        <Route path="/configuracao-ia" element={<ConfiguracaoIA/>} />
        <Route path="/usuarios/cadastro" element={<CadastroUsuario/>} />
      </Routes>
    </div>
  );
}

export default App;
