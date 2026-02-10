from pydantic import BaseModel

class MensagemJsonDto(BaseModel):
    id_usuario: str
    mensagem: str