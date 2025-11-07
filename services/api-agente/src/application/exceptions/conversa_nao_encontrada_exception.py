class ConversaNaoEncontradaException(Exception):
    def __init__(self, message: str = "Conversa não encontrada pelo seu id."):
        super().__init__(message)



