package com.github.metriccaution.boondoggle.poi;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A named {@link BufferedImage}
 */
public class ImageFile {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageFile.class);

	/**
	 * Load an image from a file
	 *
	 * @param p
	 *            The path to read from
	 * @return The image at that file
	 * @throws IllegalStateException
	 *             if there was a problem reading the image
	 */
	public static ImageFile fromPath(final Path p) {
		try {
			LOGGER.info("Started - Loading image from {}", p);
			final BufferedImage data = ImageIO.read(p.toFile());
			LOGGER.info("Finished - Loading image from {}", p);

			return new ImageFile(p.getFileName().toString(), data);
		} catch (final IOException e) {
			throw new IllegalStateException("Could not load image", e);
		}
	}

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

	/**
	 * Convert one buffered image to another by transforming the data it wraps
	 *
	 * @param transform
	 *            The transform to run on the image
	 * @return A new image
	 */
	public ImageFile convertImage(final Function<BufferedImage, BufferedImage> transform) {
		return new ImageFile(name, transform.apply(data));
	}

}