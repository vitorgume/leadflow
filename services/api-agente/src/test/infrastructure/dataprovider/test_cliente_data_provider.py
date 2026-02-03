import pytest
from unittest.mock import Mock
import uuid

from src.infrastructure.dataprovider.cliente_data_provider import ClienteDataProvider
from src.infrastructure.mapper.cliente_mapper import ClienteMapper
from src.infrastructure.entity.cliente_entity import ClienteEntity
from src.infrastructure.exceptions.data_provider_exception import DataProviderException
import src.infrastructure.dataprovider.cliente_data_provider as provider_module

@pytest.fixture
def session_mock(monkeypatch):
    mock_session = Mock()
    mock_session.query.return_value.filter.return_value.first.return_value = None
    monkeypatch.setattr(provider_module, 'SessionLocal', lambda: mock_session)
    return mock_session

@pytest.fixture
def mapper_mock():
    return Mock(spec=ClienteMapper)

@pytest.fixture
def data_provider(mapper_mock):
    return ClienteDataProvider(cliente_mapper=mapper_mock)

def test_data_provider_init(mapper_mock):
    dp = ClienteDataProvider(mapper_mock)
    assert dp.cliente_mappper == mapper_mock

def test_consultar_por_id_found(data_provider, session_mock, mapper_mock):
    mock_entity = Mock(spec=ClienteEntity)
    session_mock.query.return_value.filter.return_value.first.return_value = mock_entity
    mapper_mock.paraDomain.return_value = "mock_domain_object"

    result = data_provider.consultar_por_id(str(uuid.uuid4()))

    session_mock.query.assert_called_once_with(ClienteEntity)
    session_mock.query.return_value.filter.assert_called_once()
    mapper_mock.paraDomain.assert_called_once_with(mock_entity)
    session_mock.close.assert_called_once()
    assert result == "mock_domain_object"

def test_consultar_por_id_not_found(data_provider, session_mock, mapper_mock):
    session_mock.query.return_value.filter.return_value.first.return_value = None
    mapper_mock.paraDomain.return_value = None

    result = data_provider.consultar_por_id(str(uuid.uuid4()))

    session_mock.query.assert_called_once_with(ClienteEntity)
    session_mock.query.return_value.filter.assert_called_once()
    mapper_mock.paraDomain.assert_called_once_with(None)
    session_mock.close.assert_called_once()
    assert result is None

def test_consultar_por_id_exception(data_provider, session_mock):
    session_mock.query.return_value.filter.return_value.first.side_effect = Exception("DB Error")

    with pytest.raises(DataProviderException, match="Erro ao consultar cliente por id."):
        data_provider.consultar_por_id(str(uuid.uuid4()))

    session_mock.rollback.assert_called_once()
    session_mock.close.assert_called_once()