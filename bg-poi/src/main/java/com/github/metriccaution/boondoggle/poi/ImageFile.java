package com.github.metriccaution.boondoggle.poi;

import java.awt.image.BufferedImage;

public class ImageFile {

	private final String name;
	private final BufferedImage data;

	public ImageFile(final String name, final BufferedImage data) {
		this.name = name;
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public BufferedImage getData() {
		return data;
	}

}