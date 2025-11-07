FROM python:3.11-slim

# Configurações Python e pip
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    PIP_NO_CACHE_DIR=1

WORKDIR /app

# Copia requirements primeiro para aproveitar cache
COPY requirements.txt ./
RUN pip install --upgrade pip && pip install -r requirements.txt

# Copia o restante do código
COPY . .

# Usuário não-root
RUN useradd -m appuser && chown -R appuser:appuser /app
USER appuser

EXPOSE 8000

CMD ["python","-m","uvicorn","src.api:app","--host","0.0.0.0","--port","8000"]


