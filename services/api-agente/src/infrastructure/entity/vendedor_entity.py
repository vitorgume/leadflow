from sqlalchemy import Column, BigInteger, String, Boolean
from src.config.base_class import Base

class VendedorEntity(Base):
    __tablename__ = "vendedores"

    id_vendedor = Column(BigInteger, primary_key=True, autoincrement=True)
    nome = Column(String(255), nullable=True)
    telefone = Column(String(255), nullable=True)
    inativo = Column(Boolean, nullable=True)