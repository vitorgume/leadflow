import React from 'react';

interface KPICardProps {
  label: string;
  value: string | number;
  icon: React.ElementType;
  colorClass?: string;
  bgClass?: string;
}

const KPICard: React.FC<KPICardProps> = ({ 
  label, 
  value, 
  icon: Icon, 
  colorClass = 'text-blue-600', 
  bgClass = 'bg-blue-50' 
}) => {
  return (
    // Adicionado gap-4 para garantir respiro entre texto e ícone
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6 flex items-start justify-between gap-4">
      
      {/* Adicionado min-w-0 para permitir que o flexbox gerencie melhor o espaço */}
      <div className="min-w-0 flex-1">
        {/* Adicionado truncate para o título não forçar a largura e quebrar o layout em telas menores */}
        <p className="text-sm font-medium text-slate-500 uppercase tracking-wider truncate" title={label}>
          {label}
        </p>
        {/* Adicionado break-all para o valor, garantindo que números gigantes se adaptem (como a taxa de 411764705882352% do print anterior) */}
        <h3 className="text-3xl font-bold text-slate-900 mt-2 break-all">{value}</h3>
      </div>
      
      {/* Adicionado shrink-0 para blindar o ícone e impedir que ele seja esmagado pelo texto */}
      <div className={`shrink-0 p-4 rounded-xl ${bgClass} border border-opacity-10 shadow-sm transition-transform hover:scale-110`}>
        <Icon className={`h-8 w-8 ${colorClass}`} />
      </div>
      
    </div>
  );
};

export default KPICard;