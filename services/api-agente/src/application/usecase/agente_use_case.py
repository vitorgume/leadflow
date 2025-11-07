import logging
from pathlib import Path

from src.domain.conversa import Conversa
from src.infrastructure.dataprovider.agente_data_provider import AgenteDataProvider

logger = logging.getLogger(__name__)

class AgenteUseCase:

    def __init__(self, agente_data_provider: AgenteDataProvider):
        self.agente_data_provider = agente_data_provider

    def _carregar_prompt_padrao(self) -> str:
        caminho = Path("src/resources/system_prompt_agent_chat.txt")
        with open(caminho, "r", encoding="utf-8") as file:
            return file.read()

    def processar(self, mensagem: str, conversa: Conversa) -> str:
        logger.info("Processando mensagem para o agente. Mensagem: %s Conversa: %s", mensagem, conversa)

        historico = [
            {
                "role": "system",
                "content": self._carregar_prompt_padrao()
            }
        ]

        for m in conversa.mensagens:
            role = "user" if m.responsavel == "usuario" else "assistant"
            historico.append({"role": role, "content": m.conteudo})

        historico.append({"role": "user", "content": mensagem})

        resposta = self.agente_data_provider.enviar_mensagem(historico)

        logger.info("Mensagem processada pelo agente com sucesso. Resposta: %s", resposta)

        return resposta
        