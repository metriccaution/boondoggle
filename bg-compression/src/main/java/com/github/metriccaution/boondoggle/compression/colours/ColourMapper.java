package com.github.metriccaution.boondoggle.compression.colours;

import java.awt.Color;

/**
 * Map from a full colour space into a restricted one
 */
@FunctionalInterface
public interface ColourMapper {
	Color map(Color original);
}
