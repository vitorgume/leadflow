import base64
import logging
import mimetypes
import os
import tempfile
from pathlib import Path
from urllib import request
from urllib.parse import urlparse

from openai import OpenAI

from src.config.settings import OPENAI_API_KEY
from src.infrastructure.exceptions.data_provider_exception import DataProviderException

logger = logging.getLogger(__name__)

client = OpenAI(api_key=OPENAI_API_KEY)


class AgenteDataProvider:
    mensagem_erro_enviar_mensagem_ia = "Erro ao enviar mensagem a IA."
    modelo_chat = "gpt-5"
    modelo_json = "gpt-4"

    def enviar_mensagem(self, historico) -> str:
        try:
            response = client.chat.completions.create(
                model=self.modelo_chat,
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
            logger.exception("Erro ao enviar mensagem a IA: %s", e)
            raise DataProviderException(self.mensagem_erro_enviar_mensagem_ia)

    def enviar_mensagem_trasformacao_json(self, historico):
        try:
            response = client.chat.completions.create(
                model=self.modelo_json,
                messages=historico,
                temperature=0
            )

            content = response.choices[0].message.content

            return content

        except Exception as e:
            logger.exception("Erro ao enviar mensagem a IA: %s", e)
            raise DataProviderException(self.mensagem_erro_enviar_mensagem_ia)

    def transcrever_audio(self, audio_url: str) -> str:
        caminho_tmp = None
        try:
            caminho_tmp = self._baixar_arquivo_temporario(audio_url, prefixo="audio")
            with open(caminho_tmp, "rb") as audio_file:
                transcription = client.audio.transcriptions.create(
                    model="whisper-1",
                    file=audio_file
                )
                logger.info("Transcricao de audio concluida para %s", audio_url)
                return transcription.text
        except Exception as e:
            logger.exception("Erro ao transcrever audio %s: %s", audio_url, e)
            raise DataProviderException("Erro ao transcrever audio.")
        finally:
            if caminho_tmp and Path(caminho_tmp).exists():
                try:
                    os.remove(caminho_tmp)
                except OSError:
                    logger.warning("Falha ao remover arquivo temporario de audio: %s", caminho_tmp)

    def baixar_imagem_como_data_uri(self, imagem_url: str) -> str:
        caminho_tmp = None
        try:
            caminho_tmp = self._baixar_arquivo_temporario(imagem_url, prefixo="imagem")
            with open(caminho_tmp, "rb") as image_file:
                encoded = base64.b64encode(image_file.read()).decode("utf-8")

            mime, _ = mimetypes.guess_type(imagem_url)
            mime = mime or "image/jpeg"

            data_uri = f"data:{mime};base64,{encoded}"
            logger.info("Imagem baixada e convertida em data URI para %s", imagem_url)
            return data_uri
        except Exception as e:
            logger.exception("Erro ao preparar imagem %s para a IA: %s", imagem_url, e)
            raise DataProviderException("Erro ao preparar imagem para analise.")
        finally:
            if caminho_tmp and Path(caminho_tmp).exists():
                try:
                    os.remove(caminho_tmp)
                except OSError:
                    logger.warning("Falha ao remover arquivo temporario de imagem: %s", caminho_tmp)

    def _baixar_arquivo_temporario(self, url: str, prefixo: str) -> str:
        parsed_url = urlparse(url)
        extensao = Path(parsed_url.path).suffix

        if not extensao:
            mime, _ = mimetypes.guess_type(url)
            if mime:
                extensao = mimetypes.guess_extension(mime) or ""
            else:
                extensao = ""

        fd, caminho_tmp = tempfile.mkstemp(prefix=f"{prefixo}_", suffix=extensao)
        os.close(fd)

        logger.info("Baixando arquivo temporario de %s para %s", url, caminho_tmp)
        request.urlretrieve(url, caminho_tmp)
        return caminho_tmp
