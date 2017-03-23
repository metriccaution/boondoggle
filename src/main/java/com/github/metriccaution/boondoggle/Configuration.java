package com.github.metriccaution.boondoggle;

import java.nio.file.Path;

public class Configuration {

	private final Path directory;
	private final String source;
	private final int maxColours;
	private final int maxWidth;
	private final int maxHeight;

	public Configuration(final Path directory, final String source, final int maxColours, final int maxWidth,
			final int maxHeight) {
		this.directory = directory;
		this.source = source;
		this.maxColours = maxColours;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}

	public Path getDirectory() {
		return directory;
	}

	public String getSource() {
		return source;
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
