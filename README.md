# Boondoggle

## Painting by numbers

Draws an image into the background of a spreadsheet.

This was originally a learning project for [Apache POI](https://poi.apache.org/), now rewritten in Python.

## Quick Start

The quickest and easiest way of trying out this project is with [Docker Compose](https://docs.docker.com/compose/), then go to http://localhost:9731/docs:

```bash
docker-compose up --build
```

If you have [uv](https://docs.astral.sh/uv/) installed, you can also run:

```bash
uv run fastapi run server.py
```

## Operation

In simple terms, an image is read, pixel by pixel and the background colour of a single cell is filled in that colour.

Some image compression is done to make this take a sane amount of time

- Scaling images - Restricting the resolution of an image limits the number of cells which need writing.
- Colour quantisation - Each cell style is an entity within an XLSX file, by restricting the number of colours, and reusing these styles for each colour, write time is (significantly) reduced.
