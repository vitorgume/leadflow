from src.application.exceptions.base_conhecimento_usuario_nao_encontraada_exception import \
    BaseConhecimentoUsuarioNaoEncontradoException
from src.infrastructure.dataprovider.base_conhecimento_usuario_data_provider import BaseConhecimentoUsuarioDataProvider


class BaseConhecimentoUsuarioUseCase:

    def __init__(self, base_conhecimento_usuario_dataprovider: BaseConhecimentoUsuarioDataProvider):
        self.base_conhecimento_usuario_dataprovider = base_conhecimento_usuario_dataprovider

    def consultar_base_conhecimento_usuario(self, id_usuario: str):
        base_conhecimento = self.base_conhecimento_usuario_dataprovider.consultar_base_conhecimento_usuario(id_usuario)

        if base_conhecimento is None:
            raise BaseConhecimentoUsuarioNaoEncontradoException()

        return base_conhecimento