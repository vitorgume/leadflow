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
from src.application.usecase.base_conhecimento_usuario_use_case import BaseConhecimentoUsuarioUseCase
from src.application.usecase.cliente_use_case import ClienteUseCase
from src.application.usecase.prompt_usuario_usecase import PromptUsuarioUseCase
from src.application.usecase.usuario_use_case import UsuarioUseCase
from src.infrastructure.dataprovider.base_conhecimento_usuario_data_provider import \
    BaseConhecimentoUsuarioDataProvider
from src.infrastructure.dataprovider.cliente_data_provider import ClienteDataProvider
from src.infrastructure.dataprovider.prompt_usuario_data_provider import PromptUsuarioDataprovider
from src.infrastructure.dataprovider.usuario_data_provider import UsuarioDataProvider
from src.infrastructure.mapper.base_conhecimento_usuario_mapper import BaseConhecimentoUsuarioMapper
from src.infrastructure.mapper.cliente_mapper import ClienteMapper
from src.infrastructure.mapper.prompt_usuario_mapper import PromptUsuarioMapper
from src.infrastructure.mapper.usuario_mapper import UsuarioMapper
from src.application.security.crypto_util import CryptoUtil
from src.config.settings import APP_SECURITY_ENCRYPTION_KEY
 
from fastapi import APIRouter, Depends

router = APIRouter()

mensagem_conversa_mapper = MensagemConversaMapper()
conversa_mapper = ConversaMapper(mensagem_conversa_mapper)
mensagem_mapper = MensagemMapper()
mensagem_json_mapper = MensagemJsonMapper()
conversa_data_provider = ConversaDataProvider(conversa_mapper)
agente_data_provider = AgenteDataProvider()
conversa_use_case = ConversaUseCase(conversa_data_provider)

#Mappers
prompt_usuario_mapper = PromptUsuarioMapper()
base_conhecimento_usuario_mapper = BaseConhecimentoUsuarioMapper()
cliente_mapper = ClienteMapper()
crypto_util = CryptoUtil(APP_SECURITY_ENCRYPTION_KEY)
usuario_mapper = UsuarioMapper(crypto_util)

#Data Providers
prompt_usuario_data_provider = PromptUsuarioDataprovider(prompt_usuario_mapper)
base_conhecimento_usuario_data_provider = BaseConhecimentoUsuarioDataProvider(base_conhecimento_usuario_mapper)
cliente_data_provider = ClienteDataProvider(cliente_mapper)
usuario_data_provider = UsuarioDataProvider(usuario_mapper)

#Use Cases
prompt_usuario_usecase = PromptUsuarioUseCase(prompt_usuario_data_provider)
base_conhecimento_usuario_use_case = BaseConhecimentoUsuarioUseCase(base_conhecimento_usuario_data_provider)
cliente_use_case = ClienteUseCase(cliente_data_provider)
usuario_use_case = UsuarioUseCase(usuario_data_provider)


agente_use_case   = AgenteUseCase(agente_data_provider, prompt_usuario_usecase, base_conhecimento_usuario_use_case, cliente_use_case, usuario_use_case)
mensagem_use_case = MensagemUseCase(conversa_use_case, agente_use_case)
json_use_case     = JsonUseCase(agente_data_provider, usuario_use_case)

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
