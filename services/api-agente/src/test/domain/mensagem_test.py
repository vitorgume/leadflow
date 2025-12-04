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
