from src.domain.mensagem_conversa import MensagemConversa
from dataclasses import dataclass


@dataclass
class Conversa: 
    id: str
    cliente_id: str
    mensagens: list[MensagemConversa]
    data_criacao: str
    finalizada: bool
    cliente_id_cliente: str
    vendedor_id_vendedor: str