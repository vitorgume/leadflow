from src.domain.mensagem_json import MensagemJson
from src.entrypoint.controller.dto.mensagem_json_dto import MensagemJsonDto


class MensagemJsonMapper:

    def para_domain(self, mensagem_json_dto: MensagemJsonDto) -> MensagemJson:
        return MensagemJson(
            id_usuario=mensagem_json_dto.id_usuario,
            mensagem=mensagem_json_dto.mensagem
        )
