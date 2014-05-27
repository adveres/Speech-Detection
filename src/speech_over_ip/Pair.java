package speech_over_ip;

/**
 * Simple class to hold a pair of integer points
 * 
 * @author adveres
 * 
 */
public class Pair {
    int n1 = 0;
    int n2 = 0;

    public Pair(int n1, int n2) {
        this.n1 = n1;
        this.n2 = n2;
    }

    public String toString() {
        String s = "(" + n1 + ", " + n2 + ")";
        return s;
    }

    public int getN1() {
        return n1;
    }

    public void setN1(int n1) {
        this.n1 = n1;
    }

    public int getN2() {
        return n2;
    }

    public void setN2(int n2) {
        this.n2 = n2;
    }

}
