# src/api.py
from fastapi import FastAPI
from src.entrypoint.controller.mensagem_controller import router as chat_router  # ajuste o import para onde está seu APIRouter

app = FastAPI(title="API Agente")

@app.get("/healthz")
def healthz():
    return {"status": "ok"}

app.include_router(chat_router)
