package com.github.metriccaution.boondoggle;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import com.github.metriccaution.boondoggle.compression.IntermediateImageCompression;
import com.github.metriccaution.boondoggle.compression.colours.ColourLimitingCompresser;
import com.github.metriccaution.boondoggle.compression.resize.SizeLimitingCompression;
import com.github.metriccaution.boondoggle.poi.ImageFile;
import com.github.metriccaution.boondoggle.poi.MultiImageConverter;
import com.google.common.collect.Lists;

public class Main {

	public static void main(final String[] args) throws Exception {
		final Configuration config = new Configuration(
				Paths.get(System.getProperty("user.home"), "Documents", "boondoggle"),
				"in",
				64,
				1024,
				1024);

		final List<IntermediateImageCompression> compressionSteps = Lists.newArrayList();

		// Limit image size
		final SizeLimitingCompression sizeRestriction = new SizeLimitingCompression(config.getMaxWidth(), config.getMaxHeight());
		compressionSteps.add(new IntermediateImageCompression(sizeRestriction));

		// Colour quantisation
		final ColourLimitingCompresser colourRestriction = new ColourLimitingCompresser(config.getMaxColours());
		compressionSteps.add(new IntermediateImageCompression(colourRestriction));

		final long timestamp = System.currentTimeMillis();
		final Path sourceDirectory = config.getDirectory().resolve(config.getSource());

		for (int i = 0; i < compressionSteps.size(); i++) {
			final Path source = i == 0 ? sourceDirectory: tmpDirectory(config.getDirectory(), timestamp, i - 1);
			final Path dest = tmpDirectory(config.getDirectory(), timestamp, i);
			compressionSteps.get(i).compress(source, dest);
		}

		final Path finalDirectory = tmpDirectory(config.getDirectory(), timestamp, compressionSteps.size() - 1);

		final Stream<ImageFile> images = Files.list(finalDirectory)
				.map(p -> {
					try {
						final String name = p.getFileName().toString();
						final BufferedImage data = ImageIO.read(p.toFile());
						return new ImageFile(name, data);
					} catch (final IOException e) {
						throw new IllegalStateException("Could not read image", e);
					}
				});

		final MultiImageConverter converter = new MultiImageConverter();

		converter.convert(images).write(new FileOutputStream(config.getDirectory().resolve("out-" + timestamp + ".xlsx").toFile()));
	}

	/**
	 * Make a temporary directory to put the intermediate images into
	 *
	 * @param parent
	 *            The directory housing the whole program
	 * @param runId
	 *            A unique identifier for this run of the program
	 * @param stepNumber
	 *            The step in the compression
	 * @return A path to put the next step into, created if required
	 */
	private static Path tmpDirectory(final Path parent, final long runId, final int stepNumber) {
		try {
			final Path runDirectory = parent.resolve(Long.toString(runId));

			if (!Files.exists(runDirectory))
				Files.createDirectory(runDirectory);

			final Path directory = runDirectory.resolve(Integer.toString(stepNumber));

			if (!Files.exists(directory))
				Files.createDirectory(directory);

			return directory;
		} catch (final IOException e) {
			throw new IllegalStateException("Could not make directory", e);
		}
	}
}
