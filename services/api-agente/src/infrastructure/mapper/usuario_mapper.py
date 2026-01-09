import uuid

from src.domain.usuario import Usuario
from src.infrastructure.entity.usuario_entity import UsuarioEntity


class UsuarioMapper:

    def para_domain(self, usuario_entity: UsuarioEntity) -> Usuario:
        return Usuario(
            id=str(uuid.UUID(usuario_entity.id)),
            nome=usuario_entity.nome,
            telefone=usuario_entity.telefone,
            senha=usuario_entity.senha,
            email=usuario_entity.email,
            telefone_concectado=usuario_entity.telefone_conectado,
            atributos_qualificacao=usuario_entity.atributos_qualificacao
        )