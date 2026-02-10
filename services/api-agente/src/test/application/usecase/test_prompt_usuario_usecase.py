import pytest
from unittest.mock import Mock

from src.application.exceptions.prompt_usuario_nao_encontrado_exception import PromptUsuarioNaoEncontradoException
from src.application.usecase.prompt_usuario_usecase import PromptUsuarioUseCase
from src.domain.prompt_usuario import PromptUsuario
from src.infrastructure.dataprovider.prompt_usuario_data_provider import PromptUsuarioDataprovider


@pytest.fixture
def prompt_usuario_dataprovider_mock():
    return Mock(spec=PromptUsuarioDataprovider)

@pytest.fixture
def prompt_usuario_usecase(prompt_usuario_dataprovider_mock):
    return PromptUsuarioUseCase(prompt_usuario_dataprovider=prompt_usuario_dataprovider_mock)

def test_prompt_usuario_usecase_init(prompt_usuario_dataprovider_mock):
    use_case = PromptUsuarioUseCase(prompt_usuario_dataprovider_mock)
    assert use_case.prompt_usuario_dataprovider == prompt_usuario_dataprovider_mock

def test_consultar_prompt_usuario_found(prompt_usuario_usecase, prompt_usuario_dataprovider_mock):
    mock_prompt = Mock(spec=PromptUsuario)
    prompt_usuario_dataprovider_mock.consultar_prompt_usuario.return_value = mock_prompt

    result = prompt_usuario_usecase.consultar_prompt_usuario("user-123")

    prompt_usuario_dataprovider_mock.consultar_prompt_usuario.assert_called_once_with("user-123")
    assert result == mock_prompt

def test_consultar_prompt_usuario_not_found_raises_exception(prompt_usuario_usecase, prompt_usuario_dataprovider_mock):
    prompt_usuario_dataprovider_mock.consultar_prompt_usuario.return_value = None

    with pytest.raises(PromptUsuarioNaoEncontradoException):
        prompt_usuario_usecase.consultar_prompt_usuario("user-456")
    prompt_usuario_dataprovider_mock.consultar_prompt_usuario.assert_called_once_with("user-456")