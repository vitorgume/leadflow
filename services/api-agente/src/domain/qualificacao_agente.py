from dataclasses import dataclass

@dataclass
class QualificacaoAgente:
    qualificado: bool
    nome: str
    cpf: str
    consentimento_atendimento: bool
    tipo_consulta: int
    dor_desejo_paciente: str
    link_midia: str
    preferencia_horario: int