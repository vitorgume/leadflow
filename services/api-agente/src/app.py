from fastapi import FastAPI, HTTPException
from src.entrypoint.controller.middleware.handler_middleware import all_exception_handler
from src.entrypoint.controller.mensagem_controller import router as mensagem_router

app = FastAPI()

app.add_exception_handler(Exception, all_exception_handler)

app.add_exception_handler(HTTPException, all_exception_handler)

app.include_router(mensagem_router)
