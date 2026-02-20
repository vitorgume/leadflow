import React from 'react';
import type { TooltipContentProps } from 'recharts';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, AreaChart, Area
} from 'recharts';
import type { ChartPoint } from '../types/dashboard';

interface ChartsSectionProps {
  dailyData: ChartPoint[];
  hourlyData: ChartPoint[];
}

const CustomTooltip = ({ active, payload, label }: TooltipContentProps<number, string>) => {
  if (active && payload && payload.length) {
    return (
      <div className="bg-white p-3 border border-slate-200 shadow-lg rounded-lg">
        <p className="text-xs font-bold text-slate-500 uppercase mb-1">{label}</p>
        <p className="text-sm font-semibold text-indigo-600">
          {payload[0].value} Contatos
        </p>
      </div>
    );
  }
  return null;
};

const ChartsSection: React.FC<ChartsSectionProps> = ({ dailyData, hourlyData }) => {
  return (
    <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
        <h4 className="text-lg font-bold text-slate-900 mb-6">Contatos por Dia</h4>
        <div className="w-full" style={{ minHeight: '300px' }}>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={dailyData}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
              <XAxis dataKey="name" axisLine={false} tickLine={false} tick={{ fill: '#64748b', fontSize: 12 }} dy={10} />
              <YAxis axisLine={false} tickLine={false} tick={{ fill: '#64748b', fontSize: 12 }} />

              <Tooltip content={CustomTooltip} cursor={{ fill: '#f1f5f9' }} />
              <Bar dataKey="value" fill="#4f46e5" radius={[4, 4, 0, 0]} barSize={40} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
        <h4 className="text-lg font-bold text-slate-900 mb-6">Volume por Horário</h4>
        <div className="w-full" style={{ minHeight: '300px' }}>
          <ResponsiveContainer width="100%" height={300}>
            <AreaChart data={hourlyData}>
              <defs>
                <linearGradient id="colorValue" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#4f46e5" stopOpacity={0.1} />
                  <stop offset="95%" stopColor="#4f46e5" stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
              <XAxis dataKey="name" axisLine={false} tickLine={false} tick={{ fill: '#64748b', fontSize: 12 }} dy={10} />
              <YAxis axisLine={false} tickLine={false} tick={{ fill: '#64748b', fontSize: 12 }} />
              <Tooltip content={CustomTooltip} />
              <Area type="monotone" dataKey="value" stroke="#4f46e5" strokeWidth={3} fillOpacity={1} fill="url(#colorValue)" />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
};

export default ChartsSection;