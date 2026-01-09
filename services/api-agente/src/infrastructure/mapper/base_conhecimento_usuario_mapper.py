from src.domain.base_conhecimento_usuario import BaseConhecimentoUsuario
from src.infrastructure.entity.base_conhecimento_usuario_entity import BaseConhecimentoUsuarioEntity
import uuid

class BaseConhecimentoUsuarioMapper:

    def paraDomain(self, base_conhecimento_usuario_entity: BaseConhecimentoUsuarioEntity) -> BaseConhecimentoUsuario:
        return BaseConhecimentoUsuario(
            id=str(uuid.UUID(base_conhecimento_usuario_entity.id)),
            id_usuario = str(uuid.UUID(base_conhecimento_usuario_entity.id_usuario)),
            titulo=base_conhecimento_usuario_entity.titulo,
            conteudo=base_conhecimento_usuario_entity.prompt
        )