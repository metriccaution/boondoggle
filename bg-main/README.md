# Boondoggle CLI

The Boondoggle core functionality of turning images into spreadsheets, deployed as a fat JAR.

## Workflow

Once run, the program will pick up all images in a directory (non-recursively), compress them so that the conversion can happen in a sane amount of time, and then writes them all to a spreadsheet.

By default, the images are picked up from `~/Documents/boondoggle/in`, and spredsheets are printed out to `~/Documents/boondoggle/out-<timestamp>.xlsx`.

## Running

Once compiled from the top level of the project (see the top level README), the program itself can be run with

    java -jar boondoggle.jar

## Configuration

Configuration is managed at compile time, from the `com.github.metriccaution.boondoggle.Main` class.
