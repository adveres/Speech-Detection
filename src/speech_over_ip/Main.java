package speech_over_ip;

import java.io.IOException;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) {

        // String host = "192.168.1.11";
        String host = "localhost";
        int port = 6222;

        Receiver receiver = null;
        try {
            receiver = new Receiver(port);
            receiver.start();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        AudioRecorder recorder = new AudioRecorder();
        recorder.start(host, port);

        try {
            // Wait for threads to stop
            receiver.thread.join();
            recorder.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

}
