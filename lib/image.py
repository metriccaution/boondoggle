"Image manipulation code."

from typing import IO, Optional

from PIL import Image
from pydantic import BaseModel


class ImageConfig(BaseModel):
    "Config for how we restrict the input image."

    colors: int = 64
    max_height: int = 500
    max_width: int = 500


def writeable_image(
    data: IO[bytes],
    config: Optional[ImageConfig] = None,
) -> Image.Image:
    "Do the normalisation required to make writing an image into Excel possible"

    if config is None:
        config = ImageConfig()

    with Image.open(data) as actual:
        height_multiplier = min(config.max_height / actual.height, 1)
        width_multiplier = min(config.max_width / actual.width, 1)
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
            .quantize(colors=config.colors)
        )


def image_pallette_hex(image: Image.Image) -> list[str]:
    "Get the content of an image's pallette as a list of hex codes."

    pallette = image.getpalette("RGB")

    return [
        f"#{r:02x}{g:02x}{b:02x}"
        for r, g, b in zip(pallette[::3], pallette[1::3], pallette[2::3], strict=True)
    ]
