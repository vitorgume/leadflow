import { Routes, Route } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import { Vendedores } from './pages/Vendedores'; // Temporário, será criado a seguir

function App() {
  return (
    <div className="antialiased text-slate-900 bg-slate-50 min-h-screen">
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/vendedores" element={<Vendedores />} />
      </Routes>
    </div>
  );
}

export default App;
