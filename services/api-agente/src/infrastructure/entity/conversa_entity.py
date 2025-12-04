from sqlalchemy import Column, DateTime, Boolean, BigInteger, ForeignKey
from sqlalchemy.dialects.mysql import BINARY
from sqlalchemy.orm import relationship

from src.config.database import Base


class ConversaEntity(Base):
    __tablename__ = "conversas_agente"

    id_conversa = Column(BINARY(16), primary_key=True, index=True)
    data_criacao = Column(DateTime(timezone=True), nullable=True)
    finalizada = Column(Boolean, default=False)
    cliente_id_cliente = Column(BINARY(16), ForeignKey("clientes.id_cliente"), nullable=True)
    vendedor_id_vendedor = Column(BigInteger, ForeignKey("vendedores.id_vendedor"), nullable=True)

    mensagens = relationship("MensagemConversaEntity", back_populates="conversa", cascade="all, delete-orphan")
