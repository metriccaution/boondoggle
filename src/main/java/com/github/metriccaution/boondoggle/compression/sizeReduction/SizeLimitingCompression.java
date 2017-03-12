package com.github.metriccaution.boondoggle.compression.sizeReduction;

import java.awt.image.BufferedImage;

import org.imgscalr.Scalr;

import com.github.metriccaution.boondoggle.compression.ImageCompression;

public class SizeLimitingCompression implements ImageCompression {

	private final long maxPixels;

	public SizeLimitingCompression(final long maxPixels) {
		this.maxPixels = maxPixels;
	}

	@Override
	public BufferedImage compress(final BufferedImage source) {
		final int width = source.getWidth();
		final int height = source.getHeight();
		final long pixels = width * height;

		if (pixels <= maxPixels)
			return source;

		final double scale = Math.pow((double) maxPixels / pixels, 0.5);

		return Scalr.resize(source, (int) (scale * width), (int) (scale * height));
	}

}
