package it.dibis.qrcodemaker;

import java.io.*;
import java.util.Hashtable;
import com.google.zxing.*;
import com.google.zxing.Writer;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

/**
 * @package: QRCodeMaker
 * @file QRCodeMake.java
 * @library: ZXing (core-3.5.2.jar; javase-3.5.2.jar)
 * @version 1.0 (27-01-2024)
 * @description: this file contains the code to generate QR code from text and files
 * @author Antonio Dal Borgo <adalborgo@gmail.com>
 */
public class QRCodeMake {

    // Revision control id
    public static String cvsId = "$Id: QRCodeMake.java,v 1.0 27/01/2023 23:59:59 adalborgo $";

    public static boolean DEBUG = false;

    public final String SEPARATOR = "|"; // Only for 'makeFromFileWithManyStrings()'

    public final int ERR_FILE_NOT_FOUND = 1;
    public final int ERR_WRITE_FILE = 2;
    public final int ERR_IO = 3;
    public final int ERR_TEXT_LEN = 4;
    public final int ERR_ENCODING = 5;

    /**
     * The file contains the text to be converted into qrcode
     *
     * @param dataFile
     * @param outputPathname
     * @param imgType
     * @param size
     * @return
     */
    public int makeFromFileSingleString(String dataFile, String outputPathname, String imgType, int size) {
        int error = 0;

        // Load the datafile in a text string (binary mode)
        int[] bts = null;
        try (FileInputStream fis = new FileInputStream(dataFile)) {
            bts = new int[fis.available()];
            for (int i = 0; i < bts.length; i++) {
                bts[i] = fis.read();
            }

            // Convert char[] to String
            char[] chars = new char[bts.length];
            for (int i = 0; i < bts.length; i++) chars[i] = (char) bts[i];
            String text = new String(chars);
            if (DEBUG) System.out.println(text);

            // Make QRCode
            saveQRImage(text, imgType, size, checkExt(outputPathname, imgType));
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFound error!");
            e.printStackTrace();
            error = ERR_FILE_NOT_FOUND;
        } catch (IOException e) {
            e.printStackTrace();
            error = ERR_IO;
        }

        return error;
    }

    /**
     * Each line of the 'dataFile' file contains the text and file name of the qrcode
     *
     * @param dataFile
     * @param folder
     * @param header
     * @param imgType
     * @param size
     * @return
     */
    public int makeFromFileWithManyStrings(String dataFile, String folder, String header, String imgType, int size) {
        int error = 0;
        int pnt;

        makeFolder(folder); // Check and make if outputPath exist

        // Load every line in binary mode (read lines with accent mark)
        try {
            RandomAccessFile file = new RandomAccessFile(dataFile, "r");
            int index = 0;
            String text = null;
            String line = null;
            String filename = null;
            while ((line = file.readLine()) != null) {
                if (DEBUG) System.out.println(line);
                if (line != null && line.length() > 0) {
                    pnt = line.lastIndexOf(SEPARATOR);
                    String s = null;
                    if (pnt > 0) {
                        text = header + line.substring(0, pnt).trim();
                        s = line.substring(pnt + 1).trim();
                        filename = checkExt(s, imgType);
                        if (!DEBUG) saveQRImage(text, imgType, size, folder + "/" + filename);
                    } else if (pnt < 0) {
                        text = header + line;
                        s = String.valueOf(index).trim();
                        ++index;
                        filename = checkExt(s, imgType);
                        if (!DEBUG) saveQRImage(text, imgType, size, folder + "/" + filename);
                    }
                }
            }
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
            error = ERR_FILE_NOT_FOUND;
        }

        return error;
    }

    /**
     * Save the qrcode image file from a string
     *
     * @param text
     * @param imgType  = {JPG | GIF | PNG | BMP}
     * @param size
     * @param pathname
     * @return
     */
    int saveQRImage(String text, String imgType, int size, String pathname) {
        int error = 0;
        if (text.length() > 4296) return ERR_TEXT_LEN;

        File file = new File(pathname);
        BitMatrix matrix = null;
        Writer writer = new MultiFormatWriter();
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>(2);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            matrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size, hints);
            MatrixToImageWriter.writeToFile(matrix, imgType, file);
            if (DEBUG) System.out.println("QRCode Image: " + file.getAbsolutePath());
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            error = ERR_WRITE_FILE;
            return error;
        }

        return 0;
    }

    /**
     * Create the folder if it doesn't exist
     *
     * @param pathDir
     * @return
     */
    public boolean makeFolder(String pathDir) {
        try {
            if (!new File(pathDir).exists()) new File(pathDir).mkdir();
            return false; // Ok
        } catch (Exception e) {
            System.out.println("MkDir error. (" + e + ")");
            return true; // mkdir error
        }
    }

    /**
     * Add the extension if it doesn't exist
     * @param s
     * @param imgType
     * @return
     */
    String checkExt(String s, String imgType) {
        if (s.toLowerCase().endsWith("." + imgType)) {
            return s;
        } else {
            return s + "." + imgType.toLowerCase();
        }
    }

}
