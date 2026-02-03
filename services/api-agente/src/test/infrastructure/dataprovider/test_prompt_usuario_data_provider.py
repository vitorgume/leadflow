import pytest
from unittest.mock import Mock
import uuid

from src.infrastructure.dataprovider.prompt_usuario_data_provider import PromptUsuarioDataprovider
from src.infrastructure.mapper.prompt_usuario_mapper import PromptUsuarioMapper
from src.infrastructure.entity.prompt_usuario_entity import PromptUsuarioEntity
from src.infrastructure.exceptions.data_provider_exception import DataProviderException
import src.infrastructure.dataprovider.prompt_usuario_data_provider as provider_module

@pytest.fixture
def session_mock(monkeypatch):
    mock_session = Mock()
    mock_session.query.return_value.filter.return_value.first.return_value = None
    monkeypatch.setattr(provider_module, 'SessionLocal', lambda: mock_session)
    return mock_session

@pytest.fixture
def mapper_mock():
    return Mock(spec=PromptUsuarioMapper)

@pytest.fixture
def data_provider(mapper_mock):
    return PromptUsuarioDataprovider(prompt_usuario_mapper=mapper_mock)

def test_data_provider_init(mapper_mock):
    dp = PromptUsuarioDataprovider(mapper_mock)
    assert dp.prompt_usuario_mapper == mapper_mock

def test_consultar_prompt_usuario_found(data_provider, session_mock, mapper_mock):
    mock_entity = Mock(spec=PromptUsuarioEntity)
    session_mock.query.return_value.filter.return_value.first.return_value = mock_entity
    mapper_mock.paraDomain.return_value = "mock_domain_object"

    result = data_provider.consultar_prompt_usuario(str(uuid.uuid4()))

    session_mock.query.assert_called_once_with(PromptUsuarioEntity)
    session_mock.query.return_value.filter.assert_called_once()
    mapper_mock.paraDomain.assert_called_once_with(mock_entity)
    session_mock.close.assert_called_once()
    assert result == "mock_domain_object"

def test_consultar_prompt_usuario_not_found(data_provider, session_mock, mapper_mock):
    session_mock.query.return_value.filter.return_value.first.return_value = None
    mapper_mock.paraDomain.return_value = None

    result = data_provider.consultar_prompt_usuario(str(uuid.uuid4()))

    session_mock.query.assert_called_once_with(PromptUsuarioEntity)
    session_mock.query.return_value.filter.assert_called_once()
    mapper_mock.paraDomain.assert_called_once_with(None)
    session_mock.close.assert_called_once()
    assert result is None

def test_consultar_prompt_usuario_exception(data_provider, session_mock):
    session_mock.query.return_value.filter.return_value.first.side_effect = Exception("DB Error")

    with pytest.raises(DataProviderException, match="Erro ao consultar prompt do usuário."):
        data_provider.consultar_prompt_usuario(str(uuid.uuid4()))

    session_mock.rollback.assert_called_once()
    session_mock.close.assert_called_once()