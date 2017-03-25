package com.github.metriccaution.boondoggle.compression;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import javax.imageio.ImageIO;

import com.google.common.collect.Sets;

/**
 * Compress an image, taking one directory as the source, and another as the destination
 */
public class IntermediateImageCompression {

	private static final Set<String> EXTENSIONS = Sets.newHashSet("jpg", "png", "gif");

	private final ImageDirectoryCompressor compression;

	public IntermediateImageCompression(final ImageDirectoryCompressor compression) {
		this.compression = compression;
	}

	public void compress(final Path sourceDirectory, final Path destinationDirectory) {
		try {
			compression.process(Files.list(sourceDirectory).filter(p -> EXTENSIONS.contains(getExtension(p))))
			.forEach(out -> {
				try {
					ImageIO.write(out.getData(), "png", destinationDirectory.resolve(removeExtension(out.getName()) + ".png").toFile());
				} catch (final IOException e) {
					throw new IllegalStateException("Could not write file", e);
				}
			});

		} catch (final IOException e) {
			throw new IllegalStateException("Could not read file", e);
		}
	}

	private String getExtension(final Path p) {
		final String fileName = p.getFileName().toString();
		final String[] components = fileName.split("\\.");
		return components[components.length - 1];
	}

	private String removeExtension(final String filename) {
		return filename.split("\\..{3,4}$")[0];
	}

	public ImageDirectoryCompressor getCompression() {
		return compression;
	}

}
