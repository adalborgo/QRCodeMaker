package it.dibis.qrcodemaker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCodeMake {

    // Revision control id
    public static String cvsId = "$Id: QRCodeMake.java,v 0.1 06/01/2023 23:59:59 adalborgo $";

    public static boolean DEBUG = false;

    public final String SEPARATOR = "|"; // Only for 'makeFromFileWithManyStrings()'

    public final int ERR_FILE_NOT_FOUND = 1;
    public final int ERR_WRITE_FILE = 2;
    public final int ERR_IO = 3;
    public final int ERR_TEXT_LEN = 4;

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
        String text = "";
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(dataFile));
            text = new String(bytes);
            if (DEBUG) System.out.println(text);
            saveQRImage(text, imgType, size, outputPathname + "." + imgType.toLowerCase());
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
     * @param dataFile
     * @param folder
     * @param header
     * @param imgType
     * @param size
     * @return
     */
    public int makeFromFileWithManyStrings(String dataFile, String folder, String header, String imgType, int size) {
        int error = 0;
        int pnt;;
        makeFolder(folder); // Check and make if outputPath exist
        try {
            File myObj = new File(dataFile);
            Scanner myReader = new Scanner(myObj);
            int index = 0;
            String text;
            String filename;
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                if (line != null && line.length() > 0) {
                    pnt = line.lastIndexOf(SEPARATOR);
                    if (pnt > 0) {
                        text = header + line.substring(0, pnt).trim();
                        filename = line.substring(pnt + 1).trim();
                        if (!DEBUG) saveQRImage(text, imgType, size, folder + "/" + filename + "." + imgType.toLowerCase());
                    } else if (pnt<0) {
                        filename = String.valueOf(index).trim();
                        if (!DEBUG) saveQRImage(line, imgType, size, folder + "/" + filename + "." + imgType.toLowerCase());
                        ++index;
                    }
                }
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            error = ERR_FILE_NOT_FOUND;
        }

        return error;
    }

    /**
     * Save the image file with the qrcode
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

        Writer qrWriter = new QRCodeWriter();
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            FileOutputStream fout = new FileOutputStream(new File(pathname));

            // OutputStream is a byte array of the QR code
            MatrixToImageWriter.writeToStream(
                    qrWriter.encode(text, com.google.zxing.BarcodeFormat.QR_CODE, size, size, hints),
                    imgType, stream);

            // Write image as a file
            fout.write(stream.toByteArray());
            fout.flush();
            fout.close();
        } catch (WriterException e) {
            e.printStackTrace();
            error = ERR_WRITE_FILE;
        } catch (IOException e) {
            e.printStackTrace();
            error = ERR_IO;
        }

        return error;
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

    //--- Only for debug ---//
    public static void main(String[] args) {
        String imgType = "PNG";
        int size = 600;
        String dataFile = "list.dat";
        String outputPath = "Commenda-sorted.txt";
        String folder = "qr-tmp";
        String header = "https://www.museoscienzefaenza.it/treelib/";
        //new QRCodeMake().makeFromFileSingleString(dataFile, outputPath, imgType, size);
        new QRCodeMake().makeFromFileWithManyStrings(dataFile, folder, header, imgType, size);
    }

}
