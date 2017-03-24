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

}
