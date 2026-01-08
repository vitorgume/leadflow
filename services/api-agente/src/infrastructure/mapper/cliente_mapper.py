from src.infrastructure.entity import ClienteEntity


class ClienteMapper:

    def paraDomain(self, clienteEntity: ClienteEntity) -> Cliente:
        return