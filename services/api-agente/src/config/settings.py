# src/config/settings.py
import os
from dotenv import load_dotenv

load_dotenv(override=False)

APP_SECURITY_ENCRYPTION_KEY = os.getenv("APP_SECURITY_ENCRYPTION_KEY")
DATABASE_URL = os.getenv("DATABASE_URL")
