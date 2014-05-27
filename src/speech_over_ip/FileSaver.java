package speech_over_ip;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * A class for saving information to files in certain formats.
 * 
 * @author adveres
 * 
 */
public class FileSaver {

    /**
     * Save an audio input stream to a WAVE file as raw sound.
     * 
     * @param audioInputStream
     */
    public static void saveToWaveFile(AudioInputStream audioInputStream) {

        AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

        if (audioInputStream == null) {
            System.err.println("No loaded audio to save");
            return;
        }

        try {
            audioInputStream.reset();
        } catch (Exception e) {
            System.err.println("Unable to reset stream " + e);
            return;
        }

        File file = new File(Utils.FILENAME_SOUND_RAW);
        try {
            if (AudioSystem.write(audioInputStream, fileType, file) == -1) {
                throw new IOException("Problems writing to file");
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }
    }

    /**
     * Takes an AudioInputStream, converts to byte array, and prints as integers
     * to file.
     * 
     * @param ais
     */
    public static void audioToDataFile(AudioInputStream ais) {
        byte[] buffer;
        BufferedWriter fileOut;

        try {
            int numBytes = ais.available();
            System.out.println(numBytes);
            buffer = new byte[numBytes];
            ais.read(buffer, 0, numBytes);
            fileOut = new BufferedWriter(new FileWriter(new File(Utils.FILENAME_SOUND_DATA)));

            for (int x = 0; x < buffer.length; x++) {
                int current = buffer[x];
                System.out.println(String.valueOf(current));
                fileOut.write(String.valueOf(current));
                fileOut.newLine();
            }

            fileOut.flush();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * Save Byte Array to file
     * 
     * @param fileName
     * @param data
     */
    public static void bytesToFile(String fileName, byte[] data) {
        FileOutputStream output = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            output = new FileOutputStream(file);
            output.write(data);
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read a file into a byte array
     * 
     * @param fileName
     * @return data a byte array of file contents
     */
    public static byte[] fileToBytes(String fileName) {
        RandomAccessFile f = null;
        try {
            f = new RandomAccessFile(fileName, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        byte[] data = null;
        try {
            data = new byte[(int) f.length()];
            f.readFully(data);
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * Write an array of ints to a file
     * 
     * @param fileName
     * @param data
     */
    public static void intsToFile(String fileName, int[] data) {
        BufferedWriter outputWriter = null;

        try {
            outputWriter = new BufferedWriter(new FileWriter(fileName));
            for (int i = 0; i < data.length; i++) {
                outputWriter.write(Integer.toString(data[i]));
                if (i != (data.length - 1)) {
                    outputWriter.newLine();
                }
            }
            outputWriter.flush();
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads in Byte file, saved it to file of integers
     * 
     * @param originalFileName
     * @param dataFileName
     */
    public static void byteFile_to_dataFile(String originalFileName, String dataFileName) {
        byte[] soundBytes = FileSaver.fileToBytes(originalFileName);
        int[] data = Utils.byte_array_to_ints(soundBytes);

        FileSaver.intsToFile(dataFileName, data);
    }
    
    /**
     * Takes a filename and returns an AudioInputStream of that file
     * 
     * @param fileName
     * @return
     */
    public static AudioInputStream fileToAudioInputStream(String fileName) {
        File file = new File(fileName);
        return fileToAudioInputStream(file);
    }

    /**
     * Takes a file and turns it into an AudioInputStream
     * 
     * @param file
     */
    public static AudioInputStream fileToAudioInputStream(File file) {
        if (null == file) {
            System.err.println("File cannot be null.");
            return null;
        }
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(file);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.toString());
        }

        return audioInputStream;
    }
}
