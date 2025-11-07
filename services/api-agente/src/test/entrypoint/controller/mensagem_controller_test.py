# tests/test_mensagem_controller.py
import pytest
from types import SimpleNamespace
from fastapi.testclient import TestClient

import src.app as app_module
from src.entrypoint.controller.mensagem_controller import (
    get_mensagem_use_case, get_json_use_case
)

@pytest.fixture(autouse=True)
def client():
    app = app_module.app

    # Stubs
    fake_msg_uc  = SimpleNamespace(processar_mensagem=lambda md: {"reply": f"Echo: {md.message}"})
    fake_json_uc = SimpleNamespace(transformar=lambda s: {"json": s.upper()})

    # Override
    app.dependency_overrides[get_mensagem_use_case] = lambda: fake_msg_uc
    app.dependency_overrides[get_json_use_case]     = lambda: fake_json_uc

    try:
        yield TestClient(app)
    finally:
        app.dependency_overrides.clear()



def test_chat_endpoint_success(client):
    payload = {
        "cliente_id": "cliente-123",
        "conversa_id": "conv-456",
        "message": "Olá, mundo!"
    }

    response = client.post("/chat", json=payload)
    assert response.status_code == 200
    assert response.json() == {"reply": "Echo: Olá, mundo!"}

def test_chat_endpoint_missing_field(client):
    payload = {
        "cliente_id": "cliente-123",
        "conversa_id": "conv-456"
    }
    response = client.post("/chat", json=payload)
    assert response.status_code == 422

def test_chat_json_endpoint_success(client):
    payload = {"mensagem": "testando json"}
    response = client.post("/chat/json", json=payload)
    assert response.status_code == 200
    assert response.json() == {"json": "TESTANDO JSON"}

def test_chat_json_endpoint_invalid_body(client):
    response = client.post("/chat/json", json={"msg": "oi"})
    assert response.status_code == 422
