from src.application.exceptions.usuario_nao_encontrado_exception import UsuarioNaoEncontradoException
from src.infrastructure.dataprovider.usuario_data_provider import UsuarioDataProvider


class UsuarioUseCase:

    def __init__(self, usuario_data_provider: UsuarioDataProvider):
        self.usuario_data_provider = usuario_data_provider

    def consultar_por_id(self, id: str):
        usuario = self.usuario_data_provider.consultar_por_id(id)

        if usuario is None:
            raise UsuarioNaoEncontradoException()

        return usuario