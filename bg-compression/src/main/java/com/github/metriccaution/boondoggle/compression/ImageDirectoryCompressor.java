package com.github.metriccaution.boondoggle.compression;

import java.nio.file.Path;
import java.util.stream.Stream;

import com.github.metriccaution.boondoggle.ImageFile;

/**
 * Compress a directory of images
 * <p>
 * The awful API is so that the set of images can be dealt with sequentially if
 * possible, and as a whole if required
 */
public interface ImageDirectoryCompressor {

	Stream<ImageFile> process(Stream<Path> images);

}
