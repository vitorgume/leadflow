from fastapi import APIRouter
from src.entrypoint.controller.dto.mensagem_dto import MensagemDto
from src.entrypoint.controller.dto.mensagem_json_dto import MensagemJsonDto
from src.entrypoint.mapper.mensagem_json_mapper import MensagemJsonMapper
from src.entrypoint.mapper.mensagem_mapper import MensagemMapper
from src.application.usecase.mensagem_use_case import MensagemUseCase
from src.application.usecase.json_use_case import JsonUseCase
from src.infrastructure.mapper.mensagem_conversa_mapper import MensagemConversaMapper
from src.infrastructure.mapper.conversa_mapper import ConversaMapper
from src.infrastructure.dataprovider.conversa_data_provider import ConversaDataProvider
from src.infrastructure.dataprovider.agente_data_provider import AgenteDataProvider
from src.application.usecase.conversa_use_case import ConversaUseCase
from src.application.usecase.agente_use_case import AgenteUseCase

from fastapi import APIRouter, Depends

router = APIRouter()

mensagem_conversa_mapper = MensagemConversaMapper()
conversa_mapper = ConversaMapper(mensagem_conversa_mapper)
mensagem_mapper = MensagemMapper()
mensagem_json_mapper = MensagemJsonMapper()
conversa_data_provider = ConversaDataProvider(conversa_mapper)
agente_data_provider = AgenteDataProvider()
conversa_use_case = ConversaUseCase(conversa_data_provider)
agente_use_case   = AgenteUseCase(agente_data_provider)
mensagem_use_case = MensagemUseCase(conversa_use_case, agente_use_case)
json_use_case     = JsonUseCase(agente_data_provider)

def get_mensagem_use_case() -> MensagemUseCase:
    return mensagem_use_case

def get_json_use_case() -> JsonUseCase:
    return json_use_case

@router.post("/chat")
def enviar_mensagem_chat(
    msg: MensagemDto,
    use_case: MensagemUseCase = Depends(get_mensagem_use_case)
):
    dom = mensagem_mapper.paraDomain(msg)
    return use_case.processar_mensagem(dom)

@router.post("/chat/json")
def estrutura_json_usuario(
    msg: MensagemJsonDto,
    use_case: JsonUseCase = Depends(get_json_use_case)
):
    dom = mensagem_json_mapper.para_domain(msg)
    return use_case.transformar(dom)
