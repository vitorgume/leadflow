import pytest
from src.application.exceptions.cliente_nao_encontrado_exception import ClienteNaoEncontradoException

def test_cliente_nao_encontrado_exception_default_message():
    exception = ClienteNaoEncontradoException()
    assert exception.args[0] == "Cliente não encontrado."

def test_cliente_nao_encontrado_exception_custom_message():
    custom_message = "Cliente com ID 123 não foi encontrado."
    exception = ClienteNaoEncontradoException(custom_message)
    assert exception.args[0] == custom_message