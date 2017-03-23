package com.github.metriccaution.boondoggle.imageGen;

import java.util.function.BiFunction;

@FunctionalInterface
public interface ColourMapping extends BiFunction<Integer, Integer, Integer> {
}
