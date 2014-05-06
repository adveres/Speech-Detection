package speech_detection;

/**
 * A class I use as an extra Main() to test what I'm working on. Can be called,
 * but not usefully.
 * 
 * @author adveres
 * 
 */
public class Example {

    public static void main(String[] args) {

        // String fileName = "adam1.raw";
        String fileName = Utils.FILENAME_SOUND_RAW;
        // String fileName = "rawSilence.raw";

        AudioBytePlayer bytePlayer = new AudioBytePlayer(FileSaver.fileToBytes(fileName));
        bytePlayer.start();
        try {
            bytePlayer.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        byte[] rawSoundBytes = FileSaver.fileToBytes(fileName);
        Data d = Utils.analyzeFirst100ms(rawSoundBytes);

        byte[] speechOnly = Algorithms.removeSilence(rawSoundBytes, d);

        System.out.println(d);
        System.out.println("Raw sound len: " + rawSoundBytes.length);
        System.out.println("Speech len: " + speechOnly.length);

        AudioBytePlayer bytePlayer3 = new AudioBytePlayer(speechOnly);
        bytePlayer3.start();
        try {
            bytePlayer3.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
