from dataclasses import dataclass

@dataclass()
class BaseConhecimentoUsuario:
    id = str
    id_usuario = str
    titulo = str
    conteudo = str