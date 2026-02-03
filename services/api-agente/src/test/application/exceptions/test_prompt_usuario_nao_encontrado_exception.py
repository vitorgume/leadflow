import pytest
from src.application.exceptions.prompt_usuario_nao_encontrado_exception import PromptUsuarioNaoEncontradoException

def test_prompt_usuario_nao_encontrado_exception_default_message():
    exception = PromptUsuarioNaoEncontradoException()
    assert exception.args[0] == "Prompt do usuário não encontrado no id e tipo de prompt especificado"

def test_prompt_usuario_nao_encontrado_exception_custom_message():
    custom_message = "Prompt para o usuário XYZ não existe."
    exception = PromptUsuarioNaoEncontradoException(custom_message)
    assert exception.args[0] == custom_message