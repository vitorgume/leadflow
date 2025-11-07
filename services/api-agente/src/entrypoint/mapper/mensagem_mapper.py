from src.domain.mensagem import Mensagem
from src.entrypoint.controller.dto.mensagem_dto import MensagemDto


class MensagemMapper:
    
    def paraDomain(self, dto: MensagemDto) -> Mensagem:
        return Mensagem(
            cliente_id=dto.cliente_id,
            conversa_id=dto.conversa_id,
            message=dto.message
        )
        
    def paraDto(self, domain: Mensagem) -> MensagemDto:
        return MensagemDto(
            cliente_id=domain.cliente_id,
            conversa_id=domain.conversa_id,
            message=domain.message
        )