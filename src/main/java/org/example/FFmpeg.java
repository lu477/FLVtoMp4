package org.example;

import org.bytedeco.javacpp.Loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class FFmpeg {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Please add 'PathToInputFile.flv','PathToOutputFile.mp4' to the script launch arguments");
        }
        String inputFilePath = args[0];
        String outputFilePath = args[1];

        try {
            Loader.load(org.bytedeco.javacpp.presets.javacpp.class);
        } catch (final Throwable t) {
            System.out.println("Problem loading javacpp ," + t.getMessage());
        }

        convertWithoutTranscoding(inputFilePath,outputFilePath);
        System.out.println("Conversion completed.");
    }

    public static void convertWithoutTranscoding(String inputFilePath, String outputFilePath) {

        String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);

        String transcodeFlvPath = "flvh265.flv";

        ProcessBuilder transcode = new ProcessBuilder(ffmpeg, "-i", inputFilePath, "-vcodec", "h264", transcodeFlvPath);

        ProcessBuilder convert = new ProcessBuilder(ffmpeg, "-i",isH264Encoded(inputFilePath) ? inputFilePath : transcodeFlvPath, "-c:v", "copy", "-c:a", "copy", outputFilePath);

        if (!isH264Encoded(inputFilePath)) {
            System.out.println("Input file is not encoded in H.264 format. Converting codec ...");
            try {
                transcode.inheritIO().start().waitFor();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        try {
            convert.inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static boolean isH264Encoded(String inputPath) {
        try {

            ProcessBuilder ffmpegProcessBuilder = new ProcessBuilder("ffmpeg", "-i", inputPath);

            Process ffmpegProcess = ffmpegProcessBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(ffmpegProcess.getErrorStream()));

            String line;

            while ((line = reader.readLine()) != null) {
                // Check if the output contains information about H.264 codec
                if (line.contains("Video: h264")) {
                    return true;
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
