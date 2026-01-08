import uuid

from src.config.database import SessionLocal
from src.infrastructure.entity.prompt_usuario_entity import PromptUsuarioEntity
from src.infrastructure.exceptions.data_provider_exception import DataProviderException
from src.infrastructure.mapper.prompt_usuario_mapper import PromptUsuarioMapper
import logging

logger = logging.getLogger(__name__)

class PromptUsuarioDataprovider:

    def __init__(self, prompt_usuario_mapper: PromptUsuarioMapper):
        self.prompt_usuario_mapper = prompt_usuario_mapper

    def consultar_prompt_usuario(self, id_usuario: str, tipo_prompt: int):
        session = SessionLocal()
        try:
            uuid_bytes = uuid.UUID(id_usuario).bytes
            entity = session.query(PromptUsuarioEntity).filter(PromptUsuarioEntity.id_cliente == uuid_bytes, PromptUsuarioEntity.tipo == tipo_prompt).first()
            return self.prompt_usuario_mapper.paraDomain(entity)
        except Exception as e:
            session.rollback()
            logger.exception("Erro ao consultar prompt do usuário %s", e)
            raise DataProviderException("Erro ao consultar prompt do usuário.")
        finally:
            session.close()