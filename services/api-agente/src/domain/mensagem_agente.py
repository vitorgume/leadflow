from dataclasses import dataclass

from src.domain.qualificacao_agente import QualificacaoAgente


@dataclass
class MensagemAgente:
    resposta: str
    qualificacao: QualificacaoAgente