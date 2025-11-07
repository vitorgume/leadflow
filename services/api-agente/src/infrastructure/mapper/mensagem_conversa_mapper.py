from src.infrastructure.entity.mensagem_conversa_entity import MensagemConversaEntity
from src.domain.mensagem_conversa import MensagemConversa
import uuid

class MensagemConversaMapper:

    def paraEntity(self, mensagem: MensagemConversa) -> MensagemConversaEntity:
        entidade = MensagemConversaEntity()
        entidade.id_mensagem_conversa = uuid.UUID(mensagem.id).bytes
        entidade.responsavel = mensagem.responsavel
        entidade.conteudo = mensagem.conteudo
        entidade.id_conversa = uuid.UUID(mensagem.conversa_id).bytes
        entidade.data = mensagem.data
        return entidade

    def paraDomain(self, mensagemConversaEntity: MensagemConversaEntity) -> MensagemConversa:
        mensagemConversa = MensagemConversa(
            id=str(uuid.UUID(bytes=mensagemConversaEntity.id_mensagem_conversa)),
            responsavel=mensagemConversaEntity.responsavel,
            conteudo=mensagemConversaEntity.conteudo,
            conversa_id=str(uuid.UUID(bytes=mensagemConversaEntity.id_conversa)),
            data=mensagemConversaEntity.data
        )
        return mensagemConversa

