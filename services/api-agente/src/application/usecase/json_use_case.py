import logging
from pathlib import Path

from src.infrastructure.dataprovider.agente_data_provider import AgenteDataProvider

logger = logging.getLogger(__name__)

class JsonUseCase:

    def __init__(self, agente_data_provider: AgenteDataProvider):
        self.agente_data_provider = agente_data_provider

    def _carregar_prompt_padrao(self) -> str:
        # Caminho absoluto para evitar falhas quando a suite roda em subdiretórios (ex.: src/test)
        base = Path(__file__).resolve().parents[2]
        caminho = base / "resources" / "system_prompt_agent_json.txt"
        with open(caminho, "r", encoding="utf-8") as file:
            return file.read()

    def transformar(self, msg: str):
        logger.info("Trasnformando dados do usuário em json. Dados: %s", msg)

        historico = [
            {
                "role": "system",
                "content": self._carregar_prompt_padrao()
            },
            {
                "role": "user",
                "content": msg
            }
        ]

        resposta = self.agente_data_provider.enviar_mensagem_trasformacao_json(historico)

        logger.info("Trasformação dos dados excutado com sucesso.")

        return resposta