import json
import logging
from pathlib import Path
from typing import List, Dict, Any

from src.application.usecase.usuario_use_case import UsuarioUseCase
from src.domain.mensagem_json import MensagemJson
from src.infrastructure.dataprovider.agente_data_provider import AgenteDataProvider

# Configure seu logger aqui se necessário
logger = logging.getLogger(__name__)


class JsonUseCase:

    def __init__(self, agente_data_provider: AgenteDataProvider, usuario_use_case: UsuarioUseCase):
        self.agente_data_provider = agente_data_provider
        self.usuario_use_case = usuario_use_case

    def _carregar_prompt_template(self) -> str:
        # Renomeei para deixar claro que é um template
        base = Path(__file__).resolve().parents[2]
        caminho = base / "resources" / "system_prompt_agent_json.txt"
        try:
            with open(caminho, "r", encoding="utf-8") as file:
                return file.read()
        except FileNotFoundError:
            logger.error(f"Arquivo de prompt não encontrado em: {caminho}")
            raise



    def transformar(self, msg: MensagemJson):
        """
        :param msg: O texto de resumo da conversa (qualificado:true...)
        :param atributos_config: Lista vinda do banco de dados com a configuração dos campos deste usuário.
        """
        logger.info("Transformando dados do usuário em JSON dinâmico. Mensagem: %s", msg)

        usuario = self.usuario_use_case.consultar_por_id(msg.id_usuario)

        # Garante que atributos_qualificacao seja uma string JSON
        json_para_prompt = usuario.atributos_qualificacao
        if not isinstance(json_para_prompt, str):
            try:
                json_para_prompt = json.dumps(json_para_prompt, ensure_ascii=False, indent=2)
            except TypeError as e:
                logger.error(f"Não foi possível serializar atributos_qualificacao para JSON string: {e}")
                json_para_prompt = "{}" # Fallback para um JSON vazio ou tratar conforme a necessidade

        # 1. Carrega o template
        template = self._carregar_prompt_template()

        # 2. Injeta no template
        system_prompt_final = template.replace("{{ESTRUTURA_JSON_DINAMICA}}", json_para_prompt)

        historico = [
            {
                "role": "system",
                "content": system_prompt_final
            },
            {
                "role": "user",
                "content": msg.mensagem
            }
        ]

        # Envia para a IA
        resposta = self.agente_data_provider.enviar_mensagem_trasformacao_json(historico, usuario.agente_api_key)

        ##resposta = ""

        logger.info("Transformação dos dados executada com sucesso.")

        return resposta