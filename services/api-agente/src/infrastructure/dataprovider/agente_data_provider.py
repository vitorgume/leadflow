import logging

from openai import OpenAI

from src.config.settings import OPENAI_API_KEY
from src.infrastructure.exceptions.data_provider_exception import DataProviderException

logger = logging.getLogger(__name__)

client = OpenAI(api_key=OPENAI_API_KEY)


class AgenteDataProvider:
    mensagem_erro_enviar_mensagem_ia = "Erro ao enviar mensagem a IA."

    def enviar_mensagem(self, historico) -> str:
        try:
            response = client.chat.completions.create(
                model="gpt-4-turbo",
                messages=historico,
                temperature=0
            )

            content = response.choices[0].message.content
            logger.info("Resposta bruta da IA: %s", content)

            if not content or content.strip() == "":
                logger.error("Resposta vazia da IA.")
                raise DataProviderException("Resposta da IA vazia.")

            return content

        except Exception as e:
            logger.exception("Erro ao enviar mensagem à IA: %s", e)
            raise DataProviderException(self.mensagem_erro_enviar_mensagem_ia)

    def enviar_mensagem_trasformacao_json(self, historico):
        try:
            response = client.chat.completions.create(
                model="gpt-4",
                messages=historico,
                temperature=0
            )

            content = response.choices[0].message.content

            return content

        except Exception as e:
            logger.exception("Erro ao enviar mensagem à IA: %s", e)
            raise DataProviderException(self.mensagem_erro_enviar_mensagem_ia)