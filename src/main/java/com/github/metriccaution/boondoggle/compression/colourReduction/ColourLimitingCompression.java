package com.github.metriccaution.boondoggle.compression.colourReduction;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

import com.github.metriccaution.boondoggle.compression.ImageCompression;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ColourLimitingCompression implements ImageCompression {

	private final int maxColours;

	public ColourLimitingCompression(final int maxColours) {
		this.maxColours = maxColours;
	}

	@Override
	public BufferedImage compress(final BufferedImage source) {
		final Set<ClusterableColour> colours = colours(source);
		if (colours.size() <= maxColours)
			return source;

		final KMeansPlusPlusClusterer<ClusterableColour> clusterer = new KMeansPlusPlusClusterer<>(maxColours);
		final List<CentroidCluster<ClusterableColour>> clusters = clusterer.cluster(colours);
		final Map<Color, Color> colourMapping = mapColours(clusters);
		return restrictColours(source, colourMapping);
	}

	private Set<ClusterableColour> colours(final BufferedImage img) {
		final Set<Color> colours = Sets.newHashSet();
		for (int i = 0; i < img.getWidth(); i++)
			for (int j = 0; j < img.getHeight(); j++)
				colours.add(new Color(img.getRGB(i, j)));

		return colours.stream()
				.map(c -> new ClusterableColour(c))
				.collect(Collectors.toSet());
	}

	private Map<Color, Color> mapColours(final List<CentroidCluster<ClusterableColour>> clusters) {
		final Map<Color, Color> colourMapping = Maps.newHashMap();
		for (final CentroidCluster<ClusterableColour> cluster : clusters) {
			final double[] point = cluster.getCenter().getPoint();
			final Color mapped = new Color((int) point[0], (int) point[1], (int) point[2]);
			for (final ClusterableColour colour : cluster.getPoints()){
				colourMapping.put(colour.getColour(), mapped);
			}
		}
		return colourMapping;
	}

	private BufferedImage restrictColours(final BufferedImage source, final Map<Color, Color> mapping) {
		final BufferedImage img = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < source.getWidth(); i++) {
			for (int j = 0; j < source.getHeight(); j++) {
				img.setRGB(i, j, mapping.get(new Color(source.getRGB(i, j))).getRGB());
			}
		}
		return img;
	}

	private static class ClusterableColour implements Clusterable {

		private final Color colour;

		public ClusterableColour(final Color colour) {
			this.colour = colour;
		}

		public Color getColour() {
			return colour;
		}

		@Override
		public double[] getPoint() {
			return new double[] { colour.getRed(), colour.getGreen(), colour.getBlue() };
		}

	}

}
