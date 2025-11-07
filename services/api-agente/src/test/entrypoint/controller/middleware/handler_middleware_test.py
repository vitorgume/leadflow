from src.entrypoint.controller.mensagem_controller import MensagemDto, MensagemJsonDto
import src.entrypoint.controller.mensagem_controller as chat_mod
from src.infrastructure.exceptions.data_provider_exception import DataProviderException
from fastapi import HTTPException
from uuid import uuid4
import pytest
from fastapi.testclient import TestClient
from src.app import app

client = TestClient(app, raise_server_exceptions=False)

def make_stub(exc):
    def stub(self, *args, **kwargs):
        raise exc
    return stub

@pytest.mark.parametrize(
    "endpoint,usecase_attr,method_name,dto_cls,dto_kwargs,exception,exp_status,exp_msg",
    [
      ("/chat","mensagem_use_case","processar_mensagem",
       MensagemDto,
       {"cliente_id": str(uuid4()), "conversa_id": str(uuid4()), "message":"oi"},
       DataProviderException("DP fail"), 500, "DP fail"),
      ("/chat","mensagem_use_case","processar_mensagem",
       MensagemDto,
       {"cliente_id": str(uuid4()), "conversa_id": str(uuid4()), "message":"oi"},
       HTTPException(status_code=401,detail="nope"), 401, "nope"),
      ("/chat","mensagem_use_case","processar_mensagem",
       MensagemDto,
       {"cliente_id": str(uuid4()), "conversa_id": str(uuid4()), "message":"oi"},
       RuntimeError("boom!"), 500, "boom!"),
      ("/chat/json","json_use_case","transformar",
       MensagemJsonDto, {"mensagem":"oi"},
       DataProviderException("DP fail json"), 500, "DP fail json"),
      ("/chat/json","json_use_case","transformar",
       MensagemJsonDto, {"mensagem":"oi"},
       HTTPException(status_code=400,detail="bad"), 400, "bad"),
      ("/chat/json","json_use_case","transformar",
       MensagemJsonDto, {"mensagem":"oi"},
       RuntimeError("uh oh"), 500, "uh oh"),
    ],
)
def test_exception_middleware_via_usecase(
    endpoint,usecase_attr,method_name,
    dto_cls,dto_kwargs,
    exception,exp_status,exp_msg
):
    Stub = type("Stub",(),{ method_name: make_stub(exception) })
    setattr(chat_mod, usecase_attr, Stub())

    payload = dto_cls(**dto_kwargs).model_dump()
    resp = client.post(endpoint, json=payload)
    assert resp.status_code == exp_status, resp.text

    body = resp.json()
    assert body["data"] is None
    assert body["error"]["mensagens"] == [exp_msg]
