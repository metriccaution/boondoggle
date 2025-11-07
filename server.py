from fastapi import FastAPI, Response, UploadFile
from PIL import Image
import xlsxwriter
from xlsxwriter.worksheet import Worksheet
import io

app = FastAPI(
    title="Boondoggle",
    description="Painting by numbers",
)


@app.post("/image")
async def convert_image(image_file: UploadFile):
    "Upload an image, get it back, but better"

    simplified = writeable_image(io.BytesIO(await image_file.read()))

    sheet_bytes = io.BytesIO()

    with xlsxwriter.Workbook(sheet_bytes) as workbook:
        sheet = workbook.add_worksheet("Masterpiece")
        write_to_sheet(workbook=workbook, sheet=sheet, image=simplified)

    return Response(
        content=sheet_bytes.getvalue(),
        headers={
            "Content-Type": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "Content-Disposition": 'attachment;filename="Masterpiece.xlsx"',
        },
    )


def writeable_image(xyz) -> Image.Image:
    "Do the normalisation required to make writing an image into Excel possible"

    colors = 64
    max_width = 500
    max_height = 500

    with Image.open(xyz) as actual:
        height_multiplier = min(max_height / actual.height, 1)
        width_multiplier = min(max_width / actual.width, 1)
        size_multiplier = min(height_multiplier, width_multiplier)

        (width, height) = (
            int(actual.width * size_multiplier),
            int(actual.height * size_multiplier),
        )

        return (
            actual.convert("RGB")
            .resize(
                (width, height),
            )
            .quantize(colors=colors)
        )


def write_to_sheet(workbook: xlsxwriter.Workbook, sheet: Worksheet, image: Image.Image):
    "Write this image into this sheet, assuming the sheet is blank."

    # Map the images colour palette to XLSX styles
    pallette = image.getpalette()
    styles = [
        workbook.add_format({"bg_color": f"#{r:02x}{g:02x}{b:02x}"})
        for r, g, b in zip(pallette[::3], pallette[1::3], pallette[2::3])
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
