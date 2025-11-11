# syntax=docker/dockerfile:1.7

# ====== BASE ======
FROM python:3.11-slim AS base
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1
WORKDIR /app

# ====== DEPS (build) ======
FROM base AS deps
# Flags de controle
ARG INCLUDE_DEV=false      # instala dev-requirements quando "true"
# Cria venv isolado (copiamos só ela pro runtime)
RUN python -m venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

# (Opcional) compilar wheels nativos
RUN apt-get update && apt-get install -y --no-install-recommends build-essential && rm -rf /var/lib/apt/lists/*

# Instala dependências com cache do pip
COPY requirements.txt .

RUN --mount=type=cache,target=/root/.cache/pip \
    pip install --upgrade pip && \
    pip install -r requirements.txt && \
    if [ "$INCLUDE_DEV" = "true" ] && [ -f requirements-dev.txt ]; then \
      pip install -r requirements-dev.txt; \
    fi

# ====== BUILD (código + testes opcionais) ======
FROM deps AS build
ARG RUN_TESTS=false        # "true" roda pytest; "false" só empacota
ENV PATH="/opt/venv/bin:$PATH"
COPY . .

# Rode os testes só quando for pedido (ex.: produção)
# Altere o comando do pytest se você usa markers, etc.
RUN --mount=type=cache,target=/root/.cache/pip \
    if [ "$RUN_TESTS" = "true" ]; then \
      pytest -q; \
    else \
      echo "Skipping tests in build stage"; \
    fi

# ====== RUNTIME (leve) ======
FROM python:3.11-slim AS runtime
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    UVICORN_HOST=0.0.0.0 \
    UVICORN_PORT=8000 \
    UVICORN_WORKERS=2
WORKDIR /app

# Copia só a venv e o app
COPY --from=deps /opt/venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"
COPY --from=build /app /app

# Usuário não-root
RUN useradd -m appuser && chown -R appuser:appuser /app
USER appuser

EXPOSE 8000
# Healthcheck simples (opcional)
# HEALTHCHECK --interval=30s --timeout=3s CMD python -c "import socket,os; s=socket.socket(); s.settimeout(2); s.connect(('127.0.0.1', int(os.getenv('UVICORN_PORT','8000')))); s.close()"

# Exec form
CMD ["uvicorn","src.api:app","--host","0.0.0.0","--port","8000","--proxy-headers"]
