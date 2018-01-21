package com.github.metriccaution.boondoggle.compression.resize;

import java.awt.image.BufferedImage;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.metriccaution.boondoggle.compression.ImageTransform;

/**
 * Limits an image's size, while maintaining aspect ratio
 */
public class ImageSizeLimiter implements ImageTransform {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageSizeLimiter.class);

	private final int maxWidth;
	private final int maxHeight;

	public ImageSizeLimiter(final int maxWidth, final int maxHeight) {
		LOGGER.info("Creating image size limiter with max size {} x {}", maxWidth, maxHeight);
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}

	@Override
	public BufferedImage apply(final BufferedImage source) {
		final int width = source.getWidth();
		final int height = source.getHeight();
		LOGGER.info("Capping image size for image {} x {}", width, height);

		// If the image is smaller than the limit, return it
		if (width <= maxWidth && height <= maxHeight) {
			LOGGER.info("Image does not require scaling - No op");
			return source;
		}

		// Pick the scaling multiplier
		final double widthScale = (double) maxWidth / width;
		final double heightScale = (double) maxHeight / height;
		final double scale = Math.min(widthScale, heightScale);

		final int newWidth = (int) (scale * width);
		final int newHeight = (int) (scale * height);

		LOGGER.info("Resizing image to {} x {} (scale {})", newWidth, newHeight, scale);

		return Scalr.resize(source, newWidth, newHeight);
	}

}
