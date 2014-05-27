package speech_over_ip;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Receiver implements Runnable {

    ServerSocket serverSocket = null;
    // InputStream in = null;
    // DataInputStream dis = null;
    Thread thread = null;

    public Receiver(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        // in = serverSocket.getInputStream();
        // dis = new DataInputStream(in);
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        thread = null;
    }

    public void run() {
        //ServerSocket serverSocket = null;

        // try {
        // serverSocket = new ServerSocket(4444);
        // } catch (IOException ex) {
        // System.out.println("Can't setup server on this port number. ");
        // }

        Socket socket = null;
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        int bufferSize = 0;

        try {
            socket = serverSocket.accept();
        } catch (IOException ex) {
            System.out.println("Can't accept client connection. ");
        }

        try {
            is = socket.getInputStream();

            bufferSize = socket.getReceiveBufferSize();
            System.out.println("Buffer size: " + bufferSize);
        } catch (IOException ex) {
            System.out.println("Can't get socket input stream. ");
        }

        try {
            fos = new FileOutputStream("/Users/averes/dev/Speech-Over-IP/OUT.txt");
            bos = new BufferedOutputStream(fos);

        } catch (FileNotFoundException ex) {
            System.out.println("File not found. ");
        }

        byte[] bytes = new byte[bufferSize];

        int count;

        try {
            while ((count = is.read(bytes)) > 0) {
                bos.write(bytes, 0, count);
            }
            bos.flush();
            bos.close();
            is.close();
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // public byte[] readBytes() throws IOException {
    // int len = dis.readInt();
    // byte[] data = new byte[len];
    // if (len > 0) {
    // dis.readFully(data);
    // }
    // return data;
    // }
}
