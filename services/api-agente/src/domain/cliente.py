from dataclasses import dataclass
from typing import Dict


@dataclass()
class Cliente:
    id: str
    nome: str
    telefone: str
    atributos_qualificacao: Dict
    inativo: bool

