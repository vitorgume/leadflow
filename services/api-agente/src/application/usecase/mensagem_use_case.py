import uuid
import datetime

from src.domain.mensagem import Mensagem
from src.application.usecase.conversa_use_case import ConversaUseCase
from src.application.usecase.agente_use_case import AgenteUseCase
from src.domain.mensagem_conversa import MensagemConversa
import logging

logger = logging.getLogger(__name__)

class MensagemUseCase:
    
    def __init__(self, conversa_use_case: ConversaUseCase, agente_use_case: AgenteUseCase):
        self.conversa_use_case = conversa_use_case
        self.agente_use_case = agente_use_case

    def processar_mensagem(self, mensagem: Mensagem) -> str:
        logger.info("Processando nova mensagem. Mensagem: %s", mensagem)

        conversa = self.conversa_use_case.consulta_por_id(mensagem.conversa_id)

        logger.info("Enviando mensagem para o agente. Mensagem: %s Conversa: %s", mensagem, conversa)

        resposta = self.agente_use_case.processar(mensagem.message, conversa)

        mensagem_usuario = MensagemConversa(
            id=str(uuid.uuid4()),
            responsavel="usuario",
            conteudo=mensagem.message,
            conversa_id=conversa.id,
            data=datetime.datetime.now()
        )

        resposta_agente = MensagemConversa(
            id=str(uuid.uuid4()),
            responsavel="agente",
            conteudo=resposta,
            conversa_id=conversa.id,
            data=datetime.datetime.now()
        )

        conversa.mensagens.append(mensagem_usuario)
        conversa.mensagens.append(resposta_agente)
        conversa = self.conversa_use_case.atualiza(conversa)

        logger.info("Mensagem processada com sucesso. Resposta agente: %s Mensagem usuário: %s Conversa: %s", resposta, mensagem_usuario, conversa)

        return resposta