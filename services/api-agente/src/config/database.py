from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base
from src.config.settings import DATABASE_URL
import src.infrastructure.entity

engine = create_engine(
    DATABASE_URL,
    pool_pre_ping=True,      # testa a conexão antes de usar
    pool_recycle=280,
    future=True
)
SessionLocal = sessionmaker(bind=engine, autoflush=False, autocommit=False)
Base = declarative_base()
