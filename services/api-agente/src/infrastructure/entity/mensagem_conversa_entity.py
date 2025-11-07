from sqlalchemy import Column, String, ForeignKey, DateTime
from sqlalchemy.orm import relationship
from src.config.database import Base
from sqlalchemy.dialects.mysql import BINARY

class MensagemConversaEntity(Base):
    __tablename__ = "mensagens_conversa"

    id_mensagem_conversa = Column(BINARY(16), primary_key=True, index=True)
    responsavel = Column(String(50), nullable=False)
    conteudo = Column(String(2000), nullable=False)
    id_conversa = Column(BINARY(16), ForeignKey("conversas_agente.id_conversa"), nullable=False)
    data = Column(DateTime(timezone=True), nullable=True)

    # Relacionamento reverso
    conversa = relationship("ConversaEntity", back_populates="mensagens")
