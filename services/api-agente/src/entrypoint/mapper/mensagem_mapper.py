from src.domain.mensagem import Mensagem
from src.entrypoint.controller.dto.mensagem_dto import MensagemDto


class MensagemMapper:
    
    def paraDomain(self, dto: MensagemDto) -> Mensagem:
        return Mensagem(
            cliente_id=dto.cliente_id,
            conversa_id=dto.conversa_id,
            message=dto.message,
            audios_url=dto.audios_url,
            imagens_url=dto.imagens_url
        )
        
    def paraDto(self, domain: Mensagem) -> MensagemDto:
        return MensagemDto(
            cliente_id=domain.cliente_id,
            conversa_id=domain.conversa_id,
            message=domain.message,
            audios_url=domain.audios_url,
            imagens_url=domain.imagens_url
        )
