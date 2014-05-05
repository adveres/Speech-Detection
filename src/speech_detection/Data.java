package speech_detection;

/**
 * A class to hold the statistics we uncover from the first 100ms of audio
 * 
 * @author adveres
 * 
 */
public class Data {
    double ITL = 0.0;
    double ITU = 0.0;
    double IZCT = 0.0;

    public Data(double ITL, double ITU, double IZCT) {
        this.ITL = ITL;
        this.ITU = ITU;
        this.IZCT = IZCT;
    }

    public String toString() {
        String s = "ITL: " + ITL + ", ITU: " + ITU + ", IZCT: " + IZCT;
        return s;
    }
}
