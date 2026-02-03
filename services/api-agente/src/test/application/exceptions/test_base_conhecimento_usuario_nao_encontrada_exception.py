import pytest
from src.application.exceptions.base_conhecimento_usuario_nao_encontraada_exception import BaseConhecimentoUsuarioNaoEncontradoException

def test_base_conhecimento_usuario_nao_encontrado_exception_default_message():
    exception = BaseConhecimentoUsuarioNaoEncontradoException()
    assert exception.args[0] == "Base de conhecimento não encontrada"

def test_base_conhecimento_usuario_nao_encontrado_exception_custom_message():
    custom_message = "Minha base de conhecimento não foi achada"
    exception = BaseConhecimentoUsuarioNaoEncontradoException(custom_message)
    assert exception.args[0] == custom_message