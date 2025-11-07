from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base
from src.config.settings import DATABASE_URL
import src.infrastructure.entity

engine = create_engine(DATABASE_URL, future=True)
SessionLocal = sessionmaker(bind=engine, autoflush=False, autocommit=False)
Base = declarative_base()
