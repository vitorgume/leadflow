import json

from pathlib import Path
from src.application.usecase.usuario_use_case import UsuarioUseCase
from src.domain.mensagem_outro_setor import MensagemOutroSetor
from src.infrastructure.dataprovider.agente_data_provider import AgenteDataProvider

import logging

logger = logging.getLogger(__name__)

class OutroSetorUseCase:

    def __init__(self, agente_data_provider: AgenteDataProvider, usuario_use_case: UsuarioUseCase):
        self.agente_data_provider = agente_data_provider
        self.usuario_use_case = usuario_use_case


    def _carregar_prompt_template(self) -> str:
        base = Path(__file__).resolve().parents[2]
        caminho = base / "resources" / "system_prompt_escolha_setor.txt"
        try:
            with open(caminho, "r", encoding="utf-8") as file:
                return file.read()
        except FileNotFoundError:
            logger.error(f"Arquivo de prompt não encontrado em: {caminho}")
            raise

    def escolher_setor(self, msg: MensagemOutroSetor):

        usuario = self.usuario_use_case.consultar_por_id(msg.id_usuario)

        setores = msg.setores
        json_para_prompt: str
        try:
            json_para_prompt = json.dumps(setores, ensure_ascii=False, indent=2)
        except TypeError as e:
            logger.error(f"Não foi possível serializar setores para JSON string: {e}")
            setores = "{}"  # Fallback para um JSON vazio ou tratar conforme a necessidade

        template = self._carregar_prompt_template()

        system_prompt_final = template.replace("{{SETORES}}", json_para_prompt)

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

        resposta = self.agente_data_provider.enviar_mensagem_escolha_setor(historico, usuario.agente_api_key)

        return resposta