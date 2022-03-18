package ru.nsu.fit.mazhuga;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @deprecated use {@link NodeParser#parseCompressed(java.lang.String)}
 */
@Deprecated
public class Decompressor {

    public static final int BUFFER_SIZE = 1024 * 1024 * 25;

    public void decompress(String inputPath, String outputPath) throws IOException {

        try (OutputStream outputStream = Files.newOutputStream(Paths.get(outputPath));
             BZip2CompressorInputStream compressorInputStream = new BZip2CompressorInputStream(
                     new BufferedInputStream(Files.newInputStream(Paths.get(inputPath))))) {

            final byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            while (-1 != (n = compressorInputStream.read(buffer))) {
                outputStream.write(buffer, 0, n);
            }
        }
    }
}
