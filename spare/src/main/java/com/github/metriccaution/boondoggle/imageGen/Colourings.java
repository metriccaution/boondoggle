package com.github.metriccaution.boondoggle.imageGen;

import java.awt.Color;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Colourings {

	public static ColourMapping of(final Color colour) {
		final int rgb = colour.getRGB();
		return (x, y) -> rgb;
	}

	public static ColourMapping checked(final ColourMapping colour1, final ColourMapping colour2) {
		return (x, y) -> {
			return (x + y) % 2 == 0 ? colour1.apply(x, y) : colour2.apply(x, y);
		};
	}

	public static ColourMapping striped(final ColourMapping... colours) {
		return (x, y) -> {
			return colours[x % colours.length].apply(x, y);
		};
	}

	public static ColourMapping progression(final ColourMapping... colours) {
		final Supplier<Integer> counter = new Supplier<Integer>() {
			private int count = 0;

			@Override
			public Integer get() {
				return count++;
			}
		};

		return (x, y) -> {
			return colours[counter.get() % colours.length].apply(x, y);
		};
	}

	public static ColourMapping nColours(final int n) {
		final Set<Color> colours = new HashSet<>();
		while (colours.size() < n) {
			colours.add(new Color(new Random().nextInt(255 * 255 * 255)));
		}

		final ColourMapping[] mappings = colours.stream()
				.map(Colourings::of)
				.collect(Collectors.toList())
				.toArray(new ColourMapping[0]);

		return progression(mappings);
	}

	private Colourings() {
	}

}
