package speech_over_ip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * 
 * A class that runs as a thread to record sound.
 * 
 * @author adveres
 * 
 */
class AudioRecorder implements Runnable {
    Thread thread;
    byte[] first_100_bytes;

    boolean running = false;

    Data data = null;
    ByteArrayOutputStream baos = null;
    Sender sender = null;

    public void start(String host, int port) {
        thread = new Thread(this);
        thread.start();
        running = true;

        try {
            sender = new Sender(host, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        running = false;
        thread = null;
    }

    public void run() {

        // Ensure we have a compatible line for our format.
        AudioFormat format = Utils.getFormat();
        TargetDataLine line = this.getAudioLine(format);
        if (null == line) {
            System.err.println("Unable to get line");
            return;
        }

        // Record!
        ByteArrayOutputStream first_100ms_bytes = new ByteArrayOutputStream();
        byte[] data = new byte[Utils.CHUNK_OF_10MS];

        int numBytesRead = 0;
        int totalBytesRead = 0;
        boolean first100Analyzed = false;

        line.start();
        while (running) {
            // System.out.println("listening");
            if ((numBytesRead = line.read(data, 0, Utils.CHUNK_OF_10MS)) == -1) {
                break;
            }
            totalBytesRead += numBytesRead;
            //System.out.println("totalBytesRead: " + totalBytesRead);

            //baos.write(data, 0, numBytesRead);
            try {
                sender.sendBytes(data);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Selectively write the first 100ms to this different buffer
            if (first_100ms_bytes.size() < Utils.CHUNK_OF_100_MS) {
                first_100ms_bytes.write(data, 0, numBytesRead);
            } else {
                if (!first100Analyzed) {
                    processFirst100ms(first_100ms_bytes);
                    first100Analyzed = true;
                }
            }

        }

        // we reached the end of the stream. stop and close the line.
        line.stop();
        line.close();
        line = null;

        System.out.println("Line stopped, closed, and nulled");

        // stop and close the output stream
        try {
            baos.flush();
            baos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        this.thread = null;
    }

    /**
     * Open a data line of the given format
     * 
     * @param format
     * @return
     */
    TargetDataLine getAudioLine(AudioFormat format) {
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine line = null;

        if (!AudioSystem.isLineSupported(dataLineInfo)) {
            System.err.println("Line " + dataLineInfo + " not supported.");
            return null;
        }

        // Try to get and open the target data line for capture.
        try {
            line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            line.open(format, line.getBufferSize());
        } catch (LineUnavailableException ex) {
            System.err.println("Unable to open the line: " + ex);
            return null;
        } catch (SecurityException ex) {
            System.err.println(ex.toString());
            return null;
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return null;
        }
        return line;
    }

    void processFirst100ms(ByteArrayOutputStream baos) {
        try {
            baos.flush();
            baos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        first_100_bytes = baos.toByteArray();

        this.data = Algorithms.processFirst100ms(first_100_bytes);
    }

}