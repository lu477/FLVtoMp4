package org.example;

import org.bytedeco.ffmpeg.avcodec.AVCodecParameters;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avformat.AVIOContext;
import org.bytedeco.ffmpeg.avformat.AVStream;
import org.bytedeco.ffmpeg.avutil.*;

import java.util.List;

import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avformat.*;
import static org.bytedeco.ffmpeg.global.avutil.*;


public class JavaCpp {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: JavaCpp <input_file>");
            System.exit(1);
        }
        String inputFilePath = args[0];
        String outputFilePath = "output.mp4";

        convertFLVToMP4(inputFilePath, outputFilePath);
    }

    public static void convertFLVToMP4(String inputPath, String outputPath) {

        AVFormatContext inputFormatContext = new AVFormatContext(null);
        AVFormatContext outputFormatContext = new AVFormatContext(null);

        avformat_open_input(inputFormatContext, inputPath, null, null);
        avformat_find_stream_info(inputFormatContext, (AVDictionary) null);
        avformat_alloc_output_context2(outputFormatContext, null, null, outputPath);

        for (int i = 0; i < inputFormatContext.nb_streams(); i++) {

            AVStream inStream = inputFormatContext.streams(i);
            AVStream outStream = avformat_new_stream(outputFormatContext, null);
            AVCodecParameters inCodecParameters = inStream.codecpar();

            avcodec_parameters_copy(outStream.codecpar(), inCodecParameters);
            outStream.codecpar().codec_tag(0);
        }

        AVIOContext ioContext = new AVIOContext(null);

        avio_open(ioContext, outputPath, AVIO_FLAG_WRITE);

        outputFormatContext.pb(ioContext);

        avformat_write_header(outputFormatContext, (AVDictionary) null);

        AVPacket packet = new AVPacket();
        while (av_read_frame(inputFormatContext, packet) >= 0) {
            AVStream inStream = inputFormatContext.streams(packet.stream_index());
            AVStream outStream = outputFormatContext.streams(packet.stream_index());

            packet.pts(av_rescale_q(packet.pts(), inStream.time_base(), outStream.time_base()));
            packet.dts(av_rescale_q(packet.dts(), inStream.time_base(), outStream.time_base()));
            packet.duration(av_rescale_q(packet.duration(), inStream.time_base(), outStream.time_base()));

            av_interleaved_write_frame(outputFormatContext, packet);
            av_packet_unref(packet);
        }

        av_write_trailer(outputFormatContext);
        avformat_close_input(inputFormatContext);
        avio_closep(outputFormatContext.pb());
        avformat_free_context(outputFormatContext);

        System.out.println("Conversion completed.");
    }
}
