package com.github.metriccaution.boondoggle.compression.colours;

import java.awt.Color;

import com.google.common.collect.Multiset;

/**
 * Provides a colour mapping from the colours present in one or more images
 */
@FunctionalInterface
public interface ColourRestriction {

	ColourMapper mapping(Multiset<Color> colours);

}
