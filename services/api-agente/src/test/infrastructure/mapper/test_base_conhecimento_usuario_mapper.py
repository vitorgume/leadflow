import pytest
from unittest.mock import Mock
import uuid

from src.domain.base_conhecimento_usuario import BaseConhecimentoUsuario
from src.infrastructure.entity.base_conhecimento_usuario_entity import BaseConhecimentoUsuarioEntity
from src.infrastructure.mapper.base_conhecimento_usuario_mapper import BaseConhecimentoUsuarioMapper

@pytest.fixture
def mapper():
    return BaseConhecimentoUsuarioMapper()

def test_paraDomain_with_none_entity(mapper):
    result = mapper.paraDomain(None)
    assert result is None

def test_paraDomain_with_valid_entity(mapper):
    mock_entity = Mock(spec=BaseConhecimentoUsuarioEntity)
    mock_entity.id = uuid.uuid4().bytes
    mock_entity.id_usuario = uuid.uuid4().bytes
    mock_entity.titulo = "Test Title"
    mock_entity.conteudo = "Test Content"

    result = mapper.paraDomain(mock_entity)

    assert isinstance(result, BaseConhecimentoUsuario)
    assert result.id == str(uuid.UUID(bytes=mock_entity.id))
    assert result.id_usuario == str(uuid.UUID(bytes=mock_entity.id_usuario))
    assert result.titulo == mock_entity.titulo
    assert result.conteudo == mock_entity.conteudo
