
from sqlalchemy import Column, String, BINARY, TEXT, INTEGER

from src.config.database import Base


class PromptUsuarioEntity(Base):
    __tablename__ = "clientes"

    id_cliente = Column(BINARY(16), primary_key=True, index=True)
    titulo = Column(String(255), nullable=True)
    prompt = Column(TEXT, nullable=False)
    tipo = Column(INTEGER, nullable=False)