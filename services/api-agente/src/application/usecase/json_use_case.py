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

    def _construir_instrucao_dinamica(self, atributos_config: List[Dict[str, Any]]) -> str:
        """
        Converte a lista de configuração de atributos do usuário em instruções de texto para o Prompt.
        Espera que atributos_config seja uma lista de dicts com chaves: 'nome_campo', 'tipo_dado', 'descricao_ia'
        """
        instrucoes = []

        exemplo_json = {}

        for attr in atributos_config:
            nome = attr.get('nome_campo', 'campo_desconhecido')
            tipo = attr.get('tipo_dado', 'string')
            desc = attr.get('descricao_ia', 'Sem descrição')

            # Monta a regra textual
            regra = f"- Campo: '{nome}'\n  - Tipo: {tipo}\n  - Instrução: {desc}"
            instrucoes.append(regra)

            # Monta parte do exemplo para ajudar a IA (One-shot learning implícito)
            valor_exemplo = "exemplo"
            if tipo.upper() in ["NUMBER", "INTEGER", "INT"]:
                valor_exemplo = 0
            elif tipo.upper() in ["BOOLEAN", "BOOL"]:
                valor_exemplo = True

            exemplo_json[nome] = valor_exemplo

        texto_regras = "\n".join(instrucoes)
        texto_exemplo = json.dumps(exemplo_json, indent=2)

        return f"{texto_regras}\n\nExemplo de formato esperado:\n{texto_exemplo}"

    def transformar(self, msg: MensagemJson):
        """
        :param msg: O texto de resumo da conversa (qualificado:true...)
        :param atributos_config: Lista vinda do banco de dados com a configuração dos campos deste usuário.
        """
        logger.info("Transformando dados do usuário em JSON dinâmico.")

        usuario = self.usuario_use_case.consultar_por_id(msg.id_usuario)

        # 1. Carrega o template
        template = self._carregar_prompt_template()

        # 2. Constrói as instruções dinâmicas
        instrucoes_campos = self._construir_instrucao_dinamica(usuario.atributos_qualificacao)

        # 3. Injeta no template
        system_prompt_final = template.replace("{{ESTRUTURA_JSON_DINAMICA}}", instrucoes_campos)

        historico = [
            {
                "role": "system",
                "content": system_prompt_final
            },
            {
                "role": "user",
                "content": msg
            }
        ]

        # Envia para a IA
        resposta = self.agente_data_provider.enviar_mensagem_trasformacao_json(historico)

        logger.info("Transformação dos dados executada com sucesso.")

        return resposta