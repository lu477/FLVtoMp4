package org.example;

import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.global.avformat;
import org.bytedeco.javacpp.*;

public class JavaCpp {

    public static void main(String[] args) {

        String inputFilePath = "test.flv";
        String outputFilePath = "output.mp4";

        convertFLVToMP4(inputFilePath, outputFilePath);
    }

    public static void convertFLVToMP4(String inputPath, String outputPath) {

        AVFormatContext inputFormatContext = new AVFormatContext(null);
        AVFormatContext outputFormatContext = new AVFormatContext(null);

        if (avformat.avformat_open_input(inputFormatContext, inputPath, null, null) < 0) {
            throw new RuntimeException("Failed to open input file: " + inputPath);
        }

        if (avformat.avformat_find_stream_info(inputFormatContext, (PointerPointer)null) < 0) {
            throw new RuntimeException("Failed to find stream information.");
        }

        if (avformat.avformat_alloc_output_context2(outputFormatContext, null, null, outputPath) < 0) {
            throw new RuntimeException("Failed to allocate output context.");
        }

        avformat.avformat_close_input(inputFormatContext);
        avformat.avformat_free_context(outputFormatContext);

        System.out.println("Conversion completed.");
    }
}