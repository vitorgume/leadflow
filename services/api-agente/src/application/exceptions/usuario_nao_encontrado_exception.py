class UsuarioNaoEncontradoException(Exception):

    def __init__(self, message: str = "Usuario não encontrado."):
        super().__init__(message)