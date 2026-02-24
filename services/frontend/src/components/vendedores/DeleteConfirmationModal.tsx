import React from 'react';
import { AlertCircle, Loader } from 'lucide-react';

interface DeleteConfirmationModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => Promise<void> | void;
  isDeleting: boolean;
  title?: string;
  message?: string;
}

const DeleteConfirmationModal: React.FC<DeleteConfirmationModalProps> = ({ 
  isOpen, 
  onClose, 
  onConfirm, 
  isDeleting,
  title = "Excluir Registro",
  message = "Tem certeza que deseja excluir? Esta ação não pode ser desfeita."
}) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-slate-900/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-xl shadow-md w-full max-w-md p-6 font-sans">
        <div className="flex items-center gap-3 text-rose-600 mb-4">
          <AlertCircle size={24} />
          <h2 className="text-xl font-bold text-slate-900">{title}</h2>
        </div>
        <p className="text-slate-600 mb-6">{message}</p>
        <div className="flex justify-end gap-3">
          <button
            type="button"
            onClick={onClose}
            className="flex items-center justify-center gap-2 bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 px-4 py-2 rounded-lg font-medium transition-colors"
            disabled={isDeleting}
          >
            Cancelar
          </button>
          <button
            type="button"
            onClick={onConfirm}
            className="flex items-center justify-center gap-2 bg-rose-600 hover:bg-rose-700 text-white px-4 py-2 rounded-lg font-medium transition-colors shadow-sm"
            disabled={isDeleting}
          >
            {isDeleting ? (
              <>
                <Loader size={20} className="animate-spin" /> Excluindo...
              </>
            ) : (
              'Sim, Excluir'
            )}
          </button>
        </div>
      </div>
    </div>
  );
};

export default DeleteConfirmationModal;