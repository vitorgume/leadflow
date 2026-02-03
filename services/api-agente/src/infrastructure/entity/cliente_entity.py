from sqlalchemy import Column, String, Boolean, SmallInteger
from sqlalchemy.dialects.mysql import BINARY, JSON
from src.config.base_class import Base

class ClienteEntity(Base):
    __tablename__ = "clientes"

    id_cliente = Column(BINARY(16), primary_key=True, index=True)
    nome = Column(String(255), nullable=True)
    telefone = Column(String(255), nullable=True)
    atributos_qualificacao = Column(JSON, nullable=True)
    inativo = Column(Boolean, nullable=False)
    usuario_id = Column(BINARY(16), nullable=False)
