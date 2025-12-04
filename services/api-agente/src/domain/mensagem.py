from dataclasses import dataclass, field
from typing import List, Optional, Any


@dataclass
class Mensagem:
    def __init__(
            self,
            message: str,
            conversa_id: str,
            cliente_id: Optional[str] = "",
            audios_url: Optional[List[str]] = None,
            imagens_url: Optional[List[str]] = None,
            *args: Any,
            **kwargs: Any,
    ):
        # Suporta chamadas posicionais antigas (message, conversa_id, qualquer_coisa)
        self.message = message
        self.conversa_id = conversa_id

        cid = cliente_id if isinstance(cliente_id, str) else ""
        if not cid and args:
            potencial = args[0]
            cid = potencial if isinstance(potencial, str) else ""
        self.cliente_id = kwargs.get("cliente_id", cid) or ""

        self.audios_url = audios_url if audios_url is not None else kwargs.get("audios_url", []) or []
        self.imagens_url = imagens_url if imagens_url is not None else kwargs.get("imagens_url", []) or []
