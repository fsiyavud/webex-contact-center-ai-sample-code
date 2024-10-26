package com.cisco.wccai.grpc.utils;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Properties;

public class Utils {

    private static final Properties PROPERTY = LoadProperties.getProperties();
    private static final String AUDIO_ENCODING_TYPE = PROPERTY.getProperty("AUDIO_ENCODING_TYPE");
    private static final int BUFFER_SIZE = Integer.parseInt(PROPERTY.getProperty("BUFFER_SIZE"));
    private static final String LINEAR_16 = "LINEAR_16";

    Utils()
    {

    }
    public static byte[] getAudioBytes() {
        byte[] audioBytes;
        if (LINEAR_16.equalsIgnoreCase(AUDIO_ENCODING_TYPE)) {
            audioBytes = new byte[16 * BUFFER_SIZE];
        } else {
            audioBytes = new byte[BUFFER_SIZE];
        }
        Arrays.fill(audioBytes, (byte) 1);
        return audioBytes;
    }

    public static int getWavHeaderSize(byte[] wavBytes) {
        /*
        Refer to https://www.mmsp.ece.mcgill.ca/Documents/AudioFormats/WAVE/WAVE.html
        for WAV file format details
         */
        if (wavBytes == null || wavBytes.length < 44) {
            // min WAV header size is 44 bytes
            return -1;
        }

        // check for 'RIFF' and 'WAVE' headers
        if (wavBytes[0] != 'R' || wavBytes[1] != 'I' || wavBytes[2] != 'F' || wavBytes[3] != 'F' ||
                wavBytes[8] != 'W' || wavBytes[9] != 'A' || wavBytes[10] != 'V' || wavBytes[11] != 'E') {
            return -1;
        }

        int offset = 12;
        while (offset + 8 <= wavBytes.length) {
            // read chunk ID (4 bytes)
            String chunkId = new String(wavBytes, offset, 4);
            offset += 4;

            // read chunk size (4 bytes, little-endian)
            int chunkSize = ByteBuffer.wrap(wavBytes, offset, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            offset += 4;

            if (offset + chunkSize > wavBytes.length) {
                // chunk size goes beyond the data length, invalid WAV file
                return -1;
            }

            if (chunkId.equals("fmt ")) {
                // skip 'fmt ' chunk data
                offset += chunkSize;
            } else if (chunkId.equals("data")) {
                // got 'data' chunk
                return offset;
            } else {
                // skip any other chunk data - 'fact' etc.
                offset += chunkSize;
            }

            // handle padding (chunks align to even byte boundaries)
            if (chunkSize % 2 == 1) {
                offset += 1;
            }
        }

        // 'data' chunk not found
        return -1;
    }

    public static InputStream getInputStreamForBookAFlight() {
        return Utils.class.getClassLoader().getResourceAsStream("audio/flightbook.wav");
    }

    public static InputStream getInputStreamForVaResponse() {
        return Utils.class.getClassLoader().getResourceAsStream("audio/va_response.wav");
    }
}
