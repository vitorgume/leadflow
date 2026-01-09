from dataclasses import dataclass

@dataclass()
class MensagemJson:
    id_usuario: str
    mensagem: str