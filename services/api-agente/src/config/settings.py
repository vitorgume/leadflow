# src/config/settings.py
import os
from dotenv import load_dotenv

load_dotenv(override=False)

OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
DATABASE_URL   = os.getenv("DATABASE_URL")
