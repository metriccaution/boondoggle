package com.github.metriccaution.boondoggle.web;

import static spark.Spark.*;

import java.awt.image.BufferedImage;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.github.metriccaution.boondoggle.compression.inPlace.colours.ColourSpaceRestriction;
import com.github.metriccaution.boondoggle.compression.inPlace.resize.ImageSizeLimiter;
import com.github.metriccaution.boondoggle.poi.ImageFile;
import com.github.metriccaution.boondoggle.poi.MultiImageConverter;

public class Main {

	public static void main(final String[] args) {
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
			res.raw().setHeader("Content-Disposition", "attachment; filename=mirrored.png");

			final Part file = req.raw().getPart("image_upload");

			// Cut down image size
			final Function<BufferedImage, BufferedImage> imageMap = new ImageSizeLimiter(500, 500)
					.andThen(new ColourSpaceRestriction(128, 25));
			final BufferedImage uploadedEmail = imageMap.apply(ImageIO.read(file.getInputStream()));

			final ImageFile image = new ImageFile("uploaded-file", uploadedEmail);
			final MultiImageConverter converter = new MultiImageConverter();
			try (XSSFWorkbook workbook = converter.convert(Stream.of(image))) {
				workbook.write(res.raw().getOutputStream());
			}

			return null;
		});

		init();
	}

}
