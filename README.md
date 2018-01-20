# Boondoggle
## Painting by numbers

A learning project for [Apache POI](https://poi.apache.org/). Draws an image into the background of a spreadsheet.

In simple terms, an image is read, pixel by pixel and the background colour of a single cell is filled in that colour.

Some image compression is done to make this take a sane amount of time, primarily, scaling images, and restricting the colour space, as both of these have significant time implications on rendering an image.

There are two different artifacts from this project:

1. A CLI tool ([bg-main](bg-main)), which picks up images from a directory, and converts them to a single spreadsheet.
2. A web version ([bg-web](bg-web)), which hosts a site with an image upload form, that provides downloads for converted sheets.

There are also two common library modules:

1. The core images to XSLX library ([bg-poi](bg-poi)).
2. A basic image compression library ([bg-compression](bg-compression)), that currently provides
	- Image size restriction
	- Colour quantisation

## Development

The project is managed with Maven, and can be compiled from source by running
	
	mvn clean install

from the top level of the project.

## Usage
See the documentation within [bg-main](bg-main) or [bg-web](bg-web) for details.
