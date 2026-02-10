from src.application.exceptions.prompt_usuario_nao_encontrado_exception import PromptUsuarioNaoEncontradoException
from src.infrastructure.dataprovider.prompt_usuario_data_provider import PromptUsuarioDataprovider


class PromptUsuarioUseCase:

    def __init__(self, prompt_usuario_dataprovider: PromptUsuarioDataprovider):
        self.prompt_usuario_dataprovider = prompt_usuario_dataprovider

    def consultar_prompt_usuario(self, id_usuario: str):
        prompt = self.prompt_usuario_dataprovider.consultar_prompt_usuario(id_usuario)

        if prompt is None:
            raise  PromptUsuarioNaoEncontradoException()

        return prompt
