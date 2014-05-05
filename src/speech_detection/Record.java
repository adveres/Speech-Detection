package speech_detection;

import javax.sound.sampled.AudioFormat;

/**
 * @author adveres
 * 
 *         You will write a program, called record, that records sound from the
 *         microphone until the user presses ctrl-c and exits. Your program will
 *         produce as output:
 * 
 *         A file named sound.data will contain all the audio data (numbers
 *         between 0 and 255) suitable for displaying on a graph.
 * 
 *         A file named sound.raw will contain the raw audio data recorded with
 *         all the sound, including the silence, suitable for playing out to an
 *         audio device.
 * 
 *         A file named speech.raw will contain the raw audio data recorded
 *         without silence, suitable for playing out to an audio device.
 * 
 *         A file named energy.data will contain the energy, with one audio
 *         frame of data per row, suitable for displaying on a graph.
 * 
 *         A file named zero.data will contain the zero crossings, with one
 *         audio frame of data per row, suitable for displaying on a graph.
 * 
 *         You should configure the sound device to capture audio at typical
 *         voice quality rates: 8000 samples/second, 8 bits per sample with one
 *         channel (i.e. mono, not stereo).
 */

public class Record {
    static AudioFormat format = Utils.getFormat();

    public static void main(String[] args) {

        final AudioRecorder recorder = new AudioRecorder();

        // Attach a stupid shutdown hook to catch ctrl+c in a Java console
        // program.
        ShutdownThread shutdownHook = new ShutdownThread(recorder);
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook));

        // The recorder MUST write to the shutdown hook's ByteArrayOutputStream
        // otherwise there is no guarantee BAOS won't be nulled during shutdown.
        recorder.start(shutdownHook.baos);

        try {
            // Wait for thread to stop
            recorder.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.exit(0);

    }
}
