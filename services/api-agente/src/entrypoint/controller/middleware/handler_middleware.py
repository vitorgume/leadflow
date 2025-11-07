from fastapi import Request, HTTPException
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from typing import Any, List, Optional
from src.infrastructure.exceptions.data_provider_exception import DataProviderException

class ErroDto(BaseModel):
    mensagens: List[str]

class ResponseDto(BaseModel):
    data: Optional[Any] = None
    error: Optional[ErroDto] = None

async def all_exception_handler(request: Request, exc: Exception):
    if isinstance(exc, DataProviderException):
        status_code, msg = 500, exc.message
    elif isinstance(exc, HTTPException):
        status_code, msg = exc.status_code, exc.detail
    else:
        status_code, msg = 500, str(exc)

    payload = ResponseDto(data=None, error=ErroDto(mensagens=[msg])).model_dump()
    return JSONResponse(status_code=status_code, content=payload)
