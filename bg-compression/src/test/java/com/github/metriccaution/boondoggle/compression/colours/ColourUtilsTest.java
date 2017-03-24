package com.github.metriccaution.boondoggle.compression.colours;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

public class ColourUtilsTest {

	@Test
	public void range_noColours() {
		assertEquals(ColourUtils.range(Sets.newHashSet(), Color::getRed), 0);
	}

	@Test
	public void range_oneColour() {
		assertEquals(ColourUtils.range(Sets.newHashSet(new Color(50, 50, 50)), Color::getRed), 0);
	}

	@Test
	public void range_twoColours() {
		assertEquals(
				ColourUtils.range(Sets.newHashSet(new Color(50, 100, 100), new Color(100, 100, 100)), Color::getRed),
				50);
	}

	@Test
	public void range_differentDimensions() {
		final Set<Color> colours = Sets.newHashSet(new Color(50, 0, 10), new Color(100, 100, 10),
				new Color(75, 50, 10));
		assertEquals(ColourUtils.range(colours, Color::getRed), 50);
		assertEquals(ColourUtils.range(colours, Color::getGreen), 100);
		assertEquals(ColourUtils.range(colours, Color::getBlue), 0);
	}

	@Test(expected = IllegalStateException.class)
	public void median_noColours() {
		ColourUtils.median(HashMultiset.create(), Color::getRed);
	}

	@Test
	public void median_singleColour() {
		final Multiset<Color> data = HashMultiset.create();
		data.add(Color.GREEN);
		assertEquals(ColourUtils.median(data, Color::getRed), Color.GREEN);
	}

	@Test
	public void median_repeatedColour() {
		final Multiset<Color> data = HashMultiset.create();
		data.add(Color.GREEN);
		data.add(Color.GREEN);
		data.add(Color.GREEN);
		data.add(Color.GREEN);
		assertEquals(ColourUtils.median(data, Color::getRed), Color.GREEN);
	}

	@Test
	public void median_twoColour() {
		final Multiset<Color> data = HashMultiset.create();
		data.add(Color.GREEN);
		data.add(Color.RED);
		assertEquals(ColourUtils.median(data, Color::getRed), Color.GREEN);

		data.add(Color.RED);
		assertEquals(ColourUtils.median(data, Color::getRed), Color.GREEN);

		data.add(Color.RED);
		assertEquals(ColourUtils.median(data, Color::getRed), Color.RED);

		data.add(Color.GREEN);
		assertEquals(ColourUtils.median(data, Color::getRed), Color.GREEN);
	}

}
