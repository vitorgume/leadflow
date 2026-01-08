import uuid
from src.infrastructure.entity.prompt_usuario_entity import PromptUsuarioEntity
from src.domain.prompt_usuario import PromptUsuario


class PromptUsuarioMapper:

    def paraDomain(self, prompt_usuario_entity: PromptUsuarioEntity) -> PromptUsuario:
        return PromptUsuario(
            id=str(uuid.UUID(prompt_usuario_entity.id_cliente)),
            titulo=prompt_usuario_entity.titulo,
            prompt=prompt_usuario_entity.prompt,
            tipo=prompt_usuario_entity.tipo
        )