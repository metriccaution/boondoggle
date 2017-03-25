package com.github.metriccaution.boondoggle.compression.colours;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;

import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

public class ColourUtils {

	private ColourUtils() {
	}

	public static int range(final Set<Color> colours, final ToIntFunction<Color> dimension) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;

		for (final Color colour : colours) {
			min = Math.min(min, dimension.applyAsInt(colour));
			max = Math.max(max, dimension.applyAsInt(colour));
		}

		if (min > max)
			return 0;

		return max - min;
	}

	public static ToIntFunction<Color> longestDirection(final Set<Color> colours, final Set<ToIntFunction<Color>> directions) {
		int longest = Integer.MIN_VALUE;
		ToIntFunction<Color> longestDirection = null;

		for (final ToIntFunction<Color> direction : directions) {
			final int length = range(colours, direction);

			if (length > longest) {
				longest = length;
				longestDirection = direction;
			}
		}

		if (longestDirection == null)
			throw new IllegalArgumentException("Could not calculate length");

		return longestDirection;
	}

	public static Color median(final Multiset<Color> colours, final ToIntFunction<Color> dimension) {
		final int halfway = colours.size() / 2;

		final List<Color> sortedColours = Lists.newArrayList(colours.elementSet());
		Collections.sort(sortedColours, (a, b) -> dimension.applyAsInt(a) - dimension.applyAsInt(b));

		int seen = 0;
		for (final Color colour : sortedColours) {
			seen += colours.count(colour);
			if (seen >= halfway)
				return colour;
		}

		throw new IllegalStateException("Did not pass the halfway point in calculating median colour");
	}

	public static int distance(final Color a, final Color b) {
		final double red = Math.pow(a.getRed() - b.getRed(), 2);
		final double green = Math.pow(a.getGreen() - b.getGreen(), 2);
		final double blue = Math.pow(a.getBlue() - b.getBlue(), 2);
		return (int) Math.pow(red + green + blue, 0.5);
	}

}
