class PromptUsuarioNaoEncontradoException(Exception):
    def __init__(self, message: str = "Prompt do usuário não encontrado no id e tipo de prompt especificado"):
        super().__init__(message)