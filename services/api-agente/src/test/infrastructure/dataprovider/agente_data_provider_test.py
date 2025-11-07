import pytest
from unittest.mock import Mock

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

@pytest.fixture(autouse=True)
def stub_openai_client(monkeypatch):
    mock_client = Mock()
    mock_completions = Mock()
    mock_client.chat = Mock(completions=mock_completions)
    monkeypatch.setattr(provider_module, 'client', mock_client)
    return mock_client

@pytest.fixture
def agente():
    return AgenteDataProvider()

def test_enviar_mensagem_success(stub_openai_client, agente):
    historico = [{'role': 'user', 'content': 'Oi'}]
    stub_openai_client.chat.completions.create.return_value = DummyResponse("Resposta IA")

    result = agente.enviar_mensagem(historico)

    stub_openai_client.chat.completions.create.assert_called_once_with(
        model='gpt-4-turbo', messages=historico, temperature=0
    )
    assert result == "Resposta IA"


def test_enviar_mensagem_empty_content(stub_openai_client, agente):
    stub_openai_client.chat.completions.create.return_value = DummyResponse("   ")
    with pytest.raises(DataProviderException) as exc:
        agente.enviar_mensagem([])
    assert agente.mensagem_erro_enviar_mensagem_ia in str(exc.value)


def test_enviar_mensagem_api_exception(stub_openai_client, agente):
    stub_openai_client.chat.completions.create.side_effect = Exception("Falha API")
    with pytest.raises(DataProviderException) as exc:
        agente.enviar_mensagem([{}])
    assert agente.mensagem_erro_enviar_mensagem_ia in str(exc.value)


def test_enviar_mensagem_transformacao_success(stub_openai_client, agente):
    historico = [{'role': 'assistant', 'content': 'ping'}]
    expected_json = '{"key":"value"}'
    stub_openai_client.chat.completions.create.return_value = DummyResponse(expected_json)

    result = agente.enviar_mensagem_trasformacao_json(historico)

    stub_openai_client.chat.completions.create.assert_called_once_with(
        model='gpt-4', messages=historico, temperature=0
    )
    assert result == expected_json


def test_enviar_mensagem_transformacao_empty(stub_openai_client, agente):
    stub_openai_client.chat.completions.create.return_value = DummyResponse("")
    result = agente.enviar_mensagem_trasformacao_json([])
    assert result == ""


def test_enviar_mensagem_transformacao_api_exception(stub_openai_client, agente):
    stub_openai_client.chat.completions.create.side_effect = Exception("Erro rede")
    with pytest.raises(DataProviderException) as exc:
        agente.enviar_mensagem_trasformacao_json([{}])
    assert agente.mensagem_erro_enviar_mensagem_ia in str(exc.value)
