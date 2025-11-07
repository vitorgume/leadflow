from unittest.mock import Mock

import pytest

from src.application.usecase.agente_use_case import AgenteUseCase
from src.domain.conversa import Conversa
from src.infrastructure.dataprovider.agente_data_provider import AgenteDataProvider


@pytest.fixture
def provider_mock():
    return Mock(spec=AgenteDataProvider)

@pytest.fixture
def use_case(provider_mock):
    return AgenteUseCase(agente_data_provider=provider_mock)

class FakeMensagem:
    def __init__(self, responsavel, conteudo):
        self.responsavel = responsavel
        self.conteudo = conteudo

def test_carregar_prompt_padrao(tmp_path, monkeypatch, provider_mock):
    texto = "PROMPT DEFAULT"
    prompt_file = tmp_path / "system_prompt_agent_chat.txt"
    prompt_file.write_text(texto, encoding="utf-8")

    monkeypatch.setattr(
        "src.application.usecase.agente_use_case.Path",
        lambda *args, **kwargs: prompt_file
    )

    uc = AgenteUseCase(provider_mock)
    resultado = uc._carregar_prompt_padrao()
    assert resultado == texto

def test_carregar_prompt_padrao_file_not_found(use_case, monkeypatch):
    monkeypatch.setattr(
        "builtins.open",
        lambda *args, **kwargs: (_ for _ in ()).throw(FileNotFoundError())
    )
    with pytest.raises(FileNotFoundError):
        use_case._carregar_prompt_padrao()

def test_processar_sem_historico(monkeypatch, provider_mock, use_case):
    monkeypatch.setattr(use_case, "_carregar_prompt_padrao", lambda: "PROMPT")
    conversa = Mock(spec=Conversa)
    conversa.mensagens = []
    provider_mock.enviar_mensagem.return_value = "RESPOSTA_OK"

    retorno = use_case.processar("Olá, tudo bem?", conversa)

    assert retorno == "RESPOSTA_OK"
    provider_mock.enviar_mensagem.assert_called_once_with([
        {"role": "system",    "content": "PROMPT"},
        {"role": "user",      "content": "Olá, tudo bem?"},
    ])

def test_processar_com_historico(monkeypatch, provider_mock, use_case):
    monkeypatch.setattr(use_case, "_carregar_prompt_padrao", lambda: "PROMPT")
    m1 = FakeMensagem("usuario", "Oi")
    m2 = FakeMensagem("bot",     "Tudo certo")
    conversa = Mock(spec=Conversa)
    conversa.mensagens = [m1, m2]
    provider_mock.enviar_mensagem.return_value = "RESPOSTA_FINAL"

    retorno = use_case.processar("Como posso ajudar?", conversa)

    esperado = [
        {"role": "system",    "content": "PROMPT"},
        {"role": "user",      "content": "Oi"},
        {"role": "assistant", "content": "Tudo certo"},
        {"role": "user",      "content": "Como posso ajudar?"},
    ]
    provider_mock.enviar_mensagem.assert_called_once_with(esperado)
    assert retorno == "RESPOSTA_FINAL"

def test_processar_provedor_exception(monkeypatch, provider_mock, use_case):
    monkeypatch.setattr(use_case, "_carregar_prompt_padrao", lambda: "PROMPT")
    conversa = Mock(spec=Conversa)
    conversa.mensagens = []

    provider_mock.enviar_mensagem.side_effect = RuntimeError("falha no provider")
    with pytest.raises(RuntimeError) as exc:
        use_case.processar("Teste de exceção", conversa)
    assert "falha no provider" in str(exc.value)
