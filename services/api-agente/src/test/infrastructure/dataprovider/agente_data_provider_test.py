from pathlib import Path
from unittest.mock import Mock, patch

import pytest

import src.infrastructure.dataprovider.agente_data_provider as provider_module
from src.infrastructure.dataprovider.agente_data_provider import AgenteDataProvider
from src.infrastructure.exceptions.data_provider_exception import DataProviderException


class DummyChoice:
    def __init__(self, content):
        self.message = Mock()
        self.message.content = content


class DummyResponse:
    def __init__(self, content):
        self.choices = [DummyChoice(content)]


@pytest.fixture
def mock_openai_client():
    with patch('src.infrastructure.dataprovider.agente_data_provider.OpenAI') as mock_openai:
        mock_instance = mock_openai.return_value
        mock_instance.chat.completions.create.return_value = DummyResponse("Resposta IA")
        mock_instance.audio.transcriptions.create.return_value = Mock(text="fala convertida")
        yield mock_instance

@pytest.fixture
def agente():
    return AgenteDataProvider()


def test_enviar_mensagem_success(mock_openai_client, agente):
    historico = [{"role": "user", "content": "Oi"}]
    result = agente.enviar_mensagem(historico, "fake_api_key")

    mock_openai_client.chat.completions.create.assert_called_once_with(
        model=agente.modelo_chat, messages=historico, temperature=0
    )
    assert result == "Resposta IA"


def test_enviar_mensagem_empty_content(mock_openai_client, agente):
    mock_openai_client.chat.completions.create.return_value = DummyResponse("   ")
    with pytest.raises(DataProviderException) as exc:
        agente.enviar_mensagem([], "fake_api_key")
    assert agente.mensagem_erro_enviar_mensagem_ia in str(exc.value)


def test_enviar_mensagem_api_exception(mock_openai_client, agente):
    mock_openai_client.chat.completions.create.side_effect = Exception("Falha API")
    with pytest.raises(DataProviderException) as exc:
        agente.enviar_mensagem([{}], "fake_api_key")
    assert agente.mensagem_erro_enviar_mensagem_ia in str(exc.value)


def test_enviar_mensagem_transformacao_success(mock_openai_client, agente):
    historico = [{"role": "assistant", "content": "ping"}]
    expected_json = '{"key":"value"}'
    mock_openai_client.chat.completions.create.return_value = DummyResponse(expected_json)

    result = agente.enviar_mensagem_trasformacao_json(historico, "fake_api_key")

    mock_openai_client.chat.completions.create.assert_called_once_with(
        model=agente.modelo_json, messages=historico, temperature=0
    )
    assert result == expected_json


def test_enviar_mensagem_transformacao_empty(mock_openai_client, agente):
    mock_openai_client.chat.completions.create.return_value = DummyResponse("")
    result = agente.enviar_mensagem_trasformacao_json([], "fake_api_key")
    assert result == ""


def test_enviar_mensagem_transformacao_api_exception(mock_openai_client, agente):
    mock_openai_client.chat.completions.create.side_effect = Exception("Erro rede")
    with pytest.raises(DataProviderException) as exc:
        agente.enviar_mensagem_trasformacao_json([{}], "fake_api_key")
    assert agente.mensagem_erro_enviar_mensagem_ia in str(exc.value)


def test_transcrever_audio_sucesso(monkeypatch, mock_openai_client, agente):
    base_dir = Path("tmp_test_media")
    base_dir.mkdir(exist_ok=True)
    audio_path = base_dir / "audio.wav"
    audio_path.write_bytes(b"123")
    monkeypatch.setattr(agente, "_baixar_arquivo_temporario", lambda url, prefixo: str(audio_path))

    resultado = agente.transcrever_audio("http://audio/1.wav", "fake_api_key")

    mock_openai_client.audio.transcriptions.create.assert_called_once()
    assert resultado == "fala convertida"
    assert not audio_path.exists()


def test_transcrever_audio_excecao_limpa_temp(monkeypatch, mock_openai_client, agente):
    base_dir = Path("tmp_test_media")
    base_dir.mkdir(exist_ok=True)
    audio_path = base_dir / "audio2.wav"
    audio_path.write_bytes(b"abc")
    monkeypatch.setattr(agente, "_baixar_arquivo_temporario", lambda url, prefixo: str(audio_path))
    mock_openai_client.audio.transcriptions.create.side_effect = ValueError("erro")

    with pytest.raises(DataProviderException):
        agente.transcrever_audio("http://audio/2.wav", "fake_api_key")

    assert not audio_path.exists()

def test_baixar_imagem_como_data_uri(monkeypatch, agente):
    base_dir = Path("tmp_test_media")
    base_dir.mkdir(exist_ok=True)
    imagem_path = base_dir / "foto.png"
    imagem_path.write_bytes(b"PNGDATA")
    monkeypatch.setattr(agente, "_baixar_arquivo_temporario", lambda url, prefixo: str(imagem_path))

    data_uri = agente.baixar_imagem_como_data_uri("http://site/foto.png")

    assert data_uri.startswith("data:image/png;base64,")
    assert not imagem_path.exists()


def test_baixar_imagem_como_data_uri_falha(monkeypatch, agente):
    monkeypatch.setattr(
        agente,
        "_baixar_arquivo_temporario",
        lambda *args, **kwargs: (_ for _ in ()).throw(RuntimeError("download falhou")),
    )

    with pytest.raises(DataProviderException):
        agente.baixar_imagem_como_data_uri("http://site/erro.png")


def test_baixar_arquivo_temporario_sem_extensao(monkeypatch, agente):
    original_mkstemp = provider_module.tempfile.mkstemp

    tmp_root = Path("tmp_test_media")
    tmp_root.mkdir(exist_ok=True)

    def fake_mkstemp(prefix="", suffix=""):
        return original_mkstemp(dir=tmp_root, prefix=prefix, suffix=suffix)

    capturas = {}

    def fake_urlretrieve(url, destino):
        Path(destino).write_bytes(b"conteudo")
        capturas["url"] = url
        capturas["destino"] = destino
        return destino, None

    monkeypatch.setattr(provider_module.tempfile, "mkstemp", fake_mkstemp)
    monkeypatch.setattr(provider_module.request, "urlretrieve", fake_urlretrieve)

    caminho = agente._baixar_arquivo_temporario("http://exemplo.com/arquivo", prefixo="teste")

    assert Path(caminho).exists()
    assert Path(caminho).suffix == ""
    assert capturas["url"] == "http://exemplo.com/arquivo"

    Path(caminho).unlink(missing_ok=True)
