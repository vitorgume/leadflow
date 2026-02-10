

class BaseConhecimentoUsuarioNaoEncontradoException(Exception):
    def __init__(self, message: str = "Base de conhecimento não encontrada"):
        super().__init__(message)