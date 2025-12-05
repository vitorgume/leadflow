import uuid
import datetime
import pytest
from unittest.mock import Mock

from src.infrastructure.dataprovider.conversa_data_provider import ConversaDataProvider
from src.infrastructure.mapper.conversa_mapper import ConversaMapper
from src.domain.conversa import Conversa
from src.infrastructure.entity.conversa_entity import ConversaEntity
from src.infrastructure.exceptions.data_provider_exception import DataProviderException
from sqlalchemy.exc import SQLAlchemyError
import src.infrastructure.dataprovider.conversa_data_provider as provider_module

@pytest.fixture
def session_mock(monkeypatch):
    mock_session = Mock()
    monkeypatch.setattr(provider_module, 'SessionLocal', lambda: mock_session)
    return mock_session

@pytest.fixture
def mapper_mock():
    mapper = Mock(spec=ConversaMapper)
    return mapper

@pytest.fixture
def data_provider(mapper_mock):
    return ConversaDataProvider(conversa_mapper=mapper_mock)

def make_conversa():
    cid = str(uuid.uuid4())
    return Conversa(
        id=str(uuid.uuid4()),
        cliente_id=cid,
        mensagens=["msg1", "msg2"],
        data_criacao=datetime.datetime(2025, 7, 25, 12, 0, 0),
        finalizada=False,
        cliente_id_cliente=cid,
        vendedor_id_vendedor="123"
    )

# Helper para criar uma entidade ConversaEntity mínima
def make_conversa_entity(domain: Conversa):
    return ConversaEntity(
        id_conversa=uuid.UUID(domain.id).bytes,
        data_criacao=domain.data_criacao,
        finalizada=domain.finalizada,
        cliente_id_cliente=uuid.UUID(domain.cliente_id_cliente).bytes,
        vendedor_id_vendedor=int(domain.vendedor_id_vendedor),
        mensagens=[]
    )

class DummyEntity:
    pass

class DummyDomain:
    pass


def test_consulta_por_id_exception_raises(session_mock, mapper_mock, data_provider):
    session_mock.query.return_value.filter.return_value.first.side_effect = SQLAlchemyError("fail")

    with pytest.raises(DataProviderException) as exc:
        data_provider.consulta_por_id(str(uuid.uuid4()))
    assert "Erro ao consultar conversa" in str(exc.value)
    session_mock.close.assert_called_once()


def test_salvar_sucesso(session_mock, mapper_mock, data_provider):
    conversa = make_conversa()
    entity = make_conversa_entity(conversa)
    mapper_mock.paraEntity.return_value = entity

    persisted = DummyEntity()
    session_mock.merge.return_value = persisted
    expected_domain = DummyDomain()
    mapper_mock.paraDomain.return_value = expected_domain

    result = data_provider.salvar(conversa)

    session_mock.merge.assert_called_once_with(entity)
    session_mock.commit.assert_called_once()
    session_mock.rollback.assert_not_called()
    session_mock.close.assert_called_once()
    mapper_mock.paraDomain.assert_called_once_with(persisted)
    assert result is expected_domain


def test_salvar_exception_rollback(session_mock, mapper_mock, data_provider):
    conversa = make_conversa()
    entity = make_conversa_entity(conversa)
    mapper_mock.paraEntity.return_value = entity
    session_mock.merge.side_effect = Exception("db fail")

    with pytest.raises(DataProviderException) as exc:
        data_provider.salvar(conversa)
    assert "Erro ao salvar conversa" in str(exc.value)

    session_mock.rollback.assert_called_once()
    session_mock.close.assert_called_once()
    session_mock.commit.assert_not_called()


def test_consulta_por_id_sucesso(session_mock, mapper_mock, data_provider):
    conversa = make_conversa()
    entity = make_conversa_entity(conversa)

    session_mock.query.return_value.filter.return_value.first.return_value = entity
    expected_domain = DummyDomain()
    mapper_mock.paraDomain.return_value = expected_domain

    result = data_provider.consulta_por_id(conversa.id)

    session_mock.query.assert_called_once_with(ConversaEntity)
    assert session_mock.query.return_value.filter.called
    mapper_mock.paraDomain.assert_called_once_with(entity)
    assert result is expected_domain
    session_mock.close.assert_called_once()
