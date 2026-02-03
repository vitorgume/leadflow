from sqlalchemy import Column, BINARY, JSON, String

from src.config.base_class import Base


class UsuarioEntity(Base):

    __tablename__ = "usuarios"

    id = Column(BINARY(16), primary_key=True, index=True)
    nome = Column(String(255), nullable=False)
    telefone = Column(String(20), nullable=False)
    senha = Column(String(255), nullable=False)
    email = Column(String(255), nullable=False)
    telefone_conectado = Column(String(20), nullable=False)
    atributos_qualificacao = Column(JSON, nullable=False)
    agente_api_key = Column(String(255), nullable=False)