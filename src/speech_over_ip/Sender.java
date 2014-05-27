package speech_over_ip;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Sender {
    private Socket senderSocket = null;
    OutputStream out = null;

    public Sender(String host, int port) throws IOException, UnknownHostException {
        try {
            senderSocket = new Socket(host, port);
            out = senderSocket.getOutputStream();
        } catch (UnknownHostException e) {
            System.err.println("Unable to find host: " + host);
            throw e;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O connection to: " + host);
            throw e;
        }
    }

    /**
     * Send an array of bytes over a socket
     * 
     * @param myByteArray
     * @throws IOException
     */
    public void sendBytes(byte[] myByteArray) throws IOException {
        sendBytes(myByteArray, 0, myByteArray.length);
    }

    /**
     * Send an array of bytes over a socket
     * 
     * @param myByteArray
     * @param start
     * @param len
     * @throws IOException
     */
    public void sendBytes(byte[] myByteArray, int start, int len) throws IOException {
        if (len < 0) {
            throw new IllegalArgumentException("Negative length not allowed");
        }
        if (start < 0 || start >= myByteArray.length) {
            throw new IndexOutOfBoundsException("Out of bounds: " + start);
        }

        DataOutputStream dos = new DataOutputStream(out);

        dos.writeInt(len);
        if (len > 0) {
            dos.write(myByteArray, start, len);
        }
    }
}
