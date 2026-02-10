import uuid

from src.config.database import SessionLocal
from src.infrastructure.entity import ClienteEntity
from src.infrastructure.exceptions.data_provider_exception import DataProviderException
from src.infrastructure.mapper.cliente_mapper import ClienteMapper
import logging

logger = logging.getLogger(__name__)

class ClienteDataProvider:

    def __init__(self, cliente_mapper: ClienteMapper):
        self.cliente_mappper = cliente_mapper

    def consultar_por_id(self, id: str):
        print("Id do cliente: " + id)
        session = SessionLocal()
        try:
            uuid_bytes = uuid.UUID(id).bytes
            entity = session.query(ClienteEntity).filter(ClienteEntity.id_cliente == uuid_bytes).first()
            return self.cliente_mappper.paraDomain(entity)
        except Exception as e:
            session.rollback()
            logger.exception("Erro ao consultar cliente por id. %s", e)
            raise DataProviderException("Erro ao consultar cliente por id.")
        finally:
            session.close()

