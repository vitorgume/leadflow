import uuid
import pytest
from unittest.mock import Mock

from src.application.usecase.agente_use_case import AgenteUseCase
from src.application.usecase.conversa_use_case import ConversaUseCase
from src.application.usecase.mensagem_use_case import MensagemUseCase
from src.domain.mensagem import Mensagem
from src.domain.mensagem_conversa import MensagemConversa
from src.infrastructure.exceptions.data_provider_exception import DataProviderException


@pytest.fixture
def conversa_use_case_mock():
    return Mock(spec=ConversaUseCase)


@pytest.fixture
def agente_use_case_mock():
    return Mock(spec=AgenteUseCase)


@pytest.fixture
def mensagem_use_case(conversa_use_case_mock, agente_use_case_mock):
    return MensagemUseCase(
        conversa_use_case=conversa_use_case_mock,
        agente_use_case=agente_use_case_mock,
    )


def make_mensagem(conversa_id: str):
    return Mensagem(
        cliente_id="cliente-1",
        conversa_id=conversa_id,
        message="Ola agente!",
    )


def test_processar_mensagem_success(conversa_use_case_mock, agente_use_case_mock, mensagem_use_case):
    conv = Mock()
    conv.id = str(uuid.uuid4())
    conv.mensagens = []
    conversa_use_case_mock.consulta_por_id.return_value = conv
    agente_use_case_mock.processar.return_value = "resposta do agente"

    msg = make_mensagem(conv.id)

    result = mensagem_use_case.processar_mensagem(msg)

    assert result == "resposta do agente"
    conversa_use_case_mock.consulta_por_id.assert_called_once_with(conv.id)
    agente_use_case_mock.processar.assert_called_once_with(msg, conv)

    assert len(conv.mensagens) == 2
    user_msg, agent_msg = conv.mensagens
    assert isinstance(user_msg, MensagemConversa)
    assert user_msg.responsavel == "usuario"
    assert user_msg.conteudo == msg.message
    assert isinstance(agent_msg, MensagemConversa)
    assert agent_msg.responsavel == "agente"
    assert agent_msg.conteudo == "resposta do agente"

    conversa_use_case_mock.atualiza.assert_called_once_with(conv)


def test_processar_mensagem_consulta_exception(conversa_use_case_mock, mensagem_use_case):
    conversa_use_case_mock.consulta_por_id.side_effect = DataProviderException("fail consulta")
    msg = make_mensagem(str(uuid.uuid4()))

    with pytest.raises(DataProviderException) as exc:
        mensagem_use_case.processar_mensagem(msg)
    assert "fail consulta" in str(exc.value)


def test_processar_mensagem_agente_exception(conversa_use_case_mock, agente_use_case_mock, mensagem_use_case):
    conv = Mock()
    conv.id = str(uuid.uuid4())
    conv.mensagens = []
    conversa_use_case_mock.consulta_por_id.return_value = conv
    agente_use_case_mock.processar.side_effect = Exception("falha agente")
    msg = make_mensagem(conv.id)

    with pytest.raises(Exception) as exc:
        mensagem_use_case.processar_mensagem(msg)
    assert "falha agente" in str(exc.value)


def test_processar_mensagem_update_exception(conversa_use_case_mock, agente_use_case_mock, mensagem_use_case):
    conv = Mock()
    conv.id = str(uuid.uuid4())
    conv.mensagens = []
    conversa_use_case_mock.consulta_por_id.return_value = conv
    agente_use_case_mock.processar.return_value = "ok"
    conversa_use_case_mock.atualiza.side_effect = DataProviderException("fail update")
    msg = make_mensagem(conv.id)

    with pytest.raises(DataProviderException) as exc:
        mensagem_use_case.processar_mensagem(msg)
    assert "fail update" in str(exc.value)
