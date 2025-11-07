import uuid
import datetime
import pytest
from types import SimpleNamespace

import src.infrastructure.mapper.mensagem_conversa_mapper as mcm

@pytest.fixture(autouse=True)
def stub_entity_class(monkeypatch):
    class StubEntity:
        def __init__(self):
            pass
    monkeypatch.setattr(mcm, "MensagemConversaEntity", StubEntity)
    return StubEntity

@pytest.fixture
def mapper():
    return mcm.MensagemConversaMapper()

def make_domain():
    return mcm.MensagemConversaMapper().paraDomain

def test_paraEntity_mapear_todos_campos(mapper):
    domain = mcm.MensagemConversaMapper().paraDomain
    domain = SimpleNamespace(
        id=str(uuid.uuid4()),
        responsavel="usuario",
        conteudo="Olá!",
        conversa_id=str(uuid.uuid4()),
        data=datetime.datetime(2025,7,25,10,0,0)
    )

    entity = mapper.paraEntity(domain)

    assert hasattr(entity, "id_mensagem_conversa")
    assert entity.id_mensagem_conversa == uuid.UUID(domain.id).bytes
    assert entity.responsavel == domain.responsavel
    assert entity.conteudo   == domain.conteudo
    assert entity.id_conversa == uuid.UUID(domain.conversa_id).bytes
    assert entity.data       == domain.data

def test_paraDomain_mapear_todos_campos(mapper):
    StubEntity = mcm.MensagemConversaEntity
    msg_uuid, conv_uuid = uuid.uuid4(), uuid.uuid4()
    ent = StubEntity()
    ent.id_mensagem_conversa = msg_uuid.bytes
    ent.responsavel          = "assistente"
    ent.conteudo             = "Resposta!"
    ent.id_conversa          = conv_uuid.bytes
    ent.data                 = datetime.datetime(2025,7,25,11,0,0)

    domain = mapper.paraDomain(ent)

    from src.domain.mensagem_conversa import MensagemConversa
    assert isinstance(domain, MensagemConversa)
    assert domain.id           == str(msg_uuid)
    assert domain.responsavel == ent.responsavel
    assert domain.conteudo    == ent.conteudo
    assert domain.conversa_id == str(conv_uuid)
    assert domain.data        == ent.data

def test_roundtrip_idempotente(mapper):
    original = SimpleNamespace(
        id=str(uuid.uuid4()),
        responsavel="user",
        conteudo="Msg",
        conversa_id=str(uuid.uuid4()),
        data=datetime.datetime(2025,7,25,12,0,0)
    )
    ent      = mapper.paraEntity(original)
    restored = mapper.paraDomain(ent)

    assert restored.id           == original.id
    assert restored.responsavel == original.responsavel
    assert restored.conteudo    == original.conteudo
    assert restored.conversa_id == original.conversa_id
    assert restored.data        == original.data

@pytest.mark.parametrize("bad", [None, object()])
def test_paraEntity_input_invalido_levanta(bad, mapper):
    with pytest.raises(AttributeError):
        mapper.paraEntity(bad)

@pytest.mark.parametrize("bad", [None, object()])
def test_paraDomain_input_invalido_levanta(bad, mapper):
    with pytest.raises(AttributeError):
        mapper.paraDomain(bad)
