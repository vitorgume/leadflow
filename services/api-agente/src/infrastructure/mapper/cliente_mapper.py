from src.infrastructure.entity import ClienteEntity
from src.domain.cliente import Cliente
import uuid

class ClienteMapper:

    def paraDomain(self, clienteEntity: ClienteEntity) -> Cliente:
        return Cliente(
            id=str(uuid.UUID(clienteEntity.id_cliente)),
            nome=clienteEntity.nome,
            telefone=clienteEntity.telefone,
            atributos_qualificacao=clienteEntity.atributos_qualificacao,
            inativo=clienteEntity.inativo,
            usuario_id=str(uuid.UUID(clienteEntity.usuario_id))
        )