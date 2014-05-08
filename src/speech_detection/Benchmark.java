package speech_detection;

/**
 * This program reads in the sound.raw file and runs certain benchmarks on its
 * system footprint while it runs the algorithm to detect speech.
 * 
 * @author adveres
 * 
 */
public class Benchmark {
    String fileName;
    byte[] rawSoundBytes;
    Data measurements;

    public Benchmark() {
        fileName = Utils.FILENAME_SOUND_RAW;
        rawSoundBytes = FileSaver.fileToBytes(fileName);
        measurements = Utils.analyzeFirst100ms(rawSoundBytes);
    }

    /**
     * Run the algorithm 10K times to make the JVM work, then do another 10K
     * times and take the average.
     * 
     * https://stackoverflow.com/questions/3382954/measure-execution-time-for-a-java-method
     */
    public void measureAverageRunTime() {
        long start = 0;
        int runs = 10000; // enough to run for 2-10 seconds.
        for (int i = -10000; i < runs; i++) {
            if (i == 0) {
                start = System.nanoTime();
            }
            byte[] speechOnly = Algorithms.removeSilence(rawSoundBytes, measurements);
        }
        long time = System.nanoTime() - start;
        System.out.printf("Each silence removal run took an average of %,d ns%n", time / runs);
    }

    /**
     * Pull in both sound.raw/speech.raw and compare file sizes.
     */
    public void measureFileSizes() {
        byte[] rawSoundBytes = FileSaver.fileToBytes(Utils.FILENAME_SOUND_RAW);
        byte[] rawSpeechBytes = FileSaver.fileToBytes(Utils.FILENAME_SPEECH_RAW);

        System.out.print("Raw sound file is [" + rawSoundBytes.length + " bytes].  ");
        System.out.println("Speech file is [" + rawSpeechBytes.length + " bytes].");

        double reduction = 100 - (100 * ((double) rawSpeechBytes.length / (double) rawSoundBytes.length));
        System.out.printf("Speech file is %.2f%s smaller than the original raw sound.", reduction,
                "%");
    }

    /**
     * MAIN
     * 
     * @param args
     */
    public static void main(String[] args) {

        Benchmark bench = new Benchmark();
        bench.measureAverageRunTime();
        bench.measureFileSizes();
    }
}
