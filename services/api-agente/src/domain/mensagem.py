from dataclasses import dataclass


@dataclass
class Mensagem:
    cliente_id: str
    conversa_id: str
    message: str