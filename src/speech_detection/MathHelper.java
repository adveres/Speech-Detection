package speech_detection;

/**
 * Class with math related helper functions.
 * 
 * @author adveres
 * 
 */
public class MathHelper {

    /**
     * Return max of integer array
     * 
     * @param data
     * @return
     */
    public static int max_of_ints(int[] data) {
        int max = data[0];
        for (int x = 1; x < data.length; x++) {
            if (data[x] > max) {
                max = data[x];
            }
        }
        return max;
    }

    /**
     * Returns min of an array of ints
     * 
     * @param data
     * @return
     */
    public static int min_of_ints(int[] data) {
        int min = data[0];
        for (int x = 1; x < data.length; x++) {
            if (data[x] < min) {
                min = data[x];
            }
        }
        return min;
    }

    /**
     * Returns mean of array of ints
     * 
     * @param data
     * @return
     */
    public static double mean(int[] data) {
        int sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }
        return (sum / data.length);
    }

    /**
     * Returns standard deviation of array of ints
     * 
     * @param x
     * @return
     */
    public static double stdDeviation(int[] data) {
        double mean = mean(data);
        double sum = 0;

        for (int i = 0; i < data.length; i++) {
            sum += Math.pow(data[i] - mean, 2);
        }

        return Math.sqrt((sum) / (data.length - 1));
    }

    /**
     * Return sum of integer array
     * 
     * @param x
     * @return
     */
    public static int sum(int[] data) {
        int sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }
        return sum;
    }

    /**
     * Return sum of absVal of each index
     * 
     * @param data
     * @return
     */
    public static int abs_sum(int[] data) {
        int sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum += Math.abs(data[i]);
        }
        return sum;
    }
}
