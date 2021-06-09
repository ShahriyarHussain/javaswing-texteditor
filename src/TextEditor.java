package editor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;

public class TextEditor extends JFrame {

    private JTextField filenameField;
    private JTextArea textArea;
    private String fileLocation = "";

    public TextEditor() {
        super("Text Editor");
        setMinimumSize(new Dimension(480, 400));
        setPreferredSize(new Dimension(860, 640));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        init();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void init() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new EmptyBorder(15, 0, 8, 0));

        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        menuFile.setName("MenuFile");
        menuFile.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menuFile);

        JMenuItem loadItem = new JMenuItem("Load");
        loadItem.setName("MenuLoad");
        loadItem.setMnemonic(KeyEvent.VK_L);
        loadItem.addActionListener(e -> readFromFile());

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setName("MenuSave");
        saveItem.setMnemonic(KeyEvent.VK_S);
        saveItem.addActionListener(e -> writeToFile());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setName("MenuExit");
        exitItem.setMnemonic(KeyEvent.VK_F4);
        exitItem.addActionListener(e -> System.exit(0));

        menuFile.add(loadItem);
        menuFile.add(saveItem);
        menuFile.addSeparator();
        menuFile.add(exitItem);

        JMenu menuSearch = new JMenu("Search");
        menuSearch.setName("MenuSearch");
        menuSearch.setMnemonic(KeyEvent.VK_S);
        menuBar.add(menuSearch);

        JMenuItem searchItem = new JMenuItem("Start Search");
        searchItem.setName("MenuStartSearch");

        JMenuItem previousMatch = new JMenuItem("Previous Match");
        previousMatch.setName("MenuPreviousMatch");

        JMenuItem nextMatch = new JMenuItem("Next Match");
        nextMatch.setName("MenuNextMatch");

        JMenuItem useRegex = new JMenuItem("Use Regular Expression");
        useRegex.setName("MenuUseRegExp");

        menuSearch.add(searchItem);
        menuSearch.add(previousMatch);
        menuSearch.add(nextMatch);
        menuSearch.add(useRegex);

        setJMenuBar(menuBar);


        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));

        JButton saveButton = new JButton("Save");
        saveButton.setName("SaveButton");
        saveButton.setPreferredSize(new Dimension(70, 25));
        rightPanel.add(saveButton);
        saveButton.addActionListener(e -> writeToFile());

//        ImageIcon loadIcon = new ImageIcon("/Users/shoss/Downloads/loading.png");
        JButton loadButton = new JButton("Load");
        loadButton.setName("LoadButton");
        loadButton.setPreferredSize(new Dimension(70, 25));
        rightPanel.add(loadButton);
        loadButton.addActionListener(e -> readFromFile());

        filenameField = new JTextField();
        filenameField.setName("FilenameField");
        filenameField.setColumns(20);
        rightPanel.add(filenameField);

        JButton startSearchBtn = new JButton("Search");
        startSearchBtn.setName("StartSearchButton");
        startSearchBtn.setPreferredSize(new Dimension(70, 25));
        rightPanel.add(startSearchBtn);
//        startSearchBtn.addActionListener(e -> writeToFile());

//        ImageIcon loadIcon = new ImageIcon("/Users/shoss/Downloads/loading.png");
        JButton previousMatchBtn = new JButton("Previous");
        previousMatchBtn.setName("PreviousMatchButton");
        previousMatchBtn.setPreferredSize(new Dimension(90, 25));
        rightPanel.add(previousMatchBtn);
//        previousMatchBtn.addActionListener(e -> readFromFile());

        JButton nextMatchBtn = new JButton("Next");
        nextMatchBtn.setName("NextMatchButton");
        nextMatchBtn.setPreferredSize(new Dimension(70, 25));
        rightPanel.add(nextMatchBtn);
//        nextMatchBtn.addActionListener(e -> readFromFile());

        JCheckBox useRegexp = new JCheckBox("Use Regular Expression");
        useRegexp.setName("UseRegExCheckbox");
        rightPanel.add(useRegexp);



        panel.add(rightPanel);

        add(panel, BorderLayout.NORTH);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout());
        textPanel.setBorder(new EmptyBorder(7, 20, 20, 20));

        textArea = new JTextArea();
        textArea.setName("TextArea");
        textArea.setColumns(38);
        textArea.setRows(16);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setName("ScrollPane");
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        textPanel.add(scrollPane);

        add(textPanel, BorderLayout.CENTER);
    }

    private void readFromFile() {

        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        this.fileLocation = "";
        int returnValue = jfc.showOpenDialog(null);
        // int returnValue = jfc.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            fileLocation = jfc.getSelectedFile().getAbsolutePath();
            filenameField.setText(fileLocation);
        } else {
            fileLocation = "";
        }

        try (FileReader fileReader = new FileReader(fileLocation)) {
            BufferedReader buff = new BufferedReader(fileReader);
            textArea.read(buff, null);
            textArea.requestFocus();
        } catch (IOException e) {
            textArea.setText("");
            System.out.println(e.getMessage());
//            e.getMessage();
        }

    }

    private void writeToFile() {

//        if (fileLocation.equals("")) {
//
//            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
//            jfc.setDialogTitle("Choose a directory to save your file: ");
//            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//
//            int returnValue = jfc.showSaveDialog(null);
//            if (returnValue == JFileChooser.APPROVE_OPTION) {
//                if (jfc.getSelectedFile().isDirectory()) {
//                    fileLocation = jfc.getSelectedFile().getAbsolutePath();
////                    System.out.println("Selectedfile: "+ jfc.getSelectedFile().getAbsolutePath());
//                    fileLocation = jfc.getSelectedFile().getAbsolutePath()+"/newfile.txt";
//                }
//            }
//        }

//        System.out.println(fileLocation);
        if (fileLocation.equals("")) fileLocation = filenameField.getText();
        try (FileWriter writer = new FileWriter(fileLocation)) {
            BufferedWriter buff = new BufferedWriter(writer);
            textArea.write(buff);
            textArea.requestFocus();
        } catch (IOException e) {

            System.out.println(e.getMessage());
//            e.getMessage();
        }
    }
}

