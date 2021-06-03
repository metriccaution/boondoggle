# Boondoggle Web

A self-contained web server to convert images. There's a single page, located at the root of the website, which contains a file-upload form.

The server runs, by default on port `9731`.

## Running

Once compiled from the top level of the project (see the top level README), the program itself can be run with

    java -jar boondoggle-web.jar

## Configuration

Configuration is managed at compile time, from the `com.github.metriccaution.boondoggle.web.Main` class.
