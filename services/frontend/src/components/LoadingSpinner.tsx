import React from 'react';
import { Loader } from 'lucide-react';

const LoadingSpinner: React.FC = () => (
  <div className="flex items-center justify-center p-8 text-indigo-600">
    <Loader className="animate-spin" size={32} />
    <span className="ml-4 text-lg font-medium text-slate-600">Carregando...</span>
  </div>
);

export default LoadingSpinner;