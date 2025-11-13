import xlsxwriter
from PIL import Image
from xlsxwriter.worksheet import Worksheet

from .image import image_pallette_hex


def write_to_sheet(workbook: xlsxwriter.Workbook, sheet: Worksheet, image: Image.Image):
    "Write this image into this sheet, assuming the sheet is blank."

    # Map the images colour palette to XLSX styles
    styles = [
        workbook.add_format({"bg_color": hex_code})
        for hex_code in image_pallette_hex(image)
    ]

    # Square cells please
    sheet.set_column_pixels(0, image.width, width=5)
    for i in range(image.height):
        sheet.set_row_pixels(row=i, height=5)

    # Do some drawing
    for i in range(image.width):
        for j in range(image.height):
            sheet.write(
                j,
                i,
                "",
                styles[image.getpixel((i, j))],
            )

    # Put some text in the last cell for Google Sheets compatibility
    sheet.write(image.height, image.width, ".")
