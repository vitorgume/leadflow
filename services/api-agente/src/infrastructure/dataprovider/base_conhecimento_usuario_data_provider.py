import uuid

from src.config.database import SessionLocal
from src.infrastructure.exceptions.data_provider_exception import DataProviderException
from src.infrastructure.mapper.base_conhecimento_usuario_mapper import BaseConhecimentoUsuarioMapper

from src.infrastructure.entity.base_conhecimento_usuario_entity import BaseConhecimentoUsuarioEntity
import logging

logger = logging.getLogger(__name__)

class BaseConhecimentoUsuarioDataProvider:

    def __init__(self, base_conhecimento_usuario_mapper: BaseConhecimentoUsuarioMapper):
        self.base_conhecimento_usuario_mapper = base_conhecimento_usuario_mapper

    def consultar_base_conhecimento_usuario(self, id_usuario: str):
        session = SessionLocal()
        try:
            uuid_bytes = uuid.UUID(id_usuario).bytes
            entity = session.query(BaseConhecimentoUsuarioEntity).filter(BaseConhecimentoUsuarioEntity.id_usuario == uuid_bytes).first()
            return self.base_conhecimento_usuario_mapper.paraDomain(entity)
        except Exception as e:
            session.rollback()
            logger.exception("Erro ao consultar base de conhecimento pelo usuário. %s", e)
            raise DataProviderException("Erro ao consultar base de conhecimento pelo usuário.")
        finally:
            session.close()