package speech_detection;

/**
 * Plays an array of raw bytes loaded from a file of given name (arg0).
 * 
 * @author adveres
 * 
 */
public class Play {
    public static void main(String[] args) {
        String fileName = Utils.FILENAME_SOUND_RAW;
        if (args.length == 0) {
            System.out.println("No argument given, defaulting to '" + Utils.FILENAME_SOUND_RAW
                    + "'");
        } else {
            fileName = args[0];
        }

        // Play that sound!
        AudioBytePlayer bytePlayer = new AudioBytePlayer(FileSaver.fileToBytes(fileName));
        bytePlayer.start();
        try {
            bytePlayer.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
