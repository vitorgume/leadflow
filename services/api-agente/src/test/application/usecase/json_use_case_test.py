import builtins
import io
from unittest.mock import Mock

import pytest

from src.application.usecase.json_use_case import JsonUseCase
from src.infrastructure.dataprovider.agente_data_provider import AgenteDataProvider
from src.infrastructure.exceptions.data_provider_exception import DataProviderException


@pytest.fixture
def agente_provider_mock():
    mock = Mock(spec=AgenteDataProvider)
    return mock

@pytest.fixture
def json_use_case(agente_provider_mock):
    return JsonUseCase(agente_data_provider=agente_provider_mock)

def test_carregar_prompt_padrao_success(monkeypatch):
    fake_content = "SYSTEM PROMPT CONTENT"
    monkeypatch.setattr(builtins, 'open',
                        lambda file, mode='r', encoding='utf-8': io.StringIO(fake_content))

    use_case = JsonUseCase(agente_data_provider=Mock())
    result = use_case._carregar_prompt_padrao()
    assert result == fake_content

def test_carregar_prompt_padrao_file_not_found(monkeypatch):
    def fake_open(file, mode='r', encoding=None):
        raise FileNotFoundError("not found")
    monkeypatch.setattr(builtins, 'open', fake_open)

    use_case = JsonUseCase(agente_data_provider=Mock())
    with pytest.raises(FileNotFoundError):
        use_case._carregar_prompt_padrao()


def test_transformar_success(monkeypatch, agente_provider_mock, json_use_case):
    prompt_text = "PROMPT TEXT"
    monkeypatch.setattr(JsonUseCase, '_carregar_prompt_padrao', lambda self: prompt_text)

    user_msg = "{""key"": ""value""}"
    expected = '{"response": true}'
    agente_provider_mock.enviar_mensagem_trasformacao_json.return_value = expected

    result = json_use_case.transformar(user_msg)

    agente_provider_mock.enviar_mensagem_trasformacao_json.assert_called_once_with([
        {'role': 'system', 'content': prompt_text},
        {'role': 'user',   'content': user_msg}
    ])
    assert result == expected


def test_transformar_provider_exception(agente_provider_mock, json_use_case):
    agente_provider_mock.enviar_mensagem_trasformacao_json.side_effect = DataProviderException("fail json")
    with pytest.raises(DataProviderException) as exc:
        json_use_case.transformar("any")
    assert "fail json" in str(exc.value)


def test_transformar_generic_exception(agente_provider_mock, json_use_case):
    agente_provider_mock.enviar_mensagem_trasformacao_json.side_effect = Exception("oops")
    with pytest.raises(Exception) as exc:
        json_use_case.transformar("any")
    assert "oops" in str(exc.value)
