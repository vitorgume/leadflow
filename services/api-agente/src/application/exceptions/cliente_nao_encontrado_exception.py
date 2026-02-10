class ClienteNaoEncontradoException(Exception):
    def __init__(self, message: str = "Cliente não encontrado."):
        super().__init__(message)