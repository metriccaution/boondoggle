"Point this at a directory, and it'll convert the contents to a single workbook."

from pathlib import Path
from sys import argv

import xlsxwriter

from lib.image import writeable_image
from lib.xlsx import write_to_sheet

if __name__ == "__main__":
    if len(argv) < 2:
        error = "Pass in an input directory name as an argument"
        raise ValueError(error)
    if len(argv) < 3:
        error = "Pass in an output file name as an argument"
        raise ValueError(error)

    image_directory = Path(argv[1])
    output_file = Path(argv[2])

    if not image_directory.exists():
        error = f"{image_directory.absolute()} doesn't exist"
        raise ValueError(error)

    if not image_directory.is_dir():
        error = f"{image_directory.absolute()} isn't a directory"
        raise ValueError(error)

    with open(output_file, mode="wb") as f, xlsxwriter.Workbook(f) as workbook:
        for file in image_directory.iterdir():
            if not file.is_file():
                continue

            if file.suffix not in [".jpg", ".png", ".webp"]:
                continue

            sheet = workbook.add_worksheet(file.name[:31])
            with open(file, mode="rb") as img:
                write_to_sheet(
                    workbook=workbook,
                    sheet=sheet,
                    image=writeable_image(img),
                )
