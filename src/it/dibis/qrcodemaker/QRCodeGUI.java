package it.dibis.qrcodemaker;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.Locale;

/**
 * @package: QRCodeMaker
 * @file QRCodeGUI.java
 * @version 1.0 (26-01-2024)
 * @description: this file contains the GUI to generate QR code from text and files
 * @author Antonio Dal Borgo <adalborgo@gmail.com>
 */
public class QRCodeGUI implements ActionListener, Languages {

    // Revision control id
    public static String cvsId = "$Id: QRCodeGUI.java,v 1.0 26/01/2023 23:59:59 adalborgo $";

    public static boolean DEBUG = false;

    private final String COPYRIGHT = "www.dibis.it"; // \u00A9

    private final int TEXT_FILE_INX = 0;
    private final int ARCH_FILE_INX = 1;
    private final int STRING_INX = 2;

    private final String FOLDER = "folder.png";
    private URL FOLDER_URL = null;

    private final String JPG = "jpg";
    private final String PNG = "png";
    private final String GIF = "gif";
    private final String BMP = "bmp";

    private final Font titleFont = new Font("Arial", Font.BOLD, 14);
    private final Font labelFont = new Font("Arial", Font.BOLD, 12);
    private final Font textAreaFont = new Font("Arial", Font.BOLD, 12);

    private final Color FRAME_BG = Color.decode("#FFFF00");
    private final Color TITLE_COLOR = Color.decode("#000080"); // Blue navy
    private final Color LABEL_COLOR = Color.decode("#006080"); // Blue Lagoon
    private final Color MSG_COLOR = Color.DARK_GRAY; //.decode("#800000"); // Maroon
    private final Color ERR_COLOR = Color.decode("#F00000"); // Helvetia Red Color
    private final Color OK_COLOR = Color.decode("#4B8F10"); // 52900B

    private JPanel dataFilePanel, folderPanel, textPanel, headerPanel, outputPathPanel;
    private JTextField dataFileText, outputPathText, folderText, headerText, imgTypeText, imgSizeText;
    private JTextArea textArea;
    private JTextField message;

    private JButton clearButton, runButton, exitButton, infoButton;
    private JButton dataFileButton, outputPathButton, folderButton;
    private JRadioButton mode1RadioBtn, mode2RadioBtn, mode3RadioBtn; //, mode4RadioBtn;

    private final String DEFAULT_IMG_SIZE = "300"; // Default: 300 px
    private String imgType = JPG; // Default: JPG
    private int mode = STRING_INX; // Default

    // Locale
    private final Locale locale = Locale.getDefault();
    final String langId = locale.getLanguage();

    // Info
    private final String INFO_FILE = "info-"; // "info-" + langId +".html";

    QRCodeMake qrcode = new QRCodeMake();

    JFrame frame;

    private int language = 0; // Default

    public QRCodeGUI() {
        FOLDER_URL = getUrlPath(FOLDER);

        // Get language
        if (langId.equals("it")) language = 1;

        makeGUI();
    }

    /**
     * Make GUI
     */
    private void makeGUI() {
        // Select: Windows Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        frame = new JFrame();
        frame.setTitle(WINDOW_NAME);
        frame.setBackground(FRAME_BG);
        frame.setContentPane(contentPanel);
        frame.getContentPane().setLayout(
                new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS)
        );
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add panels to the frame
        frame.add(dataPanel());
        frame.add(imagePanel());
        frame.add(modePanel());
        frame.add(msgPanel());
        frame.add(buttonPanel());
        creditsPanel();
        frame.pack();
        frame.setVisible(true);

