import uuid
import pytest
from unittest.mock import Mock

from src.application.usecase.conversa_use_case import ConversaUseCase
from src.application.exceptions.conversa_nao_encontrada_exception import ConversaNaoEncontradaException
from src.infrastructure.dataprovider.conversa_data_provider import ConversaDataProvider
from src.infrastructure.exceptions.data_provider_exception import DataProviderException


@pytest.fixture
def provider_mock():
    return Mock(spec=ConversaDataProvider)


@pytest.fixture
def use_case(provider_mock):
    return ConversaUseCase(conversa_data_provider=provider_mock)

def make_conversa():
    conv = Mock()
    conv.id = str(uuid.uuid4())
    return conv


def test_consulta_por_id_success(provider_mock, use_case):
    conv = make_conversa()
    provider_mock.consulta_por_id.return_value = conv

    result = use_case.consulta_por_id(conv.id)

    provider_mock.consulta_por_id.assert_called_once_with(conv.id)
    assert result is conv


def test_consulta_por_id_not_found(provider_mock, use_case):
    provider_mock.consulta_por_id.return_value = None

    with pytest.raises(ConversaNaoEncontradaException) as exc_info:
        use_case.consulta_por_id(str(uuid.uuid4()))
    assert "Conversa não encontrada" in str(exc_info.value)

def test_atualiza_success(provider_mock, use_case):
    conv = make_conversa()
    provider_mock.consulta_por_id.return_value = conv
    saved = make_conversa()
    provider_mock.salvar.return_value = saved

    result = use_case.atualiza(conv)

    provider_mock.consulta_por_id.assert_called_once_with(conv.id)
    provider_mock.salvar.assert_called_once_with(conv)
    assert result is saved


def test_atualiza_consulta_exception(provider_mock, use_case):
    provider_mock.consulta_por_id.side_effect = ConversaNaoEncontradaException()
    conv = make_conversa()

    with pytest.raises(ConversaNaoEncontradaException):
        use_case.atualiza(conv)
    provider_mock.salvar.assert_not_called()


def test_atualiza_save_exception(provider_mock, use_case):
    conv = make_conversa()
    provider_mock.consulta_por_id.return_value = conv
    provider_mock.salvar.side_effect = DataProviderException("save fail")

    with pytest.raises(DataProviderException) as exc:
        use_case.atualiza(conv)
    provider_mock.salvar.assert_called_once_with(conv)
    assert "save fail" in str(exc.value)
