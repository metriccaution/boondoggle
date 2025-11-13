from pathlib import Path

import pytest
from PIL import Image

from .image import ImageConfig, image_pallette_hex, writeable_image

image_dir = Path(Path(__file__).parent, "test_images")
image_files = (
    list(image_dir.glob("*.jpg"))
    + list(image_dir.glob("*.png"))
    + list(image_dir.glob("*.webp"))
)
image_files = sorted(str(i) for i in image_files)


@pytest.mark.parametrize("image_path", image_files)
def test_image_processing(image_path: Path):
    config = ImageConfig(colors=32, max_height=500, max_width=1000)

    with open(Path(image_dir, image_path), mode="rb") as f:
        output = writeable_image(f, config=config)

    assert output.size[0] <= config.max_width
    assert output.size[1] <= config.max_height
    assert len(output.palette.colors) == config.colors

    with open(Path(image_dir, image_path), mode="rb") as f:
        raw_width, raw_height = Image.open(f).size

    # Check the aspect ratio hasn't been altered
    assert float(raw_width) / raw_height == pytest.approx(
        float(output.size[0]) / output.size[1], 0.1
    )

    # Make sure images don't get scaled back up if they start small
    if raw_width < config.max_width and raw_height < config.max_height:
        assert output.size[0] == raw_width or output.size[1] == raw_height


@pytest.mark.parametrize(
    ("image_path", "hex_1", "hex_8"),
    zip(
        image_files,
        [["#9b4a31"], ["#9c4a32"], ["#9c4a32"], ["#9c4a32"], ["#3c515e"], ["#3d525f"]],
        [
            [
                "#fde0c9",
                "#fcb682",
                "#ef7028",
                "#b52406",
                "#920c04",
                "#630402",
                "#2f0603",
                "#0d0502",
            ],
            [
                "#fde0c9",
                "#fcb784",
                "#f1722b",
                "#b62706",
                "#940d04",
                "#650403",
                "#310603",
                "#0e0502",
            ],
            [
                "#fee0c9",
                "#fdb784",
                "#f1722b",
                "#b62705",
                "#930d03",
                "#640402",
                "#310603",
                "#0e0502",
            ],
            [
                "#fde0c9",
                "#fbb784",
                "#f0712b",
                "#b52707",
                "#930d04",
                "#640403",
                "#310603",
                "#0e0502",
            ],
            [
                "#899aa8",
                "#627989",
                "#46606e",
                "#344d5a",
                "#273f4c",
                "#203541",
                "#1a2d38",
                "#13212a",
            ],
            [
                "#899aa8",
                "#627a89",
                "#48616f",
                "#354e5b",
                "#29404d",
                "#213642",
                "#1b2e39",
                "#14222b",
            ],
        ],
        strict=True,
    ),
)
def test_hex_codes(image_path: Path, hex_1: list[str], hex_8: list[str]):
    with open(Path(image_dir, image_path), mode="rb") as f:
        img = writeable_image(f, config=ImageConfig(colors=1))
        assert image_pallette_hex(img) == hex_1

    with open(Path(image_dir, image_path), mode="rb") as f:
        img = writeable_image(f, config=ImageConfig(colors=8))
        assert image_pallette_hex(img) == hex_8
