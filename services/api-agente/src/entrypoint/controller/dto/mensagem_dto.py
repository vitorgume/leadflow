from pydantic import BaseModel, Field

class MensagemDto(BaseModel):
    cliente_id: str = Field(alias="cliente_id")
    conversa_id: str = Field(alias="conversa_id")
    message: str

    class Config:
        populate_by_name = True