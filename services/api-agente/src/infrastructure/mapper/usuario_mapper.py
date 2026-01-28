import uuid

from src.application.security.crypto_util import CryptoUtil
from src.domain.usuario import Usuario
from src.infrastructure.entity.usuario_entity import UsuarioEntity


class UsuarioMapper:

    def __init__(self, crypto_util: CryptoUtil):
        self.crypto_util = crypto_util


    def para_domain(self, usuario_entity: UsuarioEntity) -> Usuario:
        return Usuario(
            id=str(uuid.UUID(bytes=usuario_entity.id)),
            nome=usuario_entity.nome,
            telefone=usuario_entity.telefone,
            senha=usuario_entity.senha,
            email=usuario_entity.email,
            telefone_conectado=usuario_entity.telefone_conectado,
            atributos_qualificacao=usuario_entity.atributos_qualificacao,
            agente_api_key=self.crypto_util.decrypt(usuario_entity.agente_api_key)
        )