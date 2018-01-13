package com.github.metriccaution.boondoggle.compression;

import java.awt.image.BufferedImage;
import java.util.function.Function;

/**
 * Transforms one image into another, typically as some sort of compression
 */
@FunctionalInterface
public interface ImageTransform extends Function<BufferedImage, BufferedImage> {
}
