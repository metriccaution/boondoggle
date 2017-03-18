package com.github.metriccaution.boondoggle;

import java.nio.file.Path;

public class Configuration {

	private final Path image;
	private final Path out;
	private final int maxColours;
	private final int maxWidth;
	private final int maxHeight;

	public Configuration(final Path image, final Path out, final int maxColours, final int maxWidth,
			final int maxHeight) {
		this.image = image;
		this.out = out;
		this.maxColours = maxColours;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
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

	public int getMaxWidth() {
		return maxWidth;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

}
