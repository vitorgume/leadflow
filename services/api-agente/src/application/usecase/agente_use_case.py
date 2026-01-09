import logging
from pathlib import Path
from typing import Any, Dict, List, Tuple, Union

from src.application.usecase.base_conhecimento_usuario_use_case import BaseConhecimentoUsuarioUseCase
from src.application.usecase.cliente_use_case import ClienteUseCase
from src.application.usecase.prompt_usuario_usecase import PromptUsuarioUseCase
from src.domain.conversa import Conversa
from src.domain.mensagem import Mensagem
from src.infrastructure.dataprovider.agente_data_provider import AgenteDataProvider

logger = logging.getLogger(__name__)


class AgenteUseCase:

    def __init__(self, agente_data_provider: AgenteDataProvider, prompt_usuario_usecase: PromptUsuarioUseCase, base_conhecimento_usuario_use_case: BaseConhecimentoUsuarioUseCase, cliente_use_case: ClienteUseCase):
        self.agente_data_provider = agente_data_provider
        self.prompt_usuario_usecase = prompt_usuario_usecase
        self.base_conhecimento_usuario_use_case = base_conhecimento_usuario_use_case
        self.cliente_use_case = cliente_use_case


    def processar(self, mensagem: Union[str, Mensagem], conversa: Conversa) -> str:
        logger.info("Processando mensagem para o agente. Mensagem: %s Conversa: %s", mensagem, conversa)

        cliente = self.cliente_use_case.consutlar_por_id(conversa.cliente_id)

        historico = [
            {
                "role": "system",
                "content": self.prompt_usuario_usecase.consultar_prompt_usuario(cliente.usuario_id)
            }
        ]

        for m in conversa.mensagens:
            role = "user" if m.responsavel == "usuario" else "assistant"
            historico.append({"role": role, "content": m.conteudo})

        # Aceita mensagem como string simples ou objeto Mensagem (suporta mídias).
        if isinstance(mensagem, str):
            conteudo_usuario = mensagem
        else:
            conteudo_usuario, _ = self._preparar_conteudo_usuario(mensagem)

        historico.append({"role": "user", "content": conteudo_usuario})

        base_conhecimento = self.base_conhecimento_usuario_use_case.consultar_base_conhecimento_usuario(cliente.usuario_id)
        base_conhecimento_system = {
            "role": "system",
            "content": f"BASE_DE_CONHECIMENTO:\n{base_conhecimento}"
        }

        historico.append(base_conhecimento_system)

        resposta = self.agente_data_provider.enviar_mensagem(historico)

        logger.info("Mensagem processada pelo agente com sucesso. Resposta: %s", resposta)

        return resposta

    def _preparar_conteudo_usuario(self, mensagem: Mensagem) -> Tuple[Union[str, List[Dict[str, Any]]], str]:
        # Texto base da mensagem (pode ser vazio)
        texto_base = mensagem.message or ""

        # --- ÁUDIOS -------------------------------------------------
        transcricoes: List[str] = []

        # Garante lista e filtra URLs vazias/nulas
        audios_validos = [
            url for url in (mensagem.audios_url or [])
            if url and url.strip()
        ]

        for indice, audio_url in enumerate(audios_validos, start=1):
            transcricao = self.agente_data_provider.transcrever_audio(audio_url)
            transcricoes.append(f"[Audio {indice}] {transcricao}")

        if transcricoes:
            bloco = "\n\n".join(transcricoes)
            if texto_base.strip():
                texto_base = f"{texto_base}\n\nTranscricoes de audio:\n{bloco}"
            else:
                texto_base = f"Transcricoes de audio:\n{bloco}"



        # --- IMAGENS ------------------------------------------------
        imagens_data_uri: List[str] = []

        imagens_validas = [
            url for url in (mensagem.imagens_url or [])
            if url and url.strip()
        ]

        for imagem_url in imagens_validas:
            imagens_data_uri.append(
                self.agente_data_provider.baixar_imagem_como_data_uri(imagem_url)
            )

        # --- HISTÓRICO (texto que vai pro contexto da conversa) ----
        conteudo_historico = texto_base
        if imagens_data_uri:
            complemento = f"[{len(imagens_data_uri)} imagem(ns) anexada(s)]"
            conteudo_historico = f"{texto_base}\n\n{complemento}" if texto_base else complemento

        # --- CONTEÚDO PARA O MODELO --------------------------------
        if imagens_data_uri:
            conteudo_modelo: Union[str, List[Dict[str, Any]]] = []
            texto_para_modelo = texto_base.strip() or "Analise as imagens enviadas e responda ao usuario."
            conteudo_modelo.append({"type": "text", "text": texto_para_modelo})

            for data_uri in imagens_data_uri:
                conteudo_modelo.append(
                    {"type": "image_url", "image_url": {"url": data_uri}}
                )
        else:
            conteudo_modelo = texto_base or "Responda ao usuario."

        # Se por algum motivo o historico ficar vazio, garante algo minimamente descritivo
        if not conteudo_historico:
            conteudo_historico = (
                conteudo_modelo if isinstance(conteudo_modelo, str)
                else "Midia enviada pelo usuario."
            )

        print(f"Conteudo modelo: {conteudo_modelo}, Conteudo historico: {conteudo_historico}")

        return conteudo_modelo, conteudo_historico
