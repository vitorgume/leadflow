import React from 'react';
import { AlertCircle } from 'lucide-react';

interface ErrorMessageProps {
  message: string;
}

const ErrorMessage: React.FC<ErrorMessageProps> = ({ message }) => (
  <div className="flex items-center justify-center p-8 bg-rose-50 border border-rose-200 rounded-xl">
    <AlertCircle className="text-rose-600" size={24} />
    <span className="ml-4 text-lg font-medium text-rose-700">{message}</span>
  </div>
);

export default ErrorMessage;
