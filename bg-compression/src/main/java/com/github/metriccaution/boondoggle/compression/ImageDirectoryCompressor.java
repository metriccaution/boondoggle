package com.github.metriccaution.boondoggle.compression;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Compress a directory of images
 * <p>
 * The awful API is so that the set of images can be dealt with sequentially if
 * possible, and as a whole if required
 */
public interface ImageDirectoryCompressor {

	Stream<ImageFile> process(Stream<Path> images);

	public static class ImageFile {

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

}
