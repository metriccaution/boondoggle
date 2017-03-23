package com.github.metriccaution.boondoggle.poi;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ImageConverter {

	public Workbook convert(final BufferedImage img) {
		final XSSFWorkbook ret = new XSSFWorkbook();
		final Sheet sheet = ret.createSheet("Img");

		final Map<Color, XSSFCellStyle> styleCache = new HashMap<>();

		final int resolution = 1;

		final int height = img.getHeight() / resolution;
		final int width = img.getWidth() / resolution;

		for (int i = 0; i < height; i++) {
			final Row row = sheet.createRow(i);
			row.setHeight((short) (20 * 5));
			for (int j = 0; j < width; j++) {
				final Cell cell = row.createCell(j);
				final Color cellColor = new Color(img.getRGB(j * resolution, i * resolution));

				if (!styleCache.containsKey(cellColor))
					styleCache.put(cellColor, createColor(ret, cellColor));

				final XSSFCellStyle style = styleCache.get(cellColor);

				cell.setCellStyle(style);
			}
		}

		for (int i = 0; i < width; i++) {
			sheet.setColumnWidth(i, 256);
		}

		return ret;
	}

	private XSSFCellStyle createColor(final XSSFWorkbook wb, final Color cellColor) {
		final XSSFCellStyle style = wb.createCellStyle();
		final XSSFColor color = new XSSFColor(cellColor);
		style.setFillForegroundColor(color);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		return style;
	}
}
