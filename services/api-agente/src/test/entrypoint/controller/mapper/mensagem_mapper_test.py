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
        message="Ola, mundo!",
        audios_url=["a.mp3"],
        imagens_url=["img.png"],
    )

    domain = mapper.paraDomain(dto)

    assert isinstance(domain, Mensagem)
    assert domain.cliente_id == dto.cliente_id
    assert domain.conversa_id == dto.conversa_id
    assert domain.message == dto.message
    assert domain.audios_url == dto.audios_url
    assert domain.imagens_url == dto.imagens_url


def test_para_dto_mapeia_todos_atributos(mapper):
    domain = Mensagem(
        cliente_id="cliente-ABC",
        conversa_id="conversa-DEF",
        message="Teste de mapeamento",
        audios_url=["b.mp3"],
        imagens_url=["foto.png"],
    )

    dto = mapper.paraDto(domain)

    assert isinstance(dto, MensagemDto)
    assert dto.cliente_id == domain.cliente_id
    assert dto.conversa_id == domain.conversa_id
    assert dto.message == domain.message
    assert dto.audios_url == domain.audios_url
    assert dto.imagens_url == domain.imagens_url


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
        message="Z",
        audios_url=["x.mp3"],
        imagens_url=["y.png"],
    )
    domain = mapper.paraDomain(original_dto)
    new_dto = mapper.paraDto(domain)

    assert isinstance(new_dto, MensagemDto)
    assert new_dto.cliente_id == original_dto.cliente_id
    assert new_dto.conversa_id == original_dto.conversa_id
    assert new_dto.message == original_dto.message
    assert new_dto.audios_url == original_dto.audios_url
    assert new_dto.imagens_url == original_dto.imagens_url


def test_mapeamento_idempotente_dto_domain_dto(mapper):
    original_domain = Mensagem(
        cliente_id="123",
        conversa_id="456",
        message="ABC",
        audios_url=["m1.mp3"],
        imagens_url=["m1.png"],
    )
    dto = mapper.paraDto(original_domain)
    new_domain = mapper.paraDomain(dto)

    assert isinstance(new_domain, Mensagem)
    assert new_domain.cliente_id == original_domain.cliente_id
    assert new_domain.conversa_id == original_domain.conversa_id
    assert new_domain.message == original_domain.message
    assert new_domain.audios_url == original_domain.audios_url
    assert new_domain.imagens_url == original_domain.imagens_url
