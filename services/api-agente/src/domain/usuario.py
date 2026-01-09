
from dataclasses import dataclass
from typing import Dict


@dataclass()
class Usuario:
    id = str
    nome = str
    telefone = str
    senha = str
    email = str
    telefone_conectado = str
    atributos_qualificacao = Dict