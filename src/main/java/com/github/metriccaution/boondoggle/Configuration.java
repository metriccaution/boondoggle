package com.github.metriccaution.boondoggle;

import java.nio.file.Path;

public class Configuration {

	private final Path image;
	private final Path out;
	private final int maxColours;
	private final int maxPixels;

	public Configuration(final Path image, final Path out, final int maxColours, final int maxPixels) {
		this.image = image;
		this.out = out;
		this.maxColours = maxColours;
		this.maxPixels = maxPixels;
	}

	public Path getImage() {
		return image;
	}

	public Path getOut() {
		return out;
	}

	public int getMaxColours() {
		return maxColours;
	}

	public int getMaxPixels() {
		return maxPixels;
	}

}
