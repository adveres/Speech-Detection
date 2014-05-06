package speech_detection;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServerConnection;

import com.sun.management.OperatingSystemMXBean;

/**
 * This program reads in the sound.raw file and benchmarks how long it takes to
 * run the algorithm on it over 10,000 tries.
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
     */
    public void measureAverageRunTime() {
        byte[] speechOnly = null;
        long start = 0;
        int runs = 10000; // enough to run for 2-10 seconds.
        for (int i = -10000; i < runs; i++) {
            if (i == 0) {
                start = System.nanoTime();
            }
            speechOnly = Algorithms.removeSilence(rawSoundBytes, measurements);
        }
        long time = System.nanoTime() - start;
        System.out.printf("Each silence removal run took an average of %,d ns%n", time / runs);
    }

    /**
     * Measure CPU usage before/after the algorithm to capture the load.
     */
    public void measureCPUUsage() {
        MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();
        OperatingSystemMXBean osMBean = null;
        try {
            osMBean = ManagementFactory.newPlatformMXBeanProxy(mbsc,
                    ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        long nanoBefore = System.nanoTime();
        long cpuBefore = osMBean.getProcessCpuTime();

        // Call an expensive task, or sleep if you are monitoring a remote
        // process
        byte[] speechOnly = Algorithms.removeSilence(rawSoundBytes, measurements);

        long cpuAfter = osMBean.getProcessCpuTime();
        long nanoAfter = System.nanoTime();
        long percent;
        System.out.println(nanoBefore + " " + nanoAfter);
        if (nanoAfter > nanoBefore)
            percent = ((cpuAfter - cpuBefore) * 100L) / (nanoAfter - nanoBefore);
        else
            percent = 0;

        System.out.println("Cpu usage: " + percent + "%");
    }

    /**
     * MAIN
     * 
     * @param args
     */
    public static void main(String[] args) {

        Benchmark bench = new Benchmark();
        bench.measureAverageRunTime();
        bench.measureCPUUsage();

    }
}
