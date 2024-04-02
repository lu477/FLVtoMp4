package org.example;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;

public class Main {
    public static void convertWithoutTranscoding(String inputPath, String outputPath) throws Exception {

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputPath);
        grabber.start();

        if (grabber.getVideoCodec() != avcodec.AV_CODEC_ID_H264) {
            System.err.println("Error: The input file is not encoded in H.264 format.");
            grabber.stop();
            return;
        }

        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
        recorder.setFormat("mp4");
        recorder.setVideoCodec(grabber.getVideoCodec());
        recorder.setAudioCodec(grabber.getAudioCodec());
        recorder.start();

        while (grabber.grabFrame() != null) {
            recorder.record(grabber.grabFrame());
        }

        recorder.stop();
        grabber.stop();

        System.out.println("Conversion completed successfully.");
    }

    public static void main(String[] args) {
        try {
            convertWithoutTranscoding("input.flv", "output.mp4");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
