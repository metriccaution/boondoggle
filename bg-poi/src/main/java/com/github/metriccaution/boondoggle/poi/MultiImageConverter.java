package com.github.metriccaution.boondoggle.poi;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Map;
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

	public XSSFWorkbook convert(final Stream<ImageFile> images) {
		final ColourCache cache = new ColourCache();
		final XSSFWorkbook ret = new XSSFWorkbook();

		images.forEach(image -> {
			System.out.println("Starting sheet " + image.getName() + ", (" + image.getData().getWidth() + "x" + image.getData().getHeight() + ")");
			final Sheet sheet = ret.createSheet(image.getName());
			sheet.setZoom(10);
			imageToSheet(image.getData(), ret, sheet, cache);
			System.out.println("Finished sheet " + image.getName());
		});

		return ret;
	}

	private void imageToSheet(final BufferedImage img, final XSSFWorkbook workbook, final Sheet sheet, final ColourCache colours) {
		final int height = img.getHeight();
		final int width = img.getWidth();

		for (int i = 0; i < height; i++) {
			final Row row = sheet.createRow(i);
			row.setHeight((short) (20 * 5));
			for (int j = 0; j < width; j++) {
				if (i % 100 == 0 && j % 100 == 0)
					System.out.println("Written cell (" + i + ", " + j + ")");

				final Cell cell = row.createCell(j);
				final Color cellColor = new Color(img.getRGB(j, i));
				final CellStyle style = colours.createColor(workbook, cellColor);
				cell.setCellStyle(style);
			}
		}

		for (int i = 0; i < width; i++) {
			sheet.setColumnWidth(i, 256);
		}
	}

	private class ColourCache {
		private final Map<Color, CellStyle> styles = Maps.newHashMap();

		public CellStyle createColor(final XSSFWorkbook wb, final Color cellColor) {
			if (styles.containsKey(cellColor))
				return styles.get(cellColor);

			final XSSFCellStyle style = wb.createCellStyle();
			final XSSFColor color = new XSSFColor(cellColor);
			style.setFillForegroundColor(color);
			style.setFillPattern(CellStyle.SOLID_FOREGROUND);

			styles.put(cellColor, style);

			return style;
		}
	}

}
