package com.github.metriccaution.boondoggle.compression.inPlace.resize;

import java.awt.image.BufferedImage;

import org.imgscalr.Scalr;

import com.github.metriccaution.boondoggle.compression.ImageTransform;

/**
 * Limits an image's size, while maintaining aspect ratio
 */
public class ImageSizeLimiter implements ImageTransform {

	private final int maxWidth;
	private final int maxHeight;

	public ImageSizeLimiter(final int maxWidth, final int maxHeight) {
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}

	@Override
	public BufferedImage apply(final BufferedImage source) {
		final int width = source.getWidth();
		final int height = source.getHeight();

		// If the image is smaller than the limit, return it
		if (width <= maxWidth && height <= maxHeight)
			return source;

		// Pick the scaling multiplier
		final double widthScale = (double) maxWidth / width;
		final double heightScale = (double) maxHeight / height;
		final double scale = Math.min(widthScale, heightScale);

		final int newWidth = (int) (scale * width);
		final int newHeight = (int) (scale * height);

		return Scalr.resize(source, newWidth, newHeight);
	}

}
