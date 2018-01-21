package com.github.metriccaution.boondoggle;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.metriccaution.boondoggle.compression.ImageTransform;
import com.github.metriccaution.boondoggle.compression.colours.ColourSpaceRestriction.ColourHistogram;
import com.github.metriccaution.boondoggle.compression.resize.ImageSizeLimiter;
import com.github.metriccaution.boondoggle.poi.ImageFile;
import com.github.metriccaution.boondoggle.poi.MultiImageConverter;

/**
 * Convert a directory of images into a single spreadsheet
 */
public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	// Configuration for the conversion
	private static final Path ROOT_DIR = Paths.get(System.getProperty("user.home"), "Documents", "boondoggle");
	private static final String IMAGES_DIR = "in";
	private static final int MAX_COLOURS = 512;
	private static final int MAX_WIDTH = 900;
	private static final int MAX_HEIGHT = 250;

	public static void main(final String[] args) throws Exception {
		configureLogger();

		LOGGER.info("Starting up");

		final Path imageDirectory = ROOT_DIR.resolve(IMAGES_DIR);
		LOGGER.info("Reading from {}", imageDirectory);

		LOGGER.info("Started - Creating image compression");
		// Read all of the image files once to prepare compression functions
		final Function<BufferedImage, BufferedImage> imageCompression = imageCompression(
				imageDirectory,
				MAX_COLOURS,
				MAX_WIDTH,
				MAX_HEIGHT);
		LOGGER.info("Finished - Creating image compression");

		LOGGER.info("Starting image load");
		// Read all of the images, and compress them
		final Stream<ImageFile> images = Files.list(imageDirectory)
				.filter(Files::isRegularFile)
				.sorted()
				.map(ImageFile::fromPath)
				.map((img) -> img.convertImage(imageCompression));

		// Do the spreadsheet magic
		final Path outputPath = ROOT_DIR.resolve("out-" + System.currentTimeMillis() + ".xlsx");

		LOGGER.info("Started - Generating xlsx");
		try (FileOutputStream fileOutputStream = new FileOutputStream(outputPath.toFile())) {
			new MultiImageConverter()
			.convert(images)
			.write(fileOutputStream);
		}
		LOGGER.info("Finished - Generating xlsx");
	}

	/**
	 * Create a function for compressing each image
	 *
	 * @return A compression function
	 * @throws IOException
	 *             If there was a problem loading any of the images
	 */
	private static Function<BufferedImage, BufferedImage> imageCompression(
			final Path imageDirectory,
			final int width,
			final int height,
			final int colours
			) throws IOException {
		// Total up all the colours so we can work out the most common colours
		final ColourHistogram histogram = new ColourHistogram();

		// Run a first pass through the images to generate aggregate metrics
		Files.list(imageDirectory)
		.filter(Files::isRegularFile)
		.map(ImageFile::fromPath)
		.forEach(img -> histogram.addImage(img.getData()));

		// Build the full image compression function
		final ImageTransform sizeLimiter = new ImageSizeLimiter(width, height);
		final ImageTransform colourLimiter = histogram.restrictor(colours, 25);

		return sizeLimiter.andThen(colourLimiter);
	}

	private static void configureLogger() {
		System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
		System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
	}
}
