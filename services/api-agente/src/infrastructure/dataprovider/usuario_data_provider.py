import uuid

from src.config.database import SessionLocal
from src.infrastructure.entity.usuario_entity import UsuarioEntity
from src.infrastructure.exceptions.data_provider_exception import DataProviderException
from src.infrastructure.mapper.usuario_mapper import UsuarioMapper
import logging

logger = logging.getLogger(__name__)

class UsuarioDataProvider:

    def __init__(self, usuario_mapper: UsuarioMapper):
        self.usuario_mapper = usuario_mapper

    def consultar_por_id(self, id: str):
        session = SessionLocal()

        try:
            uuid_bytes = uuid.UUID(id).bytes
            entity = session.query(UsuarioEntity).filter(UsuarioEntity.id == uuid_bytes).first()
            return self.usuario_mapper.para_domain(entity)
        except Exception as e:
            session.rollback()
            logger.exception("Erro ao consultar usuário pelo id. %s", e)
            raise DataProviderException("Erro ao consultar usuário pelo id.")
        finally:
            session.close()