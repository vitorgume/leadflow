from src.domain.mensagem import Mensagem


def test_mensagem_com_listas_definidas():
    audios = ["a.mp3"]
    imagens = ["img.png"]

    mensagem = Mensagem(
        cliente_id="cliente-123",
        conversa_id="conv-2",
        message="hi",
        audios_url=audios,
        imagens_url=imagens,
    )

    assert mensagem.cliente_id == "cliente-123"
    assert mensagem.audios_url == audios
    assert mensagem.imagens_url == imagens


def test_mensagem_defaults_listas_vazias():
    mensagem = Mensagem(
        cliente_id="cli",
        conversa_id="conv-3",
        message="msg",
    )

    assert mensagem.audios_url == []
    assert mensagem.imagens_url == []


def test_mensagem_cliente_id_kwarg():
    mensagem = Mensagem(
        "conteudo",
        "conv-4",
        cliente_id="cli-kwarg",
        audios_url=None,
        imagens_url=None,
    )

    assert mensagem.cliente_id == "cli-kwarg"
    assert mensagem.audios_url == []
    assert mensagem.imagens_url == []


def test_mensagem_cliente_id_usando_posicional_fallback():
    mensagem = Mensagem(
        "conteudo",
        "conv-5",
        None,
        None,
        None,
        "cli-pos",
    )

    assert mensagem.cliente_id == "cli-pos"
    assert mensagem.audios_url == []
    assert mensagem.imagens_url == []


def test_mensagem_cliente_id_invalido_sem_fallback():
    mensagem = Mensagem(
        "conteudo",
        "conv-6",
        999,
        [],
        [],
    )

    assert mensagem.cliente_id == ""
