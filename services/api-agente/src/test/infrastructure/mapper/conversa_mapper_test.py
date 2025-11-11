import datetime
import uuid
from unittest.mock import Mock

import pytest

from src.domain.conversa import Conversa
from src.infrastructure.entity.conversa_entity import ConversaEntity
from src.infrastructure.entity.mensagem_conversa_entity import MensagemConversaEntity
from src.infrastructure.mapper.conversa_mapper import ConversaMapper
from src.infrastructure.mapper.mensagem_conversa_mapper import MensagemConversaMapper


@pytest.fixture
def mensagem_mapper_mock():
    mock = Mock(spec=MensagemConversaMapper)
    mock.paraEntity.return_value = MensagemConversaEntity()
    mock.paraDomain.return_value = "MSG_DOMAIN"
    return mock

@pytest.fixture
def conversa_mapper(mensagem_mapper_mock):
    return ConversaMapper(mensagem_conversa_mapper=mensagem_mapper_mock)

def make_conversa():
    cid = str(uuid.uuid4())
    return Conversa(
        id=str(uuid.uuid4()),
        data_criacao=datetime.datetime(2025,7,25,12,0,0),
        finalizada=False,
        cliente_id_cliente=cid,
        vendedor_id_vendedor="123",
        cliente_id=cid,
        mensagens=["msg1","msg2"]
    )

# def test_para_domain(mensagem_mapper_mock, conversa_mapper):
#     conversa = make_conversa()
#
#     dummy_msg = MensagemConversaEntity()
#     dummy_msg.id_mensagem = uuid.uuid4().bytes
#     dummy_msg.conversa_id = uuid.UUID(conversa.id).bytes
#     dummy_msg.conteudo = "irrelevante"
#     dummy_msg.timestamp = conversa.data_criacao
#
#     entity = ConversaEntity(
#         id_conversa=uuid.UUID(conversa.id).bytes,
#         data_criacao=conversa.data_criacao,
#         finalizada=conversa.finalizada,
#         cliente_id_cliente=uuid.UUID(conversa.cliente_id_cliente).bytes,
#         vendedor_id_vendedor=int(conversa.vendedor_id_vendedor),
#         mensagens=[dummy_msg, dummy_msg]
#     )
#
#     domain = conversa_mapper.paraDomain(entity)
#
#     assert domain.id == conversa.id
#     assert domain.data_criacao == conversa.data_criacao.isoformat()
#     assert domain.finalizada == conversa.finalizada
#     assert domain.cliente_id_cliente == conversa.cliente_id_cliente
#     assert domain.vendedor_id_vendedor == conversa.vendedor_id_vendedor
#     assert domain.cliente_id == conversa.cliente_id
#
#     assert mensagem_mapper_mock.paraDomain.call_count == 2
#     assert domain.mensagens == ["MSG_DOMAIN", "MSG_DOMAIN"]


def make_conversa_entity(conversa):
    return ConversaEntity(
        id_conversa=uuid.UUID(conversa.id).bytes,
        data_criacao=conversa.data_criacao,
        finalizada=conversa.finalizada,
        cliente_id_cliente=uuid.UUID(conversa.cliente_id_cliente).bytes,
        vendedor_id_vendedor=int(conversa.vendedor_id_vendedor),
        mensagens=[mensagem_mapper_mock.paraEntity(None) for _ in conversa.mensagens]
    )

# def test_para_entity(mensagem_mapper_mock, conversa_mapper):
#     conversa = make_conversa()
#     entity = conversa_mapper.paraEntity(conversa)
#
#     assert all(isinstance(m, MensagemConversaEntity) for m in entity.mensagens)
#     assert mensagem_mapper_mock.paraEntity.call_count == 2

