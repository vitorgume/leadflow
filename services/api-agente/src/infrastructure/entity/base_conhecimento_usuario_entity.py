from sqlalchemy import Column, String, BINARY, TEXT, INTEGER

from src.config.database import Base

class BaseConhecimentoUsuarioEntity(Base):
    __tablename__ = "base_conhecimento_usuarios"

    id = Column(BINARY(16), primary_key=True, index=True)
    id_usuario = Column(BINARY(16), nullable=False)
    titulo = Column(String(255), nullable=True)
    conteudo = Column(TEXT, nullable=False)