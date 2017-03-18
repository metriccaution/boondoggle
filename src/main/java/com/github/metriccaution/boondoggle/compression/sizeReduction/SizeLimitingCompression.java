package com.github.metriccaution.boondoggle.compression.sizeReduction;

import java.awt.image.BufferedImage;

import org.imgscalr.Scalr;

import com.github.metriccaution.boondoggle.compression.ImageCompression;

public class SizeLimitingCompression implements ImageCompression {

	private final int maxWidth;
	private final int maxHeight;

	public SizeLimitingCompression(final int maxWidth, final int maxHeight) {
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}

	@Override
	public BufferedImage compress(final BufferedImage source) {
		final int width = source.getWidth();
		final int height = source.getHeight();

		if (width <= maxWidth && height <= maxHeight)
			return source;

		final double widthScale = (double) maxWidth / width;
		final double heightScale = (double) maxHeight / height;

		final double scale = Math.min(widthScale, heightScale);
		return Scalr.resize(source, (int) (scale * width), (int) (scale * height));
	}

}
