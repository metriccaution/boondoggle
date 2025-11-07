FROM python:3.12
COPY --from=ghcr.io/astral-sh/uv:latest /uv /uvx /bin/

WORKDIR /app

COPY .python-version .python-version
COPY pyproject.toml pyproject.toml
COPY uv.lock uv.lock

RUN uv sync --locked

COPY server.py server.py

CMD ["uv", "run", "fastapi", "run", "server.py", "--port", "9731"]
