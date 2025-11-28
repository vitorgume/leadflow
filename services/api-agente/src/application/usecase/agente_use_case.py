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

    def _carregar_base_conhecimento(self) -> str:
        caminho = Path("src/resources/base_conhecimento_agente.txt")
        if not caminho.exists():
            logger.warning("Base de conhecimento não encontrada em %s", caminho)
            return ""
        with open(caminho, "r", encoding="utf-8") as file:
            return file.read()

    def _carregar_estado(self, estado: int) -> str:
        match estado:
            case 0:
                return "Inativo grau 1"
            case 1:
                return "Inativo grau 2"
            case 2:
                return "Conversa finalizada"
            case 3:
                return "Conversa em andamento"

    def processar(self, mensagem: str, conversa: Conversa) -> str:
        logger.info("Processando mensagem para o agente. Mensagem: %s Conversa: %s", mensagem, conversa)

        # 1) Carrega estado persistido da conversa (já existente no seu sistema)
        estado = self._carregar_estado(conversa.status)  # dict com {fase, qualificado, dados:{...}}

        estado_system = {
            "role": "system",
            "content": (
                "STATE:\n"
                f"fase={estado}\n"
                f"qualificado={'true' if conversa.status == 2 else 'false'}\n"
                "INSTRUÇÕES DE ESTADO:\n"
                "- Se qualificado=true, não reinicie a triagem nem repita perguntas já respondidas.\n"
                "- No recontato, responda dúvidas objetivamente. Use `encaminhar: true` apenas quando necessário.\n"
            )
        }

        # 2) Seu prompt padrão vem DEPOIS do system de estado
        prompt_system = {
            "role": "system",
            "content": self._carregar_prompt_padrao()
        }

        base_conhecimento = self._carregar_base_conhecimento()
        base_conhecimento_system = {
            "role": "system",
            "content": f"BASE_DE_CONHECIMENTO:\n{base_conhecimento}"
        }

        historico = [estado_system, prompt_system]
        if base_conhecimento:
            historico.append(base_conhecimento_system)

        # 4) Janela deslizante dos últimos N turnos + papeis corretos
        ultimas = conversa.mensagens[-15:]  # ajuste conforme seu limite de tokens
        for m in ultimas:
            if m.responsavel == "usuario":
                historico.append({"role": "user", "content": m.conteudo})
            elif m.responsavel == "ia":
                historico.append({"role": "assistant", "content": m.conteudo})
            else:
                # atendente humano -> registre como nota (não como assistant)
                historico.append({"role": "system", "content": f"NOTA-HUMANO: {m.conteudo}"})

        # 5) Mensagem atual do usuário
        historico.append({"role": "user", "content": mensagem})

        # 6) Chamada ao modelo
        resposta = self.agente_data_provider.enviar_mensagem(historico)
        logger.info("Mensagem processada pelo agente com sucesso. Resposta: %s", resposta)

        return resposta

        
