package speech_detection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A thread intended to be called when ctrl+c is pressed after recording some
 * audio!
 * 
 * @author adveres
 * 
 */
public class ShutdownThread implements Runnable {

    Thread thread;
    AudioRecorder recorder;
    public ByteArrayOutputStream baos = new ByteArrayOutputStream();

    public ShutdownThread(AudioRecorder ar) {
        recorder = ar;
    }

    public void run() {
        if (null == recorder) {
            System.err.println("ShutdownThread called with no recorder to handle!");
            return;
        }
        System.out.println("ShutdownThread called!");

        // Stop the recorder if it isn't already
        recorder.stop();
        // try {
        while (null != recorder.thread) {
            // Never seem to end up in here implying the shutdown process kills
            // the other thread mercilessly.
            System.out.print("WAITING FOR THREAD TO NULL");
        }
        // recorder.thread.join();
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }

        try {
            baos.flush();
            baos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println("Out flushed, closed");

        byte[] audioBytes = baos.toByteArray();
        int[] intArray = Utils.byte_array_to_ints(audioBytes);
        int[] zeroCrossings = Utils.calculateZeroCrossingsInChunks(intArray, Utils.CHUNK_OF_10MS);

        // Process the first 100ms for statistical data
        byte[] first_800_bytes = new byte[Utils.CHUNK_OF_100_MS];
        for (int x = 0; x < Utils.CHUNK_OF_100_MS; x++) {
            first_800_bytes[x] = audioBytes[x];
        }
        Data d = Algorithms.processFirst100ms(first_800_bytes);

        byte[] speechOnly = Algorithms.removeSilence(audioBytes, d);

        System.out.println(d);
        System.out.println("speechOnly: " + speechOnly);
        System.out.println("len speechOnly: " + speechOnly.length);
        System.out.println("recorder.audioBytes: " + audioBytes);
        System.out.println("recorder.audioBytes.length: " + audioBytes.length);
        System.out.println("filename: " + Utils.FILENAME_SOUND_RAW);
        System.out.println(audioBytes);

        // Write out allllll of the files.

        // Raw sound bytes
        FileSaver.bytesToFile(Utils.FILENAME_SOUND_RAW, audioBytes);

        // Bytes as integer data
        FileSaver.byteFile_to_dataFile(Utils.FILENAME_SOUND_RAW, Utils.FILENAME_SOUND_DATA);

        // Energy data per 10ms chunk
        FileSaver.intsToFile(Utils.FILENAME_ENERGY_DATA,
                Utils.energyOfArray(intArray, Utils.CHUNK_OF_10MS));

        // Raw speech minus silence
        FileSaver.bytesToFile(Utils.FILENAME_SPEECH_RAW, speechOnly);

        // Zero crossings per 10ms chunk
        FileSaver.intsToFile(Utils.FILENAME_ZERO_DATA, zeroCrossings);
    }
}
