import uuid

from src.infrastructure.entity.conversa_entity import ConversaEntity
from src.infrastructure.mapper.conversa_mapper import ConversaMapper
from src.domain.conversa import Conversa
from src.config.database import SessionLocal, Base
from src.infrastructure.exceptions.data_provider_exception import DataProviderException
from sqlalchemy.exc import SQLAlchemyError
import logging

logger = logging.getLogger(__name__)

class ConversaDataProvider:
    
    def __init__(self, conversa_mapper: ConversaMapper):
        self.conversa_mapper = conversa_mapper
    
    def salvar(self, conversa: Conversa):
        session = SessionLocal()
        conversa_entity = self.conversa_mapper.paraEntity(conversa)
        try:
            persisted_entity = session.merge(conversa_entity)
            session.commit()
            return self.conversa_mapper.paraDomain(persisted_entity)
        except Exception as e:
            session.rollback()
            logger.exception("Erro ao salvar conversa no banco de dados")
            raise DataProviderException("Erro ao salvar conversa")
        finally:
            session.close()

    def consulta_por_id(self, id: str) -> Conversa:
        session = SessionLocal()
        try:
            uuid_bytes = uuid.UUID(id).bytes
            entity = session.query(ConversaEntity).filter(ConversaEntity.id_conversa == uuid_bytes).first()
            if entity is None:
                return None
            return self.conversa_mapper.paraDomain(entity)
        except SQLAlchemyError as e:
            logger.exception("Erro ao consultar conversa no banco de dados %s", e)
            raise DataProviderException("Erro ao consultar conversa")
        finally:
            session.close()