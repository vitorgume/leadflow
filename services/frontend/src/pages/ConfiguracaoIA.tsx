import { useState, useEffect } from 'react';
import Sidebar from '../components/Sidebar';
import { Brain, Bot, BookOpen, Save, CheckCircle2, Loader2, FileText, Trash2, AlertCircle } from 'lucide-react';
import { promptService, baseConhecimentoService } from '../services/iaService';
import type { PromptDto, BaseConhecimentoDto } from '../types/ia';
import DeleteConfirmationModal from '../components/DeleteConfirmationModal';
import { useAuth } from '../contexts/AuthContext';

export default function ConfiguracaoIA() {

    const { user } = useAuth();
    
    // --- Estados Prompt ---
    const defaultPrompt: PromptDto = { usuario: { id: user == null ? "" : user.id }, titulo: '', prompt: '' };
    const [prompt, setPrompt] = useState<PromptDto>(defaultPrompt);
    const [loadingPrompt, setLoadingPrompt] = useState(true);
    const [savingPrompt, setSavingPrompt] = useState(false);
    const [successPrompt, setSuccessPrompt] = useState(false);
    const [errorPrompt, setErrorPrompt] = useState<string | null>(null);

    // Controle do Modal de Exclusão do Prompt
    const [isDeletePromptModalOpen, setIsDeletePromptModalOpen] = useState(false);
    const [deletingPrompt, setDeletingPrompt] = useState(false);

    // --- Estados Base de Conhecimento ---
    const defaultBase: BaseConhecimentoDto = { usuario: { id: user == null ? "" : user.id }, titulo: '', conteudo: '' };
    const [base, setBase] = useState<BaseConhecimentoDto>(defaultBase);
    const [loadingBase, setLoadingBase] = useState(true);
    const [savingBase, setSavingBase] = useState(false);
    const [successBase, setSuccessBase] = useState(false);
    const [errorBase, setErrorBase] = useState<string | null>(null);

    // Controle do Modal de Exclusão da Base
    const [isDeleteBaseModalOpen, setIsDeleteBaseModalOpen] = useState(false);
    const [deletingBase, setDeletingBase] = useState(false);

    // --- Efeito Inicial (Carregar Dados) ---
    useEffect(() => {
        const fetchData = async () => {
            setErrorPrompt(null);
            setErrorBase(null);
            try {
                if (user) {
                    const [promptSalvo, baseSalva] = await Promise.all([
                        promptService.buscar(user.id),
                        baseConhecimentoService.buscar(user.id)
                    ]);

                    if (promptSalvo) setPrompt(promptSalvo);
                    if (baseSalva) setBase(baseSalva);
                }
            } catch (err: any) {
                // Se falhar a carga inicial, mostramos o erro nos dois cards
                const mensagem = err.message || "Erro ao carregar os dados da IA.";
                setErrorPrompt(mensagem);
                setErrorBase(mensagem);
            } finally {
                setLoadingPrompt(false);
                setLoadingBase(false);
            }
        };
        fetchData();
    }, [user]);

    // --- Handlers Prompt ---
    const handleSavePrompt = async () => {
        setSavingPrompt(true);
        setSuccessPrompt(false);
        setErrorPrompt(null);
        try {
            const result = await promptService.salvar(prompt);
            setPrompt(result);
            setSuccessPrompt(true);
            setTimeout(() => setSuccessPrompt(false), 3000);
        } catch (err: any) {
            setErrorPrompt(err.message || "Falha ao salvar o prompt.");
        } finally {
            setSavingPrompt(false);
        }
    };

    const executeDeletePrompt = async () => {
        if (!prompt.id) return;
        setDeletingPrompt(true);
        setErrorPrompt(null);
        try {
            await promptService.deletar(prompt.id);
            setPrompt(defaultPrompt); // Limpa o formulário
            setIsDeletePromptModalOpen(false); // Fecha o modal
        } catch (err: any) {
            setIsDeletePromptModalOpen(false); // Fecha o modal primeiro
            setErrorPrompt(err.message || "Falha ao excluir o prompt.");
        } finally {
            setDeletingPrompt(false);
        }
    };

    // --- Handlers Base de Conhecimento ---
    const handleSaveBase = async () => {
        setSavingBase(true);
        setSuccessBase(false);
        setErrorBase(null);
        try {
            const result = await baseConhecimentoService.salvar(base);
            setBase(result);
            setSuccessBase(true);
            setTimeout(() => setSuccessBase(false), 3000);
        } catch (err: any) {
            setErrorBase(err.message || "Falha ao salvar a base de conhecimento.");
        } finally {
            setSavingBase(false);
        }
    };

    const executeDeleteBase = async () => {
        if (!base.id) return;
        setDeletingBase(true);
        setErrorBase(null);
        try {
            await baseConhecimentoService.deletar(base.id);
            setBase(defaultBase); // Limpa o formulário
            setIsDeleteBaseModalOpen(false); // Fecha o modal
        } catch (err: any) {
            setIsDeleteBaseModalOpen(false); // Fecha o modal primeiro
            setErrorBase(err.message || "Falha ao excluir a base de conhecimento.");
        } finally {
            setDeletingBase(false);
        }
    };

    return (
        <div className="flex h-screen bg-slate-50">
            <Sidebar activeItem="IA e Prompts" />

            <main className="flex-1 overflow-auto p-4 lg:p-8">
                <div className="max-w-5xl mx-auto">

                    <div className="mb-8">
                        <div className="flex items-center gap-3 mb-2">
                            <div className="p-2 bg-blue-100 text-blue-600 rounded-lg">
                                <Brain size={24} />
                            </div>
                            <div>
                                <h1 className="text-2xl font-bold text-slate-900 leading-tight">Configuração de Inteligência Artificial</h1>
                                <p className="text-slate-500 text-sm mt-0.5 font-medium">
                                    Gerencie as instruções e os dados que orientam as respostas do seu agente. Suporta escrita em Markdown.
                                </p>
                            </div>
                        </div>
                    </div>

                    <div className="flex flex-col gap-8">

                        {/* CARD 1: PROMPT DO AGENTE */}
                        <div className="bg-white rounded-xl border border-slate-200 shadow-sm flex flex-col">
                            <div className="p-6 border-b border-slate-100 flex justify-between items-center bg-slate-50/50 rounded-t-xl">
                                <div className="flex items-center gap-2">
                                    <Bot size={20} className="text-blue-600" />
                                    <h2 className="text-lg font-semibold text-slate-900">Comportamento (Prompt)</h2>
                                </div>
                                {prompt.id && <span className="text-xs font-semibold px-2 py-1 bg-blue-100 text-blue-700 rounded-full">Modo Edição</span>}
                            </div>

                            <div className="p-6 flex-1 flex flex-col">
                                {loadingPrompt ? (
                                    <div className="flex-1 flex items-center justify-center min-h-[300px]">
                                        <Loader2 className="animate-spin text-blue-600" size={32} />
                                    </div>
                                ) : (
                                    <>
                                        {/* Card de Erro específico do Prompt */}
                                        {errorPrompt && (
                                            <div className="mb-6 p-4 bg-rose-50 border border-rose-200 rounded-lg flex items-start gap-3 text-rose-700 animate-in fade-in slide-in-from-top-2 duration-300">
                                                <AlertCircle className="shrink-0 mt-0.5" size={20} />
                                                <p className="text-sm font-medium">{errorPrompt}</p>
                                            </div>
                                        )}

                                        <div className="mb-4">
                                            <label className="block text-sm font-medium text-slate-700 mb-1">Título de Identificação</label>
                                            <input
                                                type="text"
                                                value={prompt.titulo}
                                                onChange={(e) => setPrompt({ ...prompt, titulo: e.target.value })}
                                                className="w-full px-4 py-2 bg-white border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-shadow text-slate-900"
                                                placeholder="Ex: Consultor de Vendas B2B"
                                            />
                                        </div>

                                        <div className="flex-1 flex flex-col">
                                            <div className="flex justify-between items-end mb-1">
                                                <label className="block text-sm font-medium text-slate-700">Instruções do Sistema (Markdown)</label>
                                                <span className="text-xs text-slate-400 flex items-center gap-1"><FileText size={12} /> Suporta Markdown</span>
                                            </div>
                                            <textarea
                                                value={prompt.prompt}
                                                onChange={(e) => setPrompt({ ...prompt, prompt: e.target.value })}
                                                className="w-full flex-1 min-h-[350px] p-4 bg-slate-50 border border-slate-300 rounded-lg text-sm font-mono text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-shadow resize-y"
                                                placeholder="# Contexto&#10;Você é um especialista em...&#10;&#10;## Regras&#10;- Seja educado&#10;- Sempre faça perguntas abertas"
                                            />
                                        </div>

                                        <div className="mt-6 flex items-center justify-between">
                                            <div className="text-sm">
                                                {successPrompt && <span className="text-emerald-600 flex items-center gap-1 font-medium"><CheckCircle2 size={16} /> Salvo com sucesso!</span>}
                                            </div>
                                            <div className="flex items-center gap-3">
                                                {prompt.id && (
                                                    <button
                                                        onClick={() => setIsDeletePromptModalOpen(true)}
                                                        disabled={deletingPrompt || savingPrompt}
                                                        className="flex items-center gap-2 px-4 py-2.5 bg-rose-50 text-rose-600 text-sm font-medium rounded-lg hover:bg-rose-100 transition-colors focus:ring-2 focus:ring-offset-2 focus:ring-rose-500 disabled:opacity-70"
                                                    >
                                                        <Trash2 size={18} />
                                                        Excluir
                                                    </button>
                                                )}
                                                <button
                                                    onClick={handleSavePrompt}
                                                    disabled={savingPrompt || !prompt.prompt.trim()}
                                                    className="flex items-center gap-2 px-5 py-2.5 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors focus:ring-2 focus:ring-offset-2 focus:ring-blue-600 disabled:opacity-70"
                                                >
                                                    {savingPrompt ? <Loader2 size={18} className="animate-spin" /> : <Save size={18} />}
                                                    Salvar Prompt
                                                </button>
                                            </div>
                                        </div>
                                    </>
                                )}
                            </div>
                        </div>

                        {/* CARD 2: BASE DE CONHECIMENTO */}
                        <div className="bg-white rounded-xl border border-slate-200 shadow-sm flex flex-col">
                            <div className="p-6 border-b border-slate-100 flex justify-between items-center bg-slate-50/50 rounded-t-xl">
                                <div className="flex items-center gap-2">
                                    <BookOpen size={20} className="text-emerald-600" />
                                    <h2 className="text-lg font-semibold text-slate-900">Base de Conhecimento</h2>
                                </div>
                                {base.id && <span className="text-xs font-semibold px-2 py-1 bg-emerald-100 text-emerald-700 rounded-full">Modo Edição</span>}
                            </div>

                            <div className="p-6 flex-1 flex flex-col">
                                {loadingBase ? (
                                    <div className="flex-1 flex items-center justify-center min-h-[300px]">
                                        <Loader2 className="animate-spin text-emerald-600" size={32} />
                                    </div>
                                ) : (
                                    <>
                                        {/* Card de Erro específico da Base de Conhecimento */}
                                        {errorBase && (
                                            <div className="mb-6 p-4 bg-rose-50 border border-rose-200 rounded-lg flex items-start gap-3 text-rose-700 animate-in fade-in slide-in-from-top-2 duration-300">
                                                <AlertCircle className="shrink-0 mt-0.5" size={20} />
                                                <p className="text-sm font-medium">{errorBase}</p>
                                            </div>
                                        )}

                                        <div className="mb-4">
                                            <label className="block text-sm font-medium text-slate-700 mb-1">Título do Documento</label>
                                            <input
                                                type="text"
                                                value={base.titulo}
                                                onChange={(e) => setBase({ ...base, titulo: e.target.value })}
                                                className="w-full px-4 py-2 bg-white border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-shadow text-slate-900"
                                                placeholder="Ex: Tabela de Preços e FAQs"
                                            />
                                        </div>

                                        <div className="flex-1 flex flex-col">
                                            <div className="flex justify-between items-end mb-1">
                                                <label className="block text-sm font-medium text-slate-700">Conteúdo (Markdown)</label>
                                                <span className="text-xs text-slate-400 flex items-center gap-1"><FileText size={12} /> Suporta Markdown</span>
                                            </div>
                                            <textarea
                                                value={base.conteudo}
                                                onChange={(e) => setBase({ ...base, conteudo: e.target.value })}
                                                className="w-full flex-1 min-h-[350px] p-4 bg-slate-50 border border-slate-300 rounded-lg text-sm font-mono text-slate-800 focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-shadow resize-y"
                                                placeholder="# Tabela de Preços&#10;- Plano Básico: R$99&#10;&#10;## FAQ&#10;**Q: Qual o prazo de entrega?**&#10;A: Até 5 dias úteis."
                                            />
                                        </div>

                                        <div className="mt-6 flex items-center justify-between">
                                            <div className="text-sm">
                                                {successBase && <span className="text-emerald-600 flex items-center gap-1 font-medium"><CheckCircle2 size={16} /> Salvo com sucesso!</span>}
                                            </div>
                                            <div className="flex items-center gap-3">
                                                {base.id && (
                                                    <button
                                                        onClick={() => setIsDeleteBaseModalOpen(true)}
                                                        disabled={deletingBase || savingBase}
                                                        className="flex items-center gap-2 px-4 py-2.5 bg-rose-50 text-rose-600 text-sm font-medium rounded-lg hover:bg-rose-100 transition-colors focus:ring-2 focus:ring-offset-2 focus:ring-rose-500 disabled:opacity-70"
                                                    >
                                                        <Trash2 size={18} />
                                                        Excluir
                                                    </button>
                                                )}
                                                <button
                                                    onClick={handleSaveBase}
                                                    disabled={savingBase || !base.conteudo.trim()}
                                                    className="flex items-center gap-2 px-5 py-2.5 bg-emerald-600 text-white text-sm font-medium rounded-lg hover:bg-emerald-700 transition-colors focus:ring-2 focus:ring-offset-2 focus:ring-emerald-600 disabled:opacity-70"
                                                >
                                                    {savingBase ? <Loader2 size={18} className="animate-spin" /> : <Save size={18} />}
                                                    Salvar Base
                                                </button>
                                            </div>
                                        </div>
                                    </>
                                )}
                            </div>
                        </div>

                    </div>
                </div>
            </main>

            {/* Renderização dos Modais de Exclusão */}
            <DeleteConfirmationModal
                isOpen={isDeletePromptModalOpen}
                onClose={() => setIsDeletePromptModalOpen(false)}
                onConfirm={executeDeletePrompt}
                isDeleting={deletingPrompt}
                title="Excluir Prompt"
                message="Tem certeza que deseja excluir o Prompt do agente? O comportamento voltará ao padrão do sistema."
            />

            <DeleteConfirmationModal
                isOpen={isDeleteBaseModalOpen}
                onClose={() => setIsDeleteBaseModalOpen(false)}
                onConfirm={executeDeleteBase}
                isDeleting={deletingBase}
                title="Excluir Base de Conhecimento"
                message="Tem certeza que deseja excluir a Base de Conhecimento? O agente não terá mais acesso a esses dados."
            />
        </div>
    );
}