        if (DEBUG) System.out.println(MODE_MSG[0][language]);
    }

    private void creditsPanel() {
        JPanel panel = new JPanel();
        JLabel jbl = customLabel(COPYRIGHT, new Font("Monospaced", Font.BOLD, 18), Color.decode("#003388"));
        panel.add(jbl);
        frame.add(panel);
    }

    private JPanel dataPanel() {
        String titlePanel = DATA_FILE[language];
        JPanel mainPanel = titlePanel(titlePanel, BoxLayout.Y_AXIS, titleFont, TITLE_COLOR);

        //--- DataFile Panel ---//
        dataFileText = new JTextField("", 80);
        dataFileText.addActionListener(this);

        // DataFile Button
        dataFileButton = new JButton();
        dataFileButton.addActionListener(this);

        dataFilePanel = pathfilePanel(DATA_LABEL[language], dataFileText, dataFileButton, null);

        //--- TextArea panel ---//
        textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        // Text label
        textPanel.add(customLabelPanel(
                STRING_MODE[language], 80, labelFont, LABEL_COLOR, FlowLayout.LEFT));

        // Text area
        textArea = new JTextArea(4, 60);
        textArea.setFont(textAreaFont);
        textArea.setEditable(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(new EmptyBorder(5, 5, 5, 5));
        textPanel.add(scroll);
        textPanel.setVisible(true); // Default
        dataFilePanel.setVisible(false); // Default

        //--- OutputPath ---//
        outputPathText = new JTextField("", 80);
        outputPathText.addActionListener(this);

        outputPathButton = new JButton();
        outputPathButton.addActionListener(this);

        outputPathPanel = pathfilePanel(OUTPUT_LABEL[language], outputPathText, outputPathButton, null);
        outputPathPanel.setVisible(true);

        //--- Folder Panel --//
        folderText = new JTextField("", 80);
        folderText.addActionListener(this);

        folderButton = new JButton();
        folderButton.addActionListener(this);

        folderPanel = pathfilePanel(FOLDER_LABEL[language], folderText, folderButton, null);
        folderPanel.setVisible(false);

        //--- Header Panel --//
        headerText = new JTextField("", 80);
        headerText.addActionListener(this);

        headerPanel = pathfilePanel(HEADER_LABEL[language], headerText, null, null);
        headerPanel.setVisible(false);

        mainPanel.add(dataFilePanel);
        mainPanel.add(textPanel);
        mainPanel.add(headerPanel);
        mainPanel.add(outputPathPanel);
        mainPanel.add(folderPanel);

        return mainPanel;
    }

    private JPanel imagePanel() {
        String titlePanel = IMAGE[language];
        JPanel mainPanel = titlePanel(titlePanel, BoxLayout.Y_AXIS, titleFont, TITLE_COLOR);

        // Size panel
        JPanel sizePanel = new JPanel();
        sizePanel.add(customLabel(SIZE_LABEL[language], labelFont, LABEL_COLOR));
        imgSizeText = new JTextField(DEFAULT_IMG_SIZE, 6);
        imgSizeText.addActionListener(this);
        sizePanel.add(imgSizeText);

        // Type panel (JPG|GIF|PNG|BMP)
        JPanel typePanel = new JPanel();
        typePanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton jpgRadioBtn = new JRadioButton();
        jpgRadioBtn.setSelected(true);
        jpgRadioBtn.setText(JPG);
        jpgRadioBtn.setBounds(120, 30, 120, 50);
        jpgRadioBtn.addActionListener(this);

        JRadioButton pngRadioBtn = new JRadioButton();
        pngRadioBtn.setText(PNG);
        pngRadioBtn.setBounds(120, 30, 120, 50);
        pngRadioBtn.addActionListener(this);

        JRadioButton gifRadioBtn = new JRadioButton();
        gifRadioBtn.setText(GIF);
        gifRadioBtn.setBounds(120, 30, 120, 50);
        gifRadioBtn.addActionListener(this);

        JRadioButton bmpRadioBtn = new JRadioButton();
        bmpRadioBtn.setText(BMP);
        bmpRadioBtn.setBounds(120, 30, 120, 50);
        bmpRadioBtn.addActionListener(this);

        typePanel.add(jpgRadioBtn);
        typePanel.add(pngRadioBtn);
        typePanel.add(gifRadioBtn);
        typePanel.add(bmpRadioBtn);

        buttonGroup.add(jpgRadioBtn);
        buttonGroup.add(pngRadioBtn);
        buttonGroup.add(gifRadioBtn);
        buttonGroup.add(bmpRadioBtn);

        mainPanel.add(typePanel);
        mainPanel.add(sizePanel);

        return mainPanel;
    }

    private JPanel modePanel() {
        String titlePanel = MODE[language];
        JPanel mainPanel = titlePanel(titlePanel, BoxLayout.Y_AXIS, titleFont, TITLE_COLOR);
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        ButtonGroup buttonGroup = new ButtonGroup();

        mode1RadioBtn = new JRadioButton();
        mode1RadioBtn.setSelected(mode == STRING_INX);
        mode1RadioBtn.setText(STRING_MODE[language]);
        mode1RadioBtn.setBounds(120, 30, 120, 50);
        mode1RadioBtn.addActionListener(this);

        mode2RadioBtn = new JRadioButton();
        mode2RadioBtn.setSelected(mode == TEXT_FILE_INX);
        mode2RadioBtn.setText(TEXT_FILE[language]);
        mode2RadioBtn.setBounds(120, 30, 120, 50);
        mode2RadioBtn.addActionListener(this);

        mode3RadioBtn = new JRadioButton();
        mode3RadioBtn.setSelected(mode == ARCH_FILE_INX);
        mode3RadioBtn.setText(ARCH_FILE[language]);
        mode3RadioBtn.setBounds(120, 30, 120, 50);
        mode3RadioBtn.addActionListener(this);

        mainPanel.add(mode1RadioBtn);
        mainPanel.add(mode2RadioBtn);
        mainPanel.add(mode3RadioBtn);

        buttonGroup.add(mode1RadioBtn);
        buttonGroup.add(mode2RadioBtn);
        buttonGroup.add(mode3RadioBtn);

        return mainPanel;
    }

    private JPanel msgPanel() {
        JPanel msgPanel = new JPanel();
        msgPanel.setBorder(new EmptyBorder(3, 0, 3, 0));
        msgPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        message = new JTextField("", 80);
        message.setBorder(new EmptyBorder(5, 0, 5, 0));
        message.setHorizontalAlignment(JTextField.CENTER);
        message.setFont(new Font("Arial", Font.BOLD, 14));
        message.setForeground(MSG_COLOR);
        message.setEditable(false);

        msgPanel.add(message);

        // Default message
        setMessage(MODE_MSG[0][language], MSG_COLOR);

        return msgPanel;
    }

    private JPanel buttonPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        //--- Left panel ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        mainPanel.add(leftPanel, BorderLayout.WEST);

        // Button 'Info'
        infoButton = new JButton(INFO[language]);
        infoButton.setForeground(Color.decode("#003366"));
        leftPanel.add(infoButton);
        infoButton.addActionListener(this);

        //--- Rigth panel ---
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        mainPanel.add(rightPanel, BorderLayout.EAST);

        // Button 'Clear'
        clearButton = new JButton(CLEAR[language]);
        rightPanel.add(clearButton);
        clearButton.addActionListener(this);

        // Button 'Exit'
        exitButton = new JButton(EXIT[language]);
        rightPanel.add(exitButton);
        exitButton.addActionListener(this);

        // Button 'Run'
        runButton = new JButton(RUN[language] + " \u25B6");
        //runButton.setBackground(Color.BLUE);
        runButton.setForeground(Color.decode("#0000A0"));
        rightPanel.add(runButton);
        runButton.addActionListener(this);

        return mainPanel;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (DEBUG) System.out.println("event: " + event);
        String actionCmd = event.getActionCommand();
        if (DEBUG) System.out.println("actionCmd: " + "");

        // Mode
        if (actionCmd.equals(STRING_MODE[language])) {
            if (mode1RadioBtn.isEnabled()) {
                mode = STRING_INX;
                if (DEBUG) System.out.println("mode: " + mode);
                dataFilePanel.setVisible(false);
                headerPanel.setVisible(false);
                outputPathPanel.setVisible(true);
                folderPanel.setVisible(false);
                textPanel.setVisible(true);
                setMessage(MODE_MSG[0][language], MSG_COLOR);
            }
        } else if (actionCmd.equals(TEXT_FILE[language])) {
            if (mode2RadioBtn.isEnabled()) {
                mode = TEXT_FILE_INX;
                if (DEBUG) System.out.println("mode: " + mode);
                dataFilePanel.setVisible(true);
                headerPanel.setVisible(false);
                outputPathPanel.setVisible(true);
                folderPanel.setVisible(false);
                textPanel.setVisible(false);
                message.setText(MODE_MSG[1][language]);
            }
        } else if (actionCmd.equals(ARCH_FILE[language])) {
            if (mode3RadioBtn.isEnabled()) {
                mode = ARCH_FILE_INX;
                if (DEBUG) System.out.println("mode: " + mode);
                dataFilePanel.setVisible(true);
                headerPanel.setVisible(true);
                folderPanel.setVisible(true);
                outputPathPanel.setVisible(false);
                textPanel.setVisible(false);
                setMessage(MODE_MSG[2][language], MSG_COLOR);
            }
        }

        // Image type
        if (actionCmd.equals(JPG)) {
            imgType = JPG;
        } else if (actionCmd.equals(PNG)) {
            imgType = PNG;
        } else if (actionCmd.equals(GIF)) {
            imgType = GIF;
        } else if (actionCmd.equals(BMP)) {
            imgType = BMP;
        }

        // Confirm/Exit
        if (source == clearButton) {
            clearAll();
        } else if (source == runButton) {
            makeQRCode();
        } else if (source == exitButton) {
            if (DEBUG) System.out.println("exitButton");
            System.exit(0); // End
        } else if (source == infoButton) {
            if (DEBUG) System.out.println("infoButton");
            showInfo();
        }

        // Browse buttons
        if (source == dataFileButton) {
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            int returnValue = jfc.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {

                File selectedFile = jfc.getSelectedFile();
                if (DEBUG) System.out.println(selectedFile.getAbsolutePath());
                // Only filename: jfc.getSelectedFile().getName());
                dataFileText.setText(selectedFile.getAbsolutePath());
            }
        } else if (source == outputPathButton) {
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            jfc.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(imgType, imgType);
            jfc.addChoosableFileFilter(filter);
            int returnValue = jfc.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = jfc.getSelectedFile();
                String pathname = selectedFile.getAbsolutePath();
                if (DEBUG) System.out.println(pathname);
                if (pathname.toLowerCase().endsWith("." + imgType)) {
                    outputPathText.setText(pathname);
                } else {
                    outputPathText.setText(pathname + "." + imgType);
                }
            }
        } else if (source == folderButton) {
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = jfc.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = jfc.getSelectedFile();
                if (DEBUG) System.out.println(selectedFile.getAbsolutePath());
                folderText.setText(selectedFile.getAbsolutePath());
            }
        }
    }

    void makeQRCode() {
        String dataFile = dataFileText.getText();
        String outputPath = outputPathText.getText();
        String folder = folderText.getText();
        String textCode = textArea.getText();
        String header = headerText.getText();
        int imgSize = stringToInt(imgSizeText.getText());

        // Check data
        boolean noInput = ((mode == ARCH_FILE_INX || mode == TEXT_FILE_INX) && dataFile.isEmpty()) ||
                (mode == STRING_INX && textCode.isEmpty());
        boolean noOutput = (mode == STRING_INX || mode == TEXT_FILE_INX) && outputPath.isEmpty() ||
                (imgSize < 10 || imgSize > 4800);
        if (noInput || noOutput) {
            return;
        }

        if ((mode == ARCH_FILE_INX || mode == TEXT_FILE_INX) && !checkPathname(dataFile)) {
            setMessage(ERR_FILE1[language] + dataFile + ERR_FILE2[language], ERR_COLOR);
            return;
        } else if (mode == ARCH_FILE_INX && !checkPathname(folder)) {
            setMessage(ERR_FILE1[language] + folder + ERR_FILE2[language], ERR_COLOR);
            return;
        } else if (textCode.length() > 4296) { // ERR_TEXT_LEN
            setMessage(ERR_TEXT_LEN[language], ERR_COLOR);
            return;
        } else {
            setMessage("", MSG_COLOR); // Clear message area
        }

        String cmd = null;
        int error = 0;
        if (mode == STRING_INX) {
            cmd = "string -s " + textCode + " -o " + outputPath +
                    " -t " + imgType + " -d " + imgSize;
            if (!DEBUG) error = qrcode.saveQRImage(textCode, imgType, imgSize, outputPath);

        } else if (mode == TEXT_FILE_INX) {
            cmd = "textfile -s " + dataFile + " -o " + outputPath +
                    " -t " + imgType + " -d " + imgSize;
            if (!DEBUG) error = qrcode.makeFromFileSingleString(dataFile, outputPath, imgType, imgSize);

        } else if (mode == ARCH_FILE_INX) {
            cmd = "archfile -s " + dataFile + " -o " + folder +
                    " -t " + imgType + " -d " + imgSize;
            if (!DEBUG) error = qrcode.makeFromFileWithManyStrings(dataFile, folder, header, imgType, imgSize);
        }

        if (DEBUG) System.out.println("cmd: " + cmd);

        if (error == 0) {
            setMessage(OK[language], OK_COLOR);
        } else if (error == qrcode.ERR_FILE_NOT_FOUND) {
            setMessage(ERR_FILE1[language] + dataFile + ERR_FILE2[language], Color.RED);
        } else if (error == qrcode.ERR_WRITE_FILE) {
            setMessage(ERR_WRITE[language], ERR_COLOR);
        } else if (error == qrcode.ERR_IO) {
            setMessage(ERR_IO[language], ERR_COLOR);
        } else if (error == qrcode.ERR_ENCODING) {
            setMessage(ERR_ENCODING[language], ERR_COLOR);
        } else {
            setMessage(" Error!", ERR_COLOR);
        }
    }

    void clearAll() {
        dataFileText.setText("");
        outputPathText.setText("");
        folderText.setText("");
        textArea.setText("");
        imgSizeText.setText(DEFAULT_IMG_SIZE);
        message.setText("");
    }

    //------ UtilGUI ------//

    /**
     * @param labelText
     * @param textField
     * @param button
     * @param border    (example: new LineBorder(Color.ORANGE, 1, true))
     * @return
     */
    JPanel pathfilePanel(String labelText, JTextField textField, JButton button, Border border) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Text label
        JPanel labelPanel = customLabelPanel(labelText, 100, labelFont, LABEL_COLOR, FlowLayout.RIGHT);
        mainPanel.add(labelPanel);

        // Text field
        if (border != null) textField.setBorder(border);
        mainPanel.add(textField);

        // Icon button
        if (button != null) {
            button.setMargin(new Insets(0, 0, 0, 0));
            button.setBorder(null);

            FontMetrics fm = new Canvas().getFontMetrics(labelFont);
            int dim = Math.round(1.5f * fm.getHeight());
            ImageIcon icon = resizeIcon(new ImageIcon(FOLDER_URL), dim, dim);
            button.setIcon(icon);
            mainPanel.add(button);
        }

        return mainPanel;
    }

    /**
     * Custom label
     *
     * @param text
     * @param font
     * @param color
     * @return
     */
    JLabel customLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setSize(new Dimension(40, 30));
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    JPanel customLabelPanel(String text, int width, Font font, Color fontColor, int position) {
        int height = 30;
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(position));
        panel.setPreferredSize(new Dimension(width, height));

        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(fontColor);
        panel.add(label);

        return panel;
    }

    private ImageIcon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) {
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    JPanel titlePanel(String title, int orientation, Font font, Color color) {
        JPanel panel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(panel, orientation);
        Border border = titleBorder(title, font, color);
        panel.setLayout(boxLayout);
        panel.setBorder(border);
        return panel;
    }

    Border titleBorder(String title, Font font, Color color) {
        Border border = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), title,
                TitledBorder.CENTER, TitledBorder.TOP, font, color);
        return border;
    }

    void setMessage(String text, Color color) {
        message.setText(text);
        message.setForeground(color);
    }

    void showInfo() {
        String infoPath = new File("info/").getAbsolutePath();
        try {
            URL infoUrl = new URL("file:/" + infoPath + "/" + INFO_FILE + langId + ".html");
            URI uri = infoUrl.toURI();
            if (uri != null) {
                Desktop.getDesktop().browse(uri);
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    URL getUrlPath(String src) {
        URL urlPath = this.getClass().getResource(src);
        return urlPath;
    }

    /**
     * Check if pathname exists
     *
     * @param pathname
     * @return
     */
    public boolean checkPathname(String pathname) {
        return new File(pathname).exists();
    }

    /**
     * Performs the conversion from string to int
     *
     * @param str
     * @return int
     */
    public int stringToInt(String str) {
        int n = -1;
        try {
            n = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            System.out.println("No int!");
        }

        return n;
    }

    // main class
    public static void main(String args[]) {
        new QRCodeGUI();
    }
}
