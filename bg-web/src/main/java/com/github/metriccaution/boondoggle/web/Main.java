package com.github.metriccaution.boondoggle.web;

import static spark.Spark.*;

import java.awt.image.BufferedImage;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.metriccaution.boondoggle.compression.colours.ColourSpaceRestriction;
import com.github.metriccaution.boondoggle.compression.resize.ImageSizeLimiter;
import com.github.metriccaution.boondoggle.poi.ImageFile;
import com.github.metriccaution.boondoggle.poi.MultiImageConverter;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(final String[] args) {
		configureLogger();

		port(9731);
		staticFileLocation("/site");

		// Jetty stuff
		before("/api/upload", (req, res) -> {
			if (req.raw().getAttribute("org.eclipse.jetty.multipartConfig") == null) {
				final MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
						System.getProperty("java.io.tmpdir"));
				req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
			}
		});

		// Accept images, return Excel file downloads
		post("/api/upload", (req, res) -> {
			res.raw().setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			res.raw().setHeader("Content-Disposition", "attachment; filename=mirrored.xlsx");

			final Part file = req.raw().getPart("image_upload");
			LOGGER.info("Serving image request, size {} bytes", file.getSize());

			// Cut down image size
			final Function<BufferedImage, BufferedImage> imageMap = new ImageSizeLimiter(500, 500)
					.andThen(new ColourSpaceRestriction(128, 25));

			final BufferedImage originalImage = ImageIO.read(file.getInputStream());
			final BufferedImage uploadedImage = imageMap.apply(originalImage);

			final ImageFile image = new ImageFile("uploaded-file", uploadedImage);
			final MultiImageConverter converter = new MultiImageConverter();
			try (XSSFWorkbook workbook = converter.convert(Stream.of(image))) {
				workbook.write(res.raw().getOutputStream());
			}

			return null;
		});

		// Handle application exceptions
		exception(Exception.class, (e, req, res) -> {
			e.printStackTrace();
			res.redirect("/?error=true");
		});

		init();
	}

	private static void configureLogger() {
		System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
		System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
	}

}
