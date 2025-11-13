#!/usr/bin/env bash

set -e
set -u
set -o pipefail

uv run ruff check --fix
uv run ruff format
uv run pytest
