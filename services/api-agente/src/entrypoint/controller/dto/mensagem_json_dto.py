from pydantic import BaseModel

class MensagemJsonDto(BaseModel):
    mensagem: str