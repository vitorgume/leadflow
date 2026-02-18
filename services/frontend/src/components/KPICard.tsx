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
  colorClass = 'text-indigo-600', 
  bgClass = 'bg-indigo-50' 
}) => {
  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6 flex items-start justify-between">
      <div>
        <p className="text-sm font-medium text-slate-500 uppercase tracking-wider">{label}</p>
        <h3 className="text-3xl font-bold text-slate-900 mt-2">{value}</h3>
      </div>
      <div className={`p-4 rounded-xl ${bgClass} border border-opacity-10 shadow-sm transition-transform hover:scale-110`}>
        <Icon className={`h-8 w-8 ${colorClass}`} />
      </div>
    </div>
  );
};

export default KPICard;
