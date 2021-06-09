package editor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {

    private JTextField filenameField;
    private JTextArea textArea;
    private String fileLocation = "";
    private int iterator = 0;
    private LinkedList<Integer[]> matchedIndex;
    private boolean flag;

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

        ImageIcon saveIcon = getResizedImage("resources/save.png", 40,40);
        JButton saveButton = new JButton(saveIcon);
        saveButton.setName("SaveButton");
        saveButton.setPreferredSize(new Dimension(70, 40));
        rightPanel.add(saveButton);
        saveButton.addActionListener(e -> writeToFile());
//        System.out.println(new File("resources/load.png").exists());
        ImageIcon loadIcon = getResizedImage("resources/load.png", 40,40);
        JButton loadButton = new JButton(loadIcon);
        loadButton.setName("LoadButton");
        loadButton.setPreferredSize(new Dimension(70, 40));
        rightPanel.add(loadButton);
        loadButton.addActionListener(e -> readFromFile());

        filenameField = new JTextField();
        filenameField.setName("FilenameField");
        filenameField.setColumns(20);
        rightPanel.add(filenameField);

        ImageIcon searchIcon = getResizedImage("resources/search.png", 40,40);
        JButton startSearchBtn = new JButton(searchIcon);
        startSearchBtn.setName("StartSearchButton");
        startSearchBtn.setPreferredSize(new Dimension(75, 40));
        rightPanel.add(startSearchBtn);

        ImageIcon previousIcon = getResizedImage("resources/previous.png", 40,40);
        JButton previousMatchBtn = new JButton(previousIcon);
        previousMatchBtn.setName("PreviousMatchButton");
        previousMatchBtn.setPreferredSize(new Dimension(90, 40));
        rightPanel.add(previousMatchBtn);


        ImageIcon nextIcon = getResizedImage("resources/next.png", 40,40);
        JButton nextMatchBtn = new JButton(nextIcon);
        nextMatchBtn.setName("NextMatchButton");
        nextMatchBtn.setPreferredSize(new Dimension(70, 40));
        rightPanel.add(nextMatchBtn);


        JCheckBox useRegexp = new JCheckBox("Use Regular Expression");
        useRegexp.setName("UseRegExCheckbox");
        rightPanel.add(useRegexp);

//        startSearchBtn.addActionListener(e -> searchText(useRegexp));
        startSearchBtn.addActionListener(e -> new Thread(new Runnable() {
            @Override
            public void run() {
                flag = false;
                searchText(useRegexp);

                System.out.println(this);
            }
        }).start());

        previousMatchBtn.addActionListener(e -> {
            Integer[] index = prev();
            if (index != null) {
                textArea.setCaretPosition(index[1]);
                textArea.select(index[0], index[1]);
                textArea.grabFocus();
            }
        });

        nextMatchBtn.addActionListener(e -> {
            Integer[] index = next();
            if (index != null) {
                textArea.setCaretPosition(index[1]);
                textArea.select(index[0], index[1]);
                textArea.grabFocus();
            }
            System.out.println(matchedIndex.size());
            System.out.println(iterator);
            if (index != null) System.out.println(index[0] + " " + index[1] + " " + Arrays.toString(matchedIndex.get(iterator)));
            else System.out.println(index);
        });

        searchItem.addActionListener(e -> new Thread(new Runnable() {
            @Override
            public void run() {
                flag = false;
                searchText(useRegexp);

                System.out.println(this);
            }
        }).start());

        previousMatch.addActionListener(e -> {
            Integer[] index = prev();
            if (index != null) {
                textArea.setCaretPosition(index[1]);
                textArea.select(index[0], index[1]);
                textArea.grabFocus();
            }
        });

        nextMatch.addActionListener(e -> {
            Integer[] index = next();
            if (index != null) {
                textArea.setCaretPosition(index[1]);
                textArea.select(index[0], index[1]);
                textArea.grabFocus();
            }//
        });

        useRegex.addActionListener(e -> {
            useRegexp.setSelected(true);
        });


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

    private void searchText(JCheckBox regex) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(this);
                String searchString = filenameField.getText();
                String text = textArea.getText();
                int index = 0;
                matchedIndex = new LinkedList<>();

                if (regex.isSelected()) {
                    Pattern pattern = Pattern.compile(searchString);
                    Matcher matcher = pattern.matcher(text);
                    while (matcher.find()) {
                        System.out.println(matcher.start());
                        matchedIndex.add(new Integer[]{matcher.start(), matcher.end()});
                        flag = true;
                    }
                } else {
                    if (text.contains(searchString)) {
                        while (text.indexOf(searchString, index) >= 0) {
                            index = text.indexOf(searchString, index);
                            matchedIndex.add(new Integer[]{index, index + searchString.length()});
                            index = index + searchString.length();
                            flag = true;
                        }
                    }
                }
            }
        }).start();

        new Thread(() -> {
            while (!flag);
            iterator = 0;
            if (matchedIndex.size() > 0) {
                Integer[] tempIndex = matchedIndex.get(iterator);
                textArea.setCaretPosition(tempIndex[1]);
                textArea.select(tempIndex[0], tempIndex[1]);
                textArea.grabFocus();
            }
        }).start();
//        new Thread(() -> {
//            System.out.println(this);
//            String searchString = filenameField.getText();
//            String text = textArea.getText();
//            int index = 0;
//            matchedIndex = new LinkedList<>();
//
//            if (regex.isSelected()) {
//                Pattern pattern = Pattern.compile(searchString);
//                Matcher matcher = pattern.matcher(text);
//                while (matcher.find()) {
//                    matchedIndex.add(new Integer[]{matcher.start(), matcher.end()});
//                }
//            } else {
//                if (text.contains(searchString)) {
//                    while (text.indexOf(searchString, index) >= 0) {
//                        index = text.indexOf(searchString, index);
//                        matchedIndex.add(new Integer[]{index, index + searchString.length()});
//                        index = index + searchString.length() - 1;
//                    }
//                }
//            }
//
//            iterator = 0;
//            if (matchedIndex.size() > 0) {
//                Integer[] tempIndex = matchedIndex.get(iterator);
//                textArea.setCaretPosition(tempIndex[1]);
//                textArea.select(tempIndex[0], tempIndex[1]);
//                textArea.grabFocus();
//            }
//        }).start();
    }

    public Integer[] next() {
        if (matchedIndex.size() == 0) return null;
        return matchedIndex.get(iterator = (iterator + 1) % matchedIndex.size());
    }

    public Integer[] prev() {
        if (matchedIndex.size() == 0) return null;
        return matchedIndex.get(iterator = (matchedIndex.size() + iterator - 1) % matchedIndex.size());
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

        if (fileLocation.equals("")) {

            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            jfc.setDialogTitle("Choose a directory to save your file: ");
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int returnValue = jfc.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                if (jfc.getSelectedFile().isDirectory()) {
                    fileLocation = jfc.getSelectedFile().getAbsolutePath();
//                    System.out.println("Selectedfile: "+ jfc.getSelectedFile().getAbsolutePath());
                    if (filenameField.getText().equals("")) {
                        fileLocation = jfc.getSelectedFile().getAbsolutePath() + "/newFile.txt";
                    } else {
                        fileLocation = jfc.getSelectedFile().getAbsolutePath() + filenameField.getText() + ".txt";
                    }
                }
            }
        }

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

    private ImageIcon getResizedImage(String file, int width, int height) {
        return new ImageIcon( new ImageIcon(
                file).getImage().getScaledInstance(
                width,height, Image.SCALE_SMOOTH));

    }
}

