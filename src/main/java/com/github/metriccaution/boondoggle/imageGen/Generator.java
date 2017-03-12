package com.github.metriccaution.boondoggle.imageGen;

import java.awt.image.BufferedImage;

public class Generator {

	public static BufferedImage create(final int x, final int y, final ColourMapping colourMapping) {

		final BufferedImage img = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				img.setRGB(i, j, colourMapping.apply(i, j));
			}
		}

		return img;
	}

	private Generator() {
	}

}
