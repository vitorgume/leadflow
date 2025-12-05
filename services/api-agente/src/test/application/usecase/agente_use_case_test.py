from unittest.mock import Mock

import pytest

from src.application.usecase.agente_use_case import AgenteUseCase
from src.domain.conversa import Conversa
from src.domain.mensagem import Mensagem
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


def test_carregar_base_conhecimento_sem_arquivo(monkeypatch, use_case):
    monkeypatch.setattr("pathlib.Path.exists", lambda self: False)
    assert use_case._carregar_base_conhecimento() == ""


def test_processar_com_historico_e_midias(monkeypatch, provider_mock, use_case):
    monkeypatch.setattr(use_case, "_carregar_prompt_padrao", lambda: "PROMPT")
    provider_mock.transcrever_audio.return_value = "fala transcrita"
    provider_mock.baixar_imagem_como_data_uri.return_value = "data:image/png;base64,xpto"
    provider_mock.enviar_mensagem.return_value = "RESPOSTA_FINAL"

    m1 = FakeMensagem("usuario", "Oi")
    m2 = FakeMensagem("bot", "Tudo certo")
    conversa = Mock(spec=Conversa)
    conversa.mensagens = [m1, m2]

    mensagem = Mensagem(
        message="texto base",
        conversa_id="abc",
        audios_url=["", "http://audio.com/1.mp3"],
        imagens_url=["http://img.com/a.png"]
    )

    retorno = use_case.processar(mensagem, conversa)

    esperado = [
        {"role": "system", "content": "PROMPT"},
        {"role": "user", "content": "Oi"},
        {"role": "assistant", "content": "Tudo certo"},
        {
            "role": "user",
            "content": [
                {"type": "text", "text": "texto base\n\nTranscricoes de audio:\n[Audio 1] fala transcrita"},
                {"type": "image_url", "image_url": {"url": "data:image/png;base64,xpto"}}
            ]
        },
    ]
    provider_mock.enviar_mensagem.assert_called_once_with(esperado)
    provider_mock.transcrever_audio.assert_called_once_with("http://audio.com/1.mp3")
    provider_mock.baixar_imagem_como_data_uri.assert_called_once_with("http://img.com/a.png")
    assert retorno == "RESPOSTA_FINAL"


def test_preparar_conteudo_usuario_apenas_audio(provider_mock, use_case):
    provider_mock.transcrever_audio.return_value = "fala clara"
    mensagem = Mensagem(
        message="Base",
        conversa_id="c1",
        audios_url=["", "http://audio.test/a.wav"],
        imagens_url=None,
    )

    conteudo_modelo, conteudo_historico = use_case._preparar_conteudo_usuario(mensagem)

    esperado_texto = "Base\n\nTranscricoes de audio:\n[Audio 1] fala clara"
    assert conteudo_modelo == esperado_texto
    assert conteudo_historico == esperado_texto
    provider_mock.transcrever_audio.assert_called_once_with("http://audio.test/a.wav")


def test_preparar_conteudo_usuario_apenas_imagem(provider_mock, use_case):
    provider_mock.baixar_imagem_como_data_uri.return_value = "data:image/jpeg;base64,z"
    mensagem = Mensagem(
        message="",
        conversa_id="c2",
        audios_url=[],
        imagens_url=["http://img/1.jpg"],
    )

    conteudo_modelo, conteudo_historico = use_case._preparar_conteudo_usuario(mensagem)

    assert conteudo_historico == "[1 imagem(ns) anexada(s)]"
    assert conteudo_modelo[0]["type"] == "text"
    assert "Analise as imagens" in conteudo_modelo[0]["text"]
    assert conteudo_modelo[1]["image_url"]["url"] == "data:image/jpeg;base64,z"

# def test_processar_sem_historico(monkeypatch, provider_mock, use_case):
#     monkeypatch.setattr(use_case, "_carregar_prompt_padrao", lambda: "PROMPT")
#     conversa = Mock(spec=Conversa)
#     conversa.mensagens = []
#     provider_mock.enviar_mensagem.return_value = "RESPOSTA_OK"
#
#     retorno = use_case.processar("Olá, tudo bem?", conversa)
#
#     assert retorno == "RESPOSTA_OK"
#     provider_mock.enviar_mensagem.assert_called_once_with([
#         {"role": "system",    "content": "PROMPT"},
#         {"role": "user",      "content": "Olá, tudo bem?"},
#     ])
#
# def test_processar_com_historico(monkeypatch, provider_mock, use_case):
#     monkeypatch.setattr(use_case, "_carregar_prompt_padrao", lambda: "PROMPT")
#     m1 = FakeMensagem("usuario", "Oi")
#     m2 = FakeMensagem("bot",     "Tudo certo")
#     conversa = Mock(spec=Conversa)
#     conversa.mensagens = [m1, m2]
#     provider_mock.enviar_mensagem.return_value = "RESPOSTA_FINAL"
#
#     retorno = use_case.processar("Como posso ajudar?", conversa)
#
#     esperado = [
#         {"role": "system",    "content": "PROMPT"},
#         {"role": "user",      "content": "Oi"},
#         {"role": "assistant", "content": "Tudo certo"},
#         {"role": "user",      "content": "Como posso ajudar?"},
#     ]
#     provider_mock.enviar_mensagem.assert_called_once_with(esperado)
#     assert retorno == "RESPOSTA_FINAL"
#
# def test_processar_provedor_exception(monkeypatch, provider_mock, use_case):
#     monkeypatch.setattr(use_case, "_carregar_prompt_padrao", lambda: "PROMPT")
#     conversa = Mock(spec=Conversa)
#     conversa.mensagens = []
#
#     provider_mock.enviar_mensagem.side_effect = RuntimeError("falha no provider")
#     with pytest.raises(RuntimeError) as exc:
#         use_case.processar("Teste de exceção", conversa)
#     assert "falha no provider" in str(exc.value)
