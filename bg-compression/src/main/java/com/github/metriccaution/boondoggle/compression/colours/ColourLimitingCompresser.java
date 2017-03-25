package com.github.metriccaution.boondoggle.compression.colours;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import com.github.metriccaution.boondoggle.compression.ImageDirectoryCompressor;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class ColourLimitingCompresser implements ImageDirectoryCompressor {

	private final ColourRestriction restriction;

	public ColourLimitingCompresser(final ColourRestriction restriction) {
		this.restriction = restriction;
	}

	@Override
	public Stream<ImageFile> process(final Stream<Path> images) {
		final List<Path> files = images.collect(Collectors.toList());
		final ColourMapper mapper = restriction.mapping(directoryColourHistogram(files));

		return files.stream().map(image -> {
			try {
				final BufferedImage source = ImageIO.read(image.toFile());
				return new ImageFile(image.getFileName().toString(), restrictColours(source, mapper));
			} catch (final IOException e) {
				throw new IllegalStateException("Could not read image", e);
			}
		});
	}

	private Multiset<Color> directoryColourHistogram(final List<Path> locations) {
		final Multiset<Color> histogram = HashMultiset.create();
		for (final Path location : locations)
			histogram.addAll(imageHistogram(location));
		return histogram;
	}

	private Multiset<Color> imageHistogram(final Path location) {
		try {
			final BufferedImage image = ImageIO.read(location.toFile());

			final Multiset<Color> histogram = HashMultiset.create();

			for (int i = 0; i < image.getWidth(); i++) {
				for (int j = 0; j < image.getHeight(); j++) {
					histogram.add(new Color(image.getRGB(i, j)));
				}
			}

			return histogram;
		} catch (final IOException e) {
			throw new IllegalStateException("Could not read image", e);
		}
	}

	private BufferedImage restrictColours(final BufferedImage img, final ColourMapper mapper) {
		final BufferedImage ret = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < img.getWidth(); i++)
			for (int j = 0; j < img.getHeight(); j++)
				ret.setRGB(i, j, mapper.map(new Color(img.getRGB(i, j))).getRGB());

		return ret;
	}

}
