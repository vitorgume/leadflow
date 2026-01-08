from dataclasses import dataclass

@dataclass
class PromptUsuario:
    id = str
    titulo = str
    prompt = str
    tipo = int