package com.github.metriccaution.boondoggle.poi;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * Turn multiple images into a multi-sheet workbook
 */
public class MultiImageConverter {
	private static final Logger LOGGER = LoggerFactory.getLogger(MultiImageConverter.class);

	private final short PIXEL_SIZE = 1;

	public XSSFWorkbook convert(final Stream<ImageFile> images) {
		final ColourCache cache = new ColourCache();
		final XSSFWorkbook ret = new XSSFWorkbook();

		LOGGER.info("Started - Rendering to xlsx");
		images.forEach(image -> {
			LOGGER.info("Started - Sheet {} ({} x {})", image.getName(), image.getData().getWidth(), image.getData().getHeight());

			final Sheet sheet = ret.createSheet(image.getName());
			sheet.setZoom(20);
			imageToSheet(image.getData(), ret, sheet, cache);
			LOGGER.info("Finished - Sheet {}", image.getName());
		});
		LOGGER.info("Finished - Rendering to xlsx");

		return ret;
	}

	private void imageToSheet(final BufferedImage img, final XSSFWorkbook workbook, final Sheet sheet, final ColourCache colours) {

		sheet.setDefaultColumnWidth(PIXEL_SIZE);
		sheet.setDefaultRowHeight((short)(240 * PIXEL_SIZE));

		for (int i = 0; i < img.getHeight(); i++) {
			final Row row = sheet.createRow(i);
			for (int j = 0; j < img.getWidth(); j++) {
				if (i % 100 == 0 && j % 100 == 0)
					LOGGER.info("Written cell ({}, {})", i, j);

				final Cell cell = row.createCell(j);
				final Color cellColor = new Color(img.getRGB(j, i));
				final Optional<CellStyle> style = colours.createColor(workbook, cellColor);
				if (style.isPresent())
					cell.setCellStyle(style.get());

				// Add space to the bottom-right so Google Sheets won't crop the image
				if (i == img.getHeight() - 1 && j == img.getWidth() - 1)
					cell.setCellValue(" ");
			}
		}
	}

	private class ColourCache {
		private final Map<Color, CellStyle> styles = Maps.newHashMap();

		public Optional<CellStyle> createColor(final XSSFWorkbook wb, final Color cellColor) {
			if (cellColor.equals(Color.WHITE))
				return Optional.empty();

			if (styles.containsKey(cellColor))
				return Optional.of(styles.get(cellColor));

			final XSSFCellStyle style = wb.createCellStyle();
			final XSSFColor color = new XSSFColor(cellColor);
			style.setFillForegroundColor(color);
			style.setFillPattern(CellStyle.SOLID_FOREGROUND);

			styles.put(cellColor, style);

			return Optional.of(style);
		}
	}

}
