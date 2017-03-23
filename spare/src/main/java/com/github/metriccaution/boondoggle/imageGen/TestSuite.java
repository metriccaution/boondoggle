package com.github.metriccaution.boondoggle.imageGen;

import static com.github.metriccaution.boondoggle.imageGen.Colourings.nColours;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.metriccaution.boondoggle.poi.ImageConverter;

public class TestSuite {

	private final Path directory;
	private final Collection<TestCase> tests;

	public TestSuite(final Path directory, final Collection<TestCase> tests) {
		this.directory = directory;
		this.tests = Collections.unmodifiableList(new ArrayList<>(tests));
	}

	private Path imageDir() {
		return directory.resolve("images");
	}

	private Path excelDir() {
		return directory.resolve("excel");
	}

	public void generate() {
		try {
			if (Files.exists(directory)) {
				// Can't continue in this case
				throw new IllegalStateException("There's a file where I want to put a directory");
			}

			final Path imageDirectory = imageDir();

			Files.createDirectory(directory);
			Files.createDirectory(excelDir());
			Files.createDirectory(imageDirectory);

			for (final TestCase test : tests) {
				final BufferedImage img = Generator.create(test.getWidth(), test.getHeight(),
						nColours(test.getColourCount()));
				ImageIO.write(img, "png", new File(imageDirectory.toFile(), test.getId() + ".png"));
			}

			// Write out test metadata
			new ObjectMapper().writeValue(new File(directory.toFile(), "images.json"), tests);
		} catch (final IOException e) {
			throw new IllegalStateException("Failed to set up tests", e);
		}

	}

	public void run() {
		try {
			final Path imageDir = imageDir();
			final Path excelDir = excelDir();
			final ImageConverter imageConverter = new ImageConverter();
			final List<TestResult> results = new ArrayList<>();

			Files.walk(imageDir)
			.filter(p -> p.toString().endsWith("png"))
			.forEach(p -> {
				try {
					final String id = p.getFileName().toString().substring(0, p.getFileName().toString().length() - 4);
					final Path out = excelDir.resolve(id + ".xlsx");
					final long start = System.currentTimeMillis();
					imageConverter.convert(ImageIO.read(p.toFile())).write(new FileOutputStream(out.toFile()));
					final long stop = System.currentTimeMillis();
					results.add(new TestResult(id, stop - start));

				} catch (final IOException e) {
					throw new IllegalStateException("Could not run a test", e);
				}
			});

			// Write out test results
			new ObjectMapper().writeValue(new File(directory.toFile(), "results.json"), results);
		} catch (final IOException e) {
			throw new IllegalStateException("Could not run tests", e);
		}
	}

	public void collateResults() throws Exception {
		// TODO
		/*
		 * Get cases
		 * Get results
		 * Join on ID
		 * Write out csv
		 */
		final ObjectMapper mapper = new ObjectMapper();
		final TypeReference<List<TestCase>> caseType = new TypeReference<List<TestCase>>() {};
		final TypeReference<List<TestResult>> resultsType = new TypeReference<List<TestResult>>() {};

		final List<TestCase> cases = mapper.readValue(new File(directory.toFile(), "images.json"), caseType);
		final List<TestResult> results = mapper.readValue(new File(directory.toFile(), "results.json"), resultsType);

		final Map<String, TestCase> casesById = new HashMap<>();
		for (final TestCase test : cases)
			casesById.put(test.getId(), test);


		final Map<String, TestResult> resultsById = new HashMap<>();
		for (final TestResult result : results)
			resultsById.put(result.getId(), result);

		final StringBuilder sb = new StringBuilder();
		sb.append("ID,Width,Height,Colour Count,Time\n");

		final Map<Long, RunningAverage> areaToTime = new HashMap<>();
		final Map<Integer, RunningAverage> coloursToTime = new HashMap<>();

		for (final String id : casesById.keySet()) {
			final TestCase test = casesById.get(id);
			final TestResult result = resultsById.get(id);

			final long area = test.getWidth() * test.getHeight();
			if (!areaToTime.containsKey(area))
				areaToTime.put(area, new RunningAverage());
			areaToTime.get(area).increment(result.getTime());

			if (!coloursToTime.containsKey(test.getColourCount()))
				coloursToTime.put(test.getColourCount(), new RunningAverage());
			coloursToTime.get(test.getColourCount()).increment(result.getTime());


			sb.append(id).append(",")
			.append(test.getWidth()).append(",")
			.append(test.getHeight()).append(",")
			.append(test.getColourCount()).append(",")
			.append(result.getTime()).append(",")
			.append("\n");
		}

		Files.write(directory.resolve("results.csv"), sb.toString().getBytes(Charset.defaultCharset()));

		final StringBuilder area = new StringBuilder();
		area.append("Area,Time\n");
		for (final long item : areaToTime.keySet())
			area.append(item).append(",")
			.append(areaToTime.get(item).average())
			.append("\n");
		Files.write(directory.resolve("areas.csv"), area.toString().getBytes(Charset.defaultCharset()));

		final StringBuilder colours = new StringBuilder();
		colours.append("Colour Count,Time\n");
		for (final int item : coloursToTime.keySet())
			colours.append(item).append(",")
			.append(coloursToTime.get(item).average())
			.append("\n");
		Files.write(directory.resolve("colours.csv"), colours.toString().getBytes(Charset.defaultCharset()));
	}

	public static class TestCase {
		private final String id;
		private final int width;
		private final int height;
		private final int colourCount;

		public TestCase(final int width, final int height, final int colourCount) {
			this(UUID.randomUUID().toString(), width, height, colourCount);
		}

		@JsonCreator
		public TestCase(
				@JsonProperty("id") final String id,
				@JsonProperty("width") final int width,
				@JsonProperty("height") final int height,
				@JsonProperty("colourCount") final int colourCount) {
			this.id = id;
			this.width = width;
			this.height = height;
			this.colourCount = colourCount;
		}

		public String getId() {
			return id;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public int getColourCount() {
			return colourCount;
		}
	}

	public static class TestResult {

		private final String id;
		private final long time;

		@JsonCreator
		public TestResult(
				@JsonProperty("id") final String id,
				@JsonProperty("time") final long time) {
			this.id = id;
			this.time = time;
		}

		public String getId() {
			return id;
		}

		public long getTime() {
			return time;
		}

	}

	public static class RunningAverage {
		private long total;
		private long count;

		public RunningAverage() {
			total = 0;
			count = 0;
		}

		public void increment(final long item) {
			total += item;
			count++;
		}

		public double average() {
			return (double) total / count;
		}

	}

}
