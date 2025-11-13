import io

import xlsxwriter
from fastapi import FastAPI, Response, UploadFile

from lib.image import writeable_image
from lib.xlsx import write_to_sheet

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
