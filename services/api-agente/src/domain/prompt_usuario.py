from dataclasses import dataclass

@dataclass
class PromptUsuario:
    id: str
    id_usuario: str
    titulo: str
    prompt: str