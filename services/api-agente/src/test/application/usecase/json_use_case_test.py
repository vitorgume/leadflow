import builtins
import io
from unittest.mock import Mock

import pytest

from src.application.usecase.json_use_case import JsonUseCase
from src.application.usecase.usuario_use_case import UsuarioUseCase
from src.domain.mensagem_json import MensagemJson
from src.infrastructure.dataprovider.agente_data_provider import AgenteDataProvider
from src.infrastructure.exceptions.data_provider_exception import DataProviderException


@pytest.fixture
def agente_provider_mock():
    mock = Mock(spec=AgenteDataProvider)
    return mock

@pytest.fixture
def usuario_use_case_mock():
    return Mock(spec=UsuarioUseCase)

@pytest.fixture
def json_use_case(agente_provider_mock, usuario_use_case_mock):
    return JsonUseCase(agente_data_provider=agente_provider_mock, usuario_use_case=usuario_use_case_mock)

def test_carregar_prompt_template_success(monkeypatch, json_use_case):
    fake_content = "SYSTEM PROMPT CONTENT"
    monkeypatch.setattr(builtins, 'open',
                        lambda file, mode='r', encoding='utf-8': io.StringIO(fake_content))

    result = json_use_case._carregar_prompt_template()
    assert result == fake_content

def test_carregar_prompt_template_file_not_found(monkeypatch, json_use_case):
    def fake_open(file, mode='r', encoding=None):
        raise FileNotFoundError("not found")
    monkeypatch.setattr(builtins, 'open', fake_open)

    with pytest.raises(FileNotFoundError):
        json_use_case._carregar_prompt_template()


def test_transformar_success(monkeypatch, agente_provider_mock, json_use_case, usuario_use_case_mock):
    prompt_text = "PROMPT TEXT"
    monkeypatch.setattr(JsonUseCase, '_carregar_prompt_template', lambda self: prompt_text)

    user_msg = MensagemJson(mensagem="{""key"": ""value""}", id_usuario="some-user-id")
    expected = '{"response": true}'
    agente_provider_mock.enviar_mensagem_trasformacao_json.return_value = expected

    usuario_mock = Mock()
    usuario_mock.agente_api_key = "some-api-key"
    usuario_mock.atributos_qualificacao = "{}"
    usuario_use_case_mock.consultar_por_id.return_value = usuario_mock

    result = json_use_case.transformar(user_msg)

    agente_provider_mock.enviar_mensagem_trasformacao_json.assert_called_once()
    assert result == expected


def test_transformar_provider_exception(agente_provider_mock, json_use_case, usuario_use_case_mock):
    agente_provider_mock.enviar_mensagem_trasformacao_json.side_effect = DataProviderException("fail json")
    usuario_mock = Mock()
    usuario_mock.agente_api_key = "some-api-key"
    usuario_mock.atributos_qualificacao = "{}"
    usuario_use_case_mock.consultar_por_id.return_value = usuario_mock
    with pytest.raises(DataProviderException) as exc:
        json_use_case.transformar(MensagemJson(mensagem="any", id_usuario="any"))
    assert "fail json" in str(exc.value)


def test_transformar_generic_exception(agente_provider_mock, json_use_case, usuario_use_case_mock):
    agente_provider_mock.enviar_mensagem_trasformacao_json.side_effect = Exception("oops")
    usuario_mock = Mock()
    usuario_mock.agente_api_key = "some-api-key"
    usuario_mock.atributos_qualificacao = "{}"
    usuario_use_case_mock.consultar_por_id.return_value = usuario_mock
    with pytest.raises(Exception) as exc:
        json_use_case.transformar(MensagemJson(mensagem="any", id_usuario="any"))
    assert "oops" in str(exc.value)
