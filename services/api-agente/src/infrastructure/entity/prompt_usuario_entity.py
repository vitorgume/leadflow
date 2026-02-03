
from sqlalchemy import Column, String, BINARY, TEXT, INTEGER

from src.config.base_class import Base


class PromptUsuarioEntity(Base):
    __tablename__ = "prompts_usuarios"

    id = Column(BINARY(16), primary_key=True, index=True)
    id_usuario = Column(BINARY(16), nullable=False)
    titulo = Column(String(255), nullable=True)
    prompt = Column(TEXT, nullable=False)