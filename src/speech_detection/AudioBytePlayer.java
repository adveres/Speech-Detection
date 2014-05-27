package speech_detection;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * A class that runs as a thread to play sound.
 * 
 * @author adveres
 * 
 */
public class AudioBytePlayer implements Runnable {
    SourceDataLine line;
    Thread thread;
    byte[] audioBytes;

    public AudioBytePlayer(byte[] audioData) {
        this.audioBytes = audioData;
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        thread = null;
    }

    public void shutdown(String message) {
        if (message != null) {
            System.err.println(message);
        }

        stop();
    }

    public void run() {

        if (audioBytes == null) {
            shutdown("No audio bytes to play");
            return;
        }

        AudioFormat format = Utils.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            shutdown("Line matching " + info + " not supported.");
            return;
        }

        // get and open the source data line for playback.

        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, audioBytes.length);
        } catch (LineUnavailableException ex) {
            shutdown("Unable to open the line: " + ex);
            return;
        }

        // start the source data line
        line.start();

        int bytesRead = 0;
        int offset = 0;
        int x = 0;
        while (thread != null && bytesRead < audioBytes.length) {
            try {
                offset = x * Utils.CHUNK_OF_10MS;
                line.write(audioBytes, offset, Utils.CHUNK_OF_10MS);

                if(!OSUtils.isWindows()){
                    if (x % 80 == 0) {
                        // Had a problem on OSX writing out 10ms chunks, or the
                        // whole array, where it would truncate the sound.
                        // Draining the line periodically fixes it.
                        line.drain();
                    }
                }

                bytesRead += Utils.CHUNK_OF_10MS;
                x++;
            } catch (Exception e) {
                shutdown("Error during playback: " + e);
                break;
            }
        }

        line.drain();
        line.stop();
        line.close();
        line = null;
        shutdown(null);
    }
}