from pydantic import BaseModel
from setor_dto import SetorDto

class MensagemOutroSetorDto(BaseModel):
    id_usuario: str
    mensagem: str
    setores: list[SetorDto]
