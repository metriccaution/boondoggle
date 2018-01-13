package com.github.metriccaution.boondoggle.compression.colours.histogram;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.github.metriccaution.boondoggle.compression.colours.ColourMapper;
import com.github.metriccaution.boondoggle.compression.colours.ColourRestriction;
import com.github.metriccaution.boondoggle.compression.colours.ColourUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.*;

/**
 * Very basic colour quantisation, just pick the most popular colours, with a
 * minimum distance between choices
 */
public class HistogramQuantisation implements ColourRestriction {

	private final int colourCount;
	private final int minimumDistance;

	public HistogramQuantisation(final int colourCount, final int minimumDistance) {
		this.colourCount = colourCount;
		this.minimumDistance = minimumDistance;
	}

	@Override
	public ColourMapper mapping(final Multiset<Color> colours) {
		final List<Color> mostCommonColours = mostCommonColours(colours);
		final Set<Color> histogramColours = mostCommonSpread(mostCommonColours, colourCount, minimumDistance);
		return new HistogramMapper(histogramColours);
	}

	private static List<Color> mostCommonColours(final Multiset<Color> colours) {
		final List<Color> sorted = Lists.newArrayList(colours.elementSet());
		Collections.sort(sorted, (a, b) -> {
			return colours.count(a) - colours.count(b);
		});
		return ImmutableList.copyOf(sorted);
	}

	private static Set<Color> mostCommonSpread(final List<Color> colours, final int colourCount, final int minimumDistance) {
		final Set<Color> ret = Sets.newHashSet();

		for (final Color colour : colours) {
			if (ret.size() == colourCount)
				break;

			if (wellSpaced(ret, colour, minimumDistance))
				ret.add(colour);
		}

		return ret;
	}

	private static boolean wellSpaced(final Collection<Color> targets, final Color colour, final int minimumDistance) {
		for (final Color target : targets)
			if (ColourUtils.distance(target, colour) < minimumDistance)
				return false;

		return true;
	}

	private static class HistogramMapper implements ColourMapper{
		private final Cache<Color, Color> mappingCache;
		private final Set<Color> colours;

		public HistogramMapper(final Set<Color> colours) {
			if (colours.size() == 0)
				throw new IllegalArgumentException("Colour histogram must have at least one colour");

			this.colours = ImmutableSet.copyOf(colours);
			mappingCache = CacheBuilder.newBuilder()
					.build();
		}

		@Override
		public Color map(final Color original) {
			try {
				return mappingCache.get(original, () -> {
					int minDistance = Integer.MAX_VALUE;
					Color closest = null;

					for (final Color colour : colours) {
						final int distance = ColourUtils.distance(colour, original);
						if (distance < minDistance) {
							minDistance = distance;
							closest = colour;
						}
					}

					return closest;
				});
			} catch (final ExecutionException e) {
				throw new IllegalStateException("Could not map colour", e);
			}
		}

	}

}
