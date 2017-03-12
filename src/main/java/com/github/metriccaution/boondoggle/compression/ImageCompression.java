package com.github.metriccaution.boondoggle.compression;

import java.awt.image.BufferedImage;

@FunctionalInterface
public interface ImageCompression {

	public BufferedImage compress(BufferedImage source);

	public default ImageCompression then(final ImageCompression other) {
		return (img) -> other.compress(this.compress(img));
	}

}
