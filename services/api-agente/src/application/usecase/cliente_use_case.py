from src.application.exceptions.cliente_nao_encontrado_exception import ClienteNaoEncontradoException
from src.infrastructure.dataprovider.cliente_data_provider import ClienteDataProvider


class ClienteUseCase:

    def __init__(self, cliente_data_provider: ClienteDataProvider):
        self.cliente_data_provider = cliente_data_provider

    def consutlar_por_id(self, id: str):
        cliente = self.cliente_data_provider.consultar_por_id(id)

        if cliente is None:
            raise ClienteNaoEncontradoException()

        return cliente