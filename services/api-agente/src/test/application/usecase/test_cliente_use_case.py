import pytest
from unittest.mock import Mock

from src.application.exceptions.cliente_nao_encontrado_exception import ClienteNaoEncontradoException
from src.application.usecase.cliente_use_case import ClienteUseCase
from src.domain.cliente import Cliente
from src.infrastructure.dataprovider.cliente_data_provider import ClienteDataProvider


@pytest.fixture
def cliente_data_provider_mock():
    return Mock(spec=ClienteDataProvider)

@pytest.fixture
def cliente_use_case(cliente_data_provider_mock):
    return ClienteUseCase(cliente_data_provider=cliente_data_provider_mock)

def test_cliente_use_case_init(cliente_data_provider_mock):
    use_case = ClienteUseCase(cliente_data_provider_mock)
    assert use_case.cliente_data_provider == cliente_data_provider_mock

def test_consultar_por_id_found(cliente_use_case, cliente_data_provider_mock):
    mock_cliente = Mock(spec=Cliente)
    cliente_data_provider_mock.consultar_por_id.return_value = mock_cliente

    result = cliente_use_case.consutlar_por_id("123")

    cliente_data_provider_mock.consultar_por_id.assert_called_once_with("123")
    assert result == mock_cliente

def test_consultar_por_id_not_found_raises_exception(cliente_use_case, cliente_data_provider_mock):
    cliente_data_provider_mock.consultar_por_id.return_value = None

    with pytest.raises(ClienteNaoEncontradoException):
        cliente_use_case.consutlar_por_id("456")
    cliente_data_provider_mock.consultar_por_id.assert_called_once_with("456")