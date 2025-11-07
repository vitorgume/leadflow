from src.application.exceptions.conversa_nao_encontrada_exception import ConversaNaoEncontradaException
from src.domain.conversa import Conversa
from src.infrastructure.dataprovider.conversa_data_provider import ConversaDataProvider
import logging

logger = logging.getLogger(__name__)

class ConversaUseCase:
    
    def __init__(self, conversa_data_provider: ConversaDataProvider):
        self.conversa_data_provider = conversa_data_provider
    
    def consulta_por_id(self, id: str) -> Conversa:
        logger.info("Consultando conversa pelo seu id. Id: %s", id)

        conversa = self.conversa_data_provider.consulta_por_id(id)
        if conversa is None:
            raise ConversaNaoEncontradaException()

        logger.info("Conversa consultada com sucesso. Conversa: %s", conversa)

        return conversa
    
    def atualiza(self, conversa: Conversa):
        logger.info("Atualizando dados de conversa. Novos dados: %s", conversa)

        self.consulta_por_id(conversa.id)
        conversa = self.conversa_data_provider.salvar(conversa)

        logger.info("Conversa atualizada com sucesso. Conversa: %s", conversa)

        return conversa

        