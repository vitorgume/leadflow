import pytest

from src.domain.mensagem import Mensagem
from src.entrypoint.controller.dto.mensagem_dto import MensagemDto
from src.entrypoint.mapper.mensagem_mapper import MensagemMapper


@pytest.fixture
def mapper():
    return MensagemMapper()

def test_para_domain_mapeia_todos_atributos(mapper):
    dto = MensagemDto(
        cliente_id="cliente-123",
        conversa_id="conversa-456",
        message="Olá, mundo!"
    )

    domain = mapper.paraDomain(dto)

    assert isinstance(domain, Mensagem)
    assert domain.cliente_id == dto.cliente_id
    assert domain.conversa_id == dto.conversa_id
    assert domain.message     == dto.message

def test_para_dto_mapeia_todos_atributos(mapper):
    domain = Mensagem(
        cliente_id="cliente-ABC",
        conversa_id="conversa-DEF",
        message="Teste de mapeamento"
    )

    dto = mapper.paraDto(domain)

    assert isinstance(dto, MensagemDto)
    assert dto.cliente_id == domain.cliente_id
    assert dto.conversa_id == domain.conversa_id
    assert dto.message     == domain.message

@pytest.mark.parametrize("bad_input", [None, object()])
def test_para_domain_input_invalido_levanta_exception(mapper, bad_input):
    with pytest.raises(AttributeError):
        mapper.paraDomain(bad_input)

@pytest.mark.parametrize("bad_input", [None, object()])
def test_para_dto_input_invalido_levanta_exception(mapper, bad_input):
    with pytest.raises(AttributeError):
        mapper.paraDto(bad_input)

def test_mapeamento_idempotente_domain_dto_domain(mapper):
    original_dto = MensagemDto(
        cliente_id="X",
        conversa_id="Y",
        message="Z"
    )
    domain = mapper.paraDomain(original_dto)
    new_dto = mapper.paraDto(domain)

    assert isinstance(new_dto, MensagemDto)
    assert new_dto.cliente_id == original_dto.cliente_id
    assert new_dto.conversa_id == original_dto.conversa_id
    assert new_dto.message     == original_dto.message

def test_mapeamento_idempotente_dto_domain_dto(mapper):
    original_domain = Mensagem(
        cliente_id="123",
        conversa_id="456",
        message="ABC"
    )
    dto = mapper.paraDto(original_domain)
    new_domain = mapper.paraDomain(dto)

    assert isinstance(new_domain, Mensagem)
    assert new_domain.cliente_id == original_domain.cliente_id
    assert new_domain.conversa_id == original_domain.conversa_id
    assert new_domain.message     == original_domain.message
