package tutorial;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.logging.Logger;

class TutorialFrame extends JFrame {
    private JTextArea question;
    private JTextArea answer;
    private Tutorial tutorial;
    private JTextField indexField;
    private boolean currentCardAdded;
    private JList<Integer> items;
    private static final Logger LOGGER = Logger.getLogger(TutorialFrame.class.getName());
    private DefaultListModel<Integer> listModel;
    private int index;

    TutorialFrame() {
        tutorial = new Tutorial();
        currentCardAdded = false;
        setJMenuBar(createMenuBar());
        getContentPane().add(BorderLayout.CENTER, createMainPanel());
        getContentPane().add(BorderLayout.SOUTH, createButtonPanel());
        getContentPane().add(BorderLayout.EAST, createListPanel());
        setSize(500, 600);
        setVisible(true);
    }
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        newMenuItem.addActionListener(new NewMenuListener());
        saveMenuItem.addActionListener(new SaveMenuListener());
        JMenuItem loadMenuItem = new JMenuItem("Load");
        loadMenuItem.addActionListener(new OpenMenuListener());
        fileMenu.add(newMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        menuBar.add(fileMenu);
        return menuBar;
    }
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        index = 0;
        indexField = new JTextField("" + index);
        indexField.setColumns(3);
        JButton previousButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");
        buttonPanel.add(indexField);
        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);
        previousButton.addActionListener(new PreviousCardListener());
        nextButton.addActionListener(new NextCardListener());
        Action action = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("some action");
                int givenIndex;
                try {
                    givenIndex = Integer.parseInt(indexField.getText());
                } catch (NumberFormatException nfe) {
                    givenIndex = index;
                }
                if(givenIndex == index || givenIndex < 0 || givenIndex > tutorial.size()) {
                    indexField.setText("" + index);
                }
            }
        };
        indexField.addActionListener(action);
        return buttonPanel;
    }
    private JPanel createListPanel() {
        JPanel listPanel = new JPanel();
        listModel = new DefaultListModel<>();
        listModel.addElement(0);
        items = new JList<>(listModel);
        items.setSelectedIndex(0);
        items.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        JScrollPane listScroller = new JScrollPane(items);
        listPanel.add(listScroller);
        listScroller.setPreferredSize(new Dimension(50, 300));
        return listPanel;
    }
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        Font bigFont = new Font("sanserif", Font.BOLD, 24);
        question = new JTextArea(6, 20);
        question.setLineWrap(true);
        question.setWrapStyleWord(true);
        question.setFont(bigFont);

        JScrollPane qScroller = new JScrollPane(question);
        qScroller.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        answer = new JTextArea(6, 20);
        answer.setLineWrap(true);
        answer.setWrapStyleWord(true);
        answer.setFont(bigFont);

        JScrollPane aScroller = new JScrollPane(answer);
        aScroller.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        aScroller.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JLabel qLabel = new JLabel("Question:");
        JLabel aLabel = new JLabel("Answer:");
        mainPanel.add(qLabel);
        mainPanel.add(qScroller);
        mainPanel.add(aLabel);
        mainPanel.add(aScroller);
        return mainPanel;
    }
    private Entry createEntry() {return new Entry(question.getText(), answer.getText());}
    private void updateEntryNext() {
        Entry entry = createEntry();
        if(currentCardAdded) {
            tutorial.set(entry);
        } else  {
            tutorial.add(entry);
        }
        clearCard();
        LOGGER.info(tutorial.toString());
    }
    private void updateEntryPrevious() {
        Entry entry = createEntry();
        if(currentCardAdded) {
            tutorial.set(entry);
        } else  {
            tutorial.add(entry);
        }
        clearCard();
        LOGGER.info(tutorial.toString());
    }
    private class NextCardListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            updateEntryNext();
            showNextCard();
        }
    }
    private class PreviousCardListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            updateEntryPrevious();
            showPreviousCard();
        }
    }
    private class SaveMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            Entry entry = createEntry();
            if(currentCardAdded) {
                tutorial.set(entry);
            } else {
                tutorial.add(entry);
                currentCardAdded = true;
            }

            JFileChooser fileSave = new JFileChooser();
            fileSave.showSaveDialog(TutorialFrame.this);
            saveFile(fileSave.getSelectedFile());
        }
    }
    private class NewMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            tutorial.clear();
            clearCard();
        }
    }
    private void clearCard() {
        question.setText("");
        answer.setText("");
        question.requestFocus();
    }
    private void saveFile(File file) {
        tutorial.save(file);
        currentCardAdded = true;
    }
    private class OpenMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            JFileChooser fileOpen = new JFileChooser();
            fileOpen.showOpenDialog(TutorialFrame.this);
            loadFile(fileOpen.getSelectedFile());
        }
    }
    private void loadFile(File file) {
        tutorial.load(file);
        showNextCard();
    }
    private void showNextCard() {
        Entry entry = tutorial.next();
        showCard(entry);
        updateIndexFieldNext(entry);
        updateCurrentCardAddedNext(entry);
    }
    private void showPreviousCard() {
        Entry entry = tutorial.previous();
        showCard(entry);
        updateIndexFieldPrevious(entry);
        updateCurrentCardAddedPrevious(entry);
    }
    private void showCard(Entry entry) {
        if(entry != null) {
            question.setText(entry.getQuestion());
            answer.setText(entry.getAnswer());
        }
    }
    private void updateIndexFieldNext(Entry entry) {
        if(entry != null) {
            index = tutorial.indexOf(entry);
            indexField.setText("" + index);
            items.setSelectedIndex(index);

        } else {
            index = tutorial.size();
            indexField.setText("" + index);
            listModel.addElement(index);
            items.setSelectedIndex(index);
        }
    }
    private void updateIndexFieldPrevious(Entry entry) {
        if(entry != null) {
            index = tutorial.indexOf(entry);
            indexField.setText("" + index);
            items.setSelectedIndex(index);
        } else {
            index = 0;
            indexField.setText("" + index);
            items.setSelectedIndex(index);
        }
    }
    private void updateCurrentCardAddedNext(Entry entry) {
        currentCardAdded = (entry != null);
    }
    private void updateCurrentCardAddedPrevious(Entry entry) {
        currentCardAdded = true;
    }
}
