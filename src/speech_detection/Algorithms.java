package speech_detection;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A class that holds all of the algorithm pieces to detect speech
 * 
 * @author adveres
 * 
 */
public class Algorithms {

    static Data processFirst100ms(byte[] byteData) {

        int[] intSoundArray = Utils.byte_array_to_ints(Utils.getFormat(), byteData);
        int[] zeroCrossings = Utils.calculateZeroCrossingsInChunks(intSoundArray,
                Utils.CHUNK_OF_10MS);

        double meanZeroCrossing = MathHelper.mean(zeroCrossings); // IZC
        double stdDevZeroCrossingRate = MathHelper.stdDeviation(zeroCrossings);
        double meanZerosPlustwoTimesStdDev = (meanZeroCrossing + (2 * stdDevZeroCrossingRate));


        int[] energyArray = Utils.energyOfArray(intSoundArray, Utils.CHUNK_OF_10MS);
        double averageEnergy = MathHelper.mean(energyArray);

        double IF = 25.0; // Given by algorithm
        double IMX = MathHelper.max_of_ints(energyArray);
        double IMN = MathHelper.min_of_ints(energyArray);

        double I1 = (0.03 * (IMX - IMN) + IMN);
        double I2 = (4.0 * IMN);

        double ITL = Math.min(I1, I2);
        double ITU = 5 * ITL;
        double IZCT = IF < meanZerosPlustwoTimesStdDev ? IF : meanZerosPlustwoTimesStdDev;

        System.out.println("--- First 100ms data results:");
        System.out.println("  Zero crossings: " + Utils.arrayToString(zeroCrossings));
        System.out.println("  meanZeroCrossing " + meanZeroCrossing);
        System.out.println("  stdDevZeroCrossingRate " + stdDevZeroCrossingRate);
        System.out.println("  Energy: " + Utils.arrayToString(energyArray));
        System.out.println("  I1 " + I1);
        System.out.println("  I2 " + I2);
        System.out.println("  IMX " + IMX);
        System.out.println("  IMN " + IMN);
        System.out.println("  averageEnergy " + averageEnergy);
        System.out.println("  zeroCrossings " + MathHelper.sum(zeroCrossings));
        System.out.println("  ITL " + ITL);
        System.out.println("  ITU " + ITU);
        System.out.println("  IZCT " + IZCT);

        return new Data(ITL, ITU, IZCT);
    }

    /**
     * Remove silence from a byte[] array and return the speech only as a byte[]
     * array
     * 
     * @param rawSound
     * @param thresholds
     * @return
     */
    public static byte[] removeSilence(byte[] rawSound, Data thresholds) {
        int[] rawSoundArray = Utils.byte_array_to_ints(Utils.getFormat(), rawSound);
        int[] energyArray = Utils.energyOfArray(rawSoundArray, Utils.CHUNK_OF_10MS);
        ArrayList<Pair> endpointPairs = new ArrayList<Pair>();

        boolean done = false;
        int n1 = 1;
        int n2 = 1;
        while (!done) {
            n1 = getNextSpeechStartPoint(energyArray, thresholds, n2);
            if (n1 == (energyArray.length - 1)) {
                done = true;
                break;
            } else {
                n1 = checkBeforeN1(rawSoundArray, n1, thresholds);
            }

            n2 = getNextSpeechEndPoint(energyArray, thresholds, n1);

            if (n2 == (energyArray.length - 1)) {
                done = true;
                break;
            } else {
                n2 = checkAfterN2(rawSoundArray, n2, thresholds);
            }

            endpointPairs.add(new Pair(n1, n2));
        }

        System.out.println("silenceRemoved " + endpointPairs);
        System.out.println("silenceRemoved.size " + endpointPairs.size());
        return removeSilentBytes(rawSound, endpointPairs, thresholds);
    }

    /**
     * Algorithm given by Bell Labs packet
     * 
     * @param energyArray
     * @param thresholds
     * @param start
     * @return
     */
    public static int getNextSpeechStartPoint(int[] energyArray, Data thresholds, int start) {
        if (start >= energyArray.length) {
            System.err.println("Cannot give a start point larger than the array.");
            return energyArray.length - 1;
        }
        boolean outer = true;
        boolean inner = true;

        int m = start;
        int i = m;
        int n1 = 0;

        while (outer) {
            if (energyArray[m] > thresholds.ITL) {
                i = m;

                inner = true;
                while (inner) {
                    if (energyArray[i] < thresholds.ITL) {
                        m = i + 1;
                        inner = false;
                        if (m >= (energyArray.length)) {
                            System.out.println("m has reached the end of the array");
                            n1 = energyArray.length - 1;
                            return n1;
                        }
                        break;
                    } else {
                        // Packet was wrong here. ITU not ITL.
                        if (energyArray[i] >= thresholds.ITU) {
                            n1 = i;
                            if (i == m) {
                                n1 = n1 - 1;
                            }
                            inner = false;
                            outer = false;
                            break;

                        } else {
                            i++;
                            if (i >= (energyArray.length)) {
                                System.out.println("i has reached the end of the array");
                                n1 = energyArray.length - 1;
                                return n1;
                            }
                        }
                    }
                }
            } else {
                m++;
                if (m >= (energyArray.length)) {
                    System.out.println("m has reached the end of the array");
                    n1 = energyArray.length - 1;
                    return n1;
                }
            }
        }
        return n1;
    }

