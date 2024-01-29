package it.dibis.qrcodemaker;

/**
 * @package: QRCodeMaker
 * @file Languages.java
 * @version 1.0 (23-01-2024)
 * @description: this file contains text strings in two languages: English and Italian
 * @author Antonio Dal Borgo <adalborgo@gmail.com>
 */
public interface Languages {

    // Revision control id
    public static String cvsId = "$Id: Languages.java,v 1.0 23/01/2023 23:59:59 adalborgo $";

    public final String[][] MODE_MSG = {
            {"Type the text to encode",
                "Digitare il testo da codificare"},

            {"Load the file with the text to be encoded",
                "Caricare il file con  il testo da codificare"},

            {"Multiple encodings: the file to be loaded contains the texts for each QRCode",
            "Codifiche multiple: caricare il file con i testi per ciascun QRCode"}
    };

    public final String APP_NAME = " QRCode maker ";
    public final String APP_VERSION = "1.0";

    public final String WINDOW_NAME = APP_NAME + "(" + APP_VERSION +")";

    public final String[] OK = {"OK", "OK"};

    // Panel Title labels
    public final String[] DATA_FILE = {"Data", "Dati"};
    public final String[] IMAGE = {"Image", "Immagine"};
    public final String[] MODE = {"Mode", "Modo"};

    // Button texts
    public final String[] CLEAR = {"Clear", "Cancella"};
    public final String[] RUN = {"Run", "Esegui"};
    public final String[] EXIT = {"Exit", "Esci"};
    public final String[] INFO = {"Info", "Info"};

    // Mode labels
    public final String[] STRING_MODE = {"Text", "Testo"};
    public final String[] TEXT_FILE = {"Text file", "File di testo"};
    public final String[] ARCH_FILE = {"Arch file", "File di archivio"};

    // Data Panel Labels
    public final String[] DATA_LABEL = {"Data File", "File dati"};
    public final String[] FOLDER_LABEL = {"Folder", "Cartella"};
    public final String[] HEADER_LABEL = {"Header", "Intestazione"};
    public final String[] OUTPUT_LABEL = {"QRCode PathName", "File QRCode"};
    public final String[] SIZE_LABEL = {"Size (pixel)", "Dimensione (pixel)"};

    // Errors
    public final String[] ERR_FILE1 = {"File '", "File '"};
    public final String[] ERR_FILE2 = {"' not exist!", "' non esiste!"};
    public final String[] ERR_WRITE = {"Write error!", "Errore di scrittura!"};
    public final String[] ERR_IO = {"IO error!", "Errore di IO"};
    public final String[] ERR_TEXT_LEN = {"Text too long (max 4296).", "Testo troppo lungo (max 4296)."};
    public final String[] ERR_ENCODING = {"Encoding error!", "Errore di Codifica"};
}
