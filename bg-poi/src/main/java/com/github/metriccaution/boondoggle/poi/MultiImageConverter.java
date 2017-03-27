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

import com.google.common.collect.Maps;

/**
 * Turn multiple images into a multi-sheet workbook
 */
public class MultiImageConverter {

	private final short PIXEL_SIZE = 1;

	public XSSFWorkbook convert(final Stream<ImageFile> images) {
		final ColourCache cache = new ColourCache();
		final XSSFWorkbook ret = new XSSFWorkbook();

		images.forEach(image -> {
			System.out.println("Starting sheet " + image.getName() + ", (" + image.getData().getWidth() + "x" + image.getData().getHeight() + ")");
			final Sheet sheet = ret.createSheet(image.getName());
			sheet.setZoom(20);
			imageToSheet(image.getData(), ret, sheet, cache);
			System.out.println("Finished sheet " + image.getName());
		});

		System.out.println("Finished rendering image to excel");

		return ret;
	}

	private void imageToSheet(final BufferedImage img, final XSSFWorkbook workbook, final Sheet sheet, final ColourCache colours) {

		sheet.setDefaultColumnWidth(PIXEL_SIZE);
		sheet.setDefaultRowHeight((short)(240 * PIXEL_SIZE));

		for (int i = 0; i < img.getHeight(); i++) {
			final Row row = sheet.createRow(i);
			for (int j = 0; j < img.getWidth(); j++) {
				if (i % 100 == 0 && j % 100 == 0)
					System.out.println("Written cell (" + i + ", " + j + ")");

				final Cell cell = row.createCell(j);
				final Color cellColor = new Color(img.getRGB(j, i));
				final Optional<CellStyle> style = colours.createColor(workbook, cellColor);
				if (style.isPresent())
					cell.setCellStyle(style.get());
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
