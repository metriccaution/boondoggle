package com.github.metriccaution.boondoggle;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import com.github.metriccaution.boondoggle.compression.colourReduction.ColourLimitingCompression;
import com.github.metriccaution.boondoggle.compression.sizeReduction.SizeLimitingCompression;
import com.github.metriccaution.boondoggle.poi.ImageConverter;

public class Main {

	public static void main(final String[] args) throws Exception {
		final Configuration config = new Configuration(
				Paths.get(System.getProperty("user.home"), "Documents", "boondoggle", "IMG_20160525_124119194.jpg"),
				Paths.get(System.getProperty("user.home"), "Documents", "boondoggle", "crude_etching.xlsx"),
				50,
				1280 * 720);

		final BufferedImage img = new SizeLimitingCompression(config.getMaxPixels())
				.then(new ColourLimitingCompression(config.getMaxColours()))
				.compress(ImageIO.read(config.getImage().toFile()));

		new ImageConverter().convert(img).write(new FileOutputStream(config.getOut().toFile()));
	}

}
