from dataclasses import dataclass
from setor import Setor

@dataclass()
class MensagemOutroSetor:
    id_usuario: str
    mensagem: str
    setores: list[Setor]
