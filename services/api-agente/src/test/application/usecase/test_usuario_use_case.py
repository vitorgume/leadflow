import pytest
from unittest.mock import Mock

from src.application.exceptions.usuario_nao_encontrado_exception import UsuarioNaoEncontradoException
from src.application.usecase.usuario_use_case import UsuarioUseCase
from src.domain.usuario import Usuario
from src.infrastructure.dataprovider.usuario_data_provider import UsuarioDataProvider


@pytest.fixture
def usuario_data_provider_mock():
    return Mock(spec=UsuarioDataProvider)

@pytest.fixture
def usuario_use_case(usuario_data_provider_mock):
    return UsuarioUseCase(usuario_data_provider=usuario_data_provider_mock)

def test_usuario_use_case_init(usuario_data_provider_mock):
    use_case = UsuarioUseCase(usuario_data_provider_mock)
    assert use_case.usuario_data_provider == usuario_data_provider_mock

def test_consultar_por_id_found(usuario_use_case, usuario_data_provider_mock):
    mock_usuario = Mock(spec=Usuario)
    usuario_data_provider_mock.consultar_por_id.return_value = mock_usuario

    result = usuario_use_case.consultar_por_id("user-123")

    usuario_data_provider_mock.consultar_por_id.assert_called_once_with("user-123")
    assert result == mock_usuario

def test_consultar_por_id_not_found_raises_exception(usuario_use_case, usuario_data_provider_mock):
    usuario_data_provider_mock.consultar_por_id.return_value = None

    with pytest.raises(UsuarioNaoEncontradoException):
        usuario_use_case.consultar_por_id("user-456")
    usuario_data_provider_mock.consultar_por_id.assert_called_once_with("user-456")