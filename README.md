# Boondoggle
## Painting by numbers

A learning project for [Apache POI](https://poi.apache.org/). Draws an image into the background of an excel workbook.

In simple terms, an image is read, pixel by pixel and the background colour of a single cell is filled in that colour.

Some image compression is done to make this take a sane amount of time, primarily, scaling images, and restricting the colour space, as both of these have significant time implications on rendering an image.
