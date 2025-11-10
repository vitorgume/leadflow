import uuid
from src.domain.conversa import Conversa
from src.infrastructure.entity.conversa_entity import ConversaEntity
from src.infrastructure.mapper.mensagem_conversa_mapper import MensagemConversaMapper


class ConversaMapper:

    def __init__(self, mensagem_conversa_mapper: MensagemConversaMapper):
        self.mensagem_conversa_mapper = mensagem_conversa_mapper

    def paraEntity(self, conversa: Conversa) -> ConversaEntity:
        return ConversaEntity(
            id_conversa=uuid.UUID(conversa.id).bytes,
            data_criacao=conversa.data_criacao,
            finalizada=conversa.finalizada,
            cliente_id_cliente=uuid.UUID(conversa.cliente_id_cliente).bytes if conversa.cliente_id_cliente else None,
            vendedor_id_vendedor=int(conversa.vendedor_id_vendedor) if conversa.vendedor_id_vendedor else None,
            mensagens=[self.mensagem_conversa_mapper.paraEntity(m) for m in conversa.mensagens],
            status=conversa.status
        )

    def paraDomain(self, conversa_entity: ConversaEntity) -> Conversa:
        return Conversa(
            id=str(uuid.UUID(bytes=conversa_entity.id_conversa)),
            data_criacao=conversa_entity.data_criacao.isoformat() if conversa_entity.data_criacao else None,
            finalizada=conversa_entity.finalizada,
            cliente_id_cliente=str(uuid.UUID(bytes=conversa_entity.cliente_id_cliente)) if conversa_entity.cliente_id_cliente else None,
            vendedor_id_vendedor=str(conversa_entity.vendedor_id_vendedor) if conversa_entity.vendedor_id_vendedor else None,
            cliente_id=str(uuid.UUID(bytes=conversa_entity.cliente_id_cliente)) if conversa_entity.cliente_id_cliente else None,
            mensagens=[self.mensagem_conversa_mapper.paraDomain(m) for m in conversa_entity.mensagens],
            status=conversa_entity.status
        )
