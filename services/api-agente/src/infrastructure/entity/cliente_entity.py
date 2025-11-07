from sqlalchemy import Column, String, Boolean, SmallInteger
from sqlalchemy.dialects.mysql import BINARY, TINYINT
from src.config.database import Base

class ClienteEntity(Base):
    __tablename__ = "clientes"

    id_cliente = Column(BINARY(16), primary_key=True, index=True)
    nome = Column(String(255), nullable=True)
    telefone = Column(String(255), nullable=True)
    cpf = Column(String(255), nullable=True)
    consentimento_atendimento = Column(Boolean, nullable=True)
    tipo_consulta = Column(TINYINT(unsigned=True), nullable=True)
    dor_desejo_paciente = Column(String(255), nullable=False)
    link_midia = Column(String(255), nullable=True)
    preferencia_horario = Column(TINYINT(unsigned=True), nullable=False)
    inativo = Column(Boolean, nullable=False)
