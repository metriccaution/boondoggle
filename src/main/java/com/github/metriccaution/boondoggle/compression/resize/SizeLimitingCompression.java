package com.github.metriccaution.boondoggle.compression.resize;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

import com.github.metriccaution.boondoggle.ImageFile;
import com.github.metriccaution.boondoggle.compression.ImageDirectoryCompressor;

public class SizeLimitingCompression implements ImageDirectoryCompressor {

	private final int maxWidth;
	private final int maxHeight;

	public SizeLimitingCompression(final int maxWidth, final int maxHeight) {
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}

	@Override
	public Stream<ImageFile> process(final Stream<Path> images) {
		return images.map(this::map);
	}

	private ImageFile map(final Path image) {
		try {
			final BufferedImage source = ImageIO.read(image.toFile());

			final int width = source.getWidth();
			final int height = source.getHeight();

			if (width <= maxWidth && height <= maxHeight)
				return new ImageFile(image.getFileName().toString(), source);

			final double widthScale = (double) maxWidth / width;
			final double heightScale = (double) maxHeight / height;

			final double scale = Math.min(widthScale, heightScale);
			final BufferedImage resized = Scalr.resize(source, (int) (scale * width), (int) (scale * height));

			return new ImageFile(image.getFileName().toString(), resized);
		} catch (final IOException e) {
			throw new IllegalStateException("Could not read image", e);
		}
	}

}
