import pytest
from src.application.exceptions.usuario_nao_encontrado_exception import UsuarioNaoEncontradoException

def test_usuario_nao_encontrado_exception_default_message():
    exception = UsuarioNaoEncontradoException()
    assert exception.args[0] == "Usuario não encontrado."

def test_usuario_nao_encontrado_exception_custom_message():
    custom_message = "Usuario com ID 789 não existe."
    exception = UsuarioNaoEncontradoException(custom_message)
    assert exception.args[0] == custom_message