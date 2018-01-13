package com.github.metriccaution.boondoggle.compression.inPlace.colours;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.github.metriccaution.boondoggle.compression.ImageTransform;
import com.google.common.collect.*;

/**
 * Limits the number of colours present in an image
 */
public class ColourSpaceRestriction implements ImageTransform {
	private final int colourCount;
	private final int minimumDistance;

	public ColourSpaceRestriction(final int colourCount, final int minimumDistance) {
		this.colourCount = colourCount;
		this.minimumDistance = minimumDistance;
	}

	@Override
	public BufferedImage apply(final BufferedImage t) {
		// Create a delegating transformer based on the contents of the image
		final ImageTransform transform = new ColourHistogram().addImage(t).restrictor(colourCount, minimumDistance);
		return transform.apply(t);
	}

	private static double distance(final Color a, final Color b) {
		final double red = Math.pow(a.getRed() - b.getRed(), 2);
		final double green = Math.pow(a.getGreen() - b.getGreen(), 2);
		final double blue = Math.pow(a.getBlue() - b.getBlue(), 2);
		return Math.pow(red + green + blue, 0.5);
	}

	/**
	 * Sums up a bunch of colours, and then produces a function to restrict the
	 * colours in those images to a representative subset of colours
	 */
	private static class ColourHistogram {
		private final Multiset<Color> colours;

		public ColourHistogram() {
			colours = HashMultiset.create();
		}

		public ColourHistogram addImage(final BufferedImage image) {
			for (int i = 0; i < image.getWidth(); i++) {
				for (int j = 0; j < image.getHeight(); j++) {
					addColour(new Color(image.getRGB(i, j)));
				}
			}

			return this;
		}

		private void addColour(final Color colour) {
			colours.add(colour);
		}

		public ImageTransform restrictor(final int colourCount, final int minimumDistance) {
			return new ColourRestrictor(chooseColours(colourCount, minimumDistance));
		}

		private Set<Color> chooseColours(final int colourCount, final int minimumDistance) {
			if (colours.size() <= colourCount)
				return colours.elementSet();

			final List<Color> sorted = Lists.newArrayList(colours.elementSet());
			Collections.sort(sorted, (a, b) -> {
				return colours.count(a) - colours.count(b);
			});

			// Pick colours based off the sorted list, but far enough away from eachother
			final Set<Color> chosen = Sets.newHashSet();
			try {
				for (final Color colour : sorted) {
					final boolean allowed = !chosen.stream().anyMatch((c) -> distance(c, colour) < minimumDistance);
					if (allowed) {
						chosen.add(colour);

						if (chosen.size() >= colourCount) {
							break;
						}
					}
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}

			return chosen;
		}
	}

	private static class ColourRestrictor implements ImageTransform {
		private final Set<Color> allowedColours;

		public ColourRestrictor(final Set<Color> allowedColours) {
			this.allowedColours = ImmutableSet.copyOf(allowedColours);
		}

		@Override
		public BufferedImage apply(final BufferedImage img) {
			final BufferedImage ret = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

			for (int i = 0; i < img.getWidth(); i++)
				for (int j = 0; j < img.getHeight(); j++)
					ret.setRGB(i, j, mapColour(new Color(img.getRGB(i, j))).getRGB());

			return ret;
		}

		private Color mapColour(final Color colour) {
			Color closestColour = allowedColours.iterator().next();
			for (final Color currentColour : allowedColours) {
				if (distance(colour, closestColour) > distance(colour, currentColour)) {
					closestColour = currentColour;
				}
			}

			return closestColour;
		}
	}
}