    /**
     * Starting at n1, find the next point passing below ITU
     * 
     * @param energyArray
     * @param thresholds
     * @param start
     * @return
     */
    public static int getNextSpeechEndPoint(int[] energyArray, Data thresholds, int start) {
        if (start >= energyArray.length) {
            System.err.println("Cannot give a start point larger than the array.");
            return energyArray.length - 1;
        }
        int n2 = 0;

        for (int x = start; x < energyArray.length; x++) {
            if ((energyArray[x] < thresholds.ITU) || (x == (energyArray.length - 1))) {
                n2 = x;
                return n2;
            }
        }

        return n2;
    }

    /**
     * Returns a byte array based on endpoint pairs.
     * 
     * @param rawSound
     * @param endpointPairs
     * @param thresholds
     * @return
     */
    public static byte[] removeSilentBytes(byte[] rawSound, ArrayList<Pair> endpointPairs,
            Data thresholds) {
        ArrayList<Byte> speech = new ArrayList<Byte>();

        for (Pair p : endpointPairs) {
            for (int x = p.getN1() * Utils.CHUNK_OF_10MS; x < p.getN2() * Utils.CHUNK_OF_10MS; x++) {
                speech.add(new Byte(rawSound[x]));
            }
        }

        return Utils.toByteArray(speech);
    }

    public static int checkBeforeN1(int[] intSoundArray, int n1, Data thresholds) {
        int new_n1 = n1;
        int startIndex = n1 * Utils.CHUNK_OF_10MS;
        int endIndex = (n1 * Utils.CHUNK_OF_100_MS) - Utils.CHUNK_OF_250MS;
        ArrayList<Integer> zeroCrossBeatsThreshold = new ArrayList<Integer>();
        if (endIndex < 0) {
            endIndex = 0;
        }
        for (int x = endIndex; x < startIndex; x++) {
            int offset = x * (Utils.CHUNK_OF_10MS);
            int[] chunk = Arrays.copyOfRange(intSoundArray, offset, (offset + Utils.CHUNK_OF_10MS));

            int zeroCt = Utils.calculateZeroCrossings(chunk);
            if (zeroCt > thresholds.IZCT) {
                System.out.println("n1 zeroCrossBeatsThreshold " + zeroCrossBeatsThreshold);
                zeroCrossBeatsThreshold.add(new Integer(zeroCt));
            }
        }
        if (zeroCrossBeatsThreshold.size() >= Utils.REQUIRED_ZERO_CROSSINGS) {
            new_n1 = zeroCrossBeatsThreshold.get(0);
            System.out.println("NEW N1 = " + new_n1);
        }

        return new_n1;
    }

    public static int checkAfterN2(int[] intSoundArray, int n2, Data thresholds) {
        int new_n2 = n2;
        int startIndex = n2;
        int endIndex = n2 + (Utils.CHUNK_OF_250MS / Utils.CHUNK_OF_10MS);
        ArrayList<Integer> zeroCrossBeatsThreshold = new ArrayList<Integer>();
        if (endIndex >= intSoundArray.length) {
            endIndex = intSoundArray.length - 1;
        }
        for (int x = startIndex; x < endIndex; x++) {
            int offset = x * (Utils.CHUNK_OF_10MS);
            if (offset >= intSoundArray.length) {
                break;
            }
            int[] chunk = Arrays.copyOfRange(intSoundArray, offset, (offset + Utils.CHUNK_OF_10MS));

            int zeroCt = Utils.calculateZeroCrossings(chunk);
            if (zeroCt > thresholds.IZCT) {
                zeroCrossBeatsThreshold.add(new Integer(x));
            }
        }
        if (zeroCrossBeatsThreshold.size() >= Utils.REQUIRED_ZERO_CROSSINGS) {
            new_n2 = zeroCrossBeatsThreshold.get(zeroCrossBeatsThreshold.size() - 1);
        }

        return new_n2;
    }
}
