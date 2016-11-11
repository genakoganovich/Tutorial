package tutorial;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.logging.Logger;

enum State {NEXT, PREVIOUS, INDEX, SELECTED}

class TutorialFrame extends JFrame {
    private static final int COLUMNS_NUMBER = 3;
    private JTextArea question;
    private JTextArea answer;
    private Tutorial tutorial;
    private JTextField indexField;
    private boolean currentCardAdded;
    private JList<Integer> items;
    private static final Logger LOGGER = Logger.getLogger(TutorialFrame.class.getName());
    private DefaultListModel<Integer> listModel;
    private int index;
    private Controller controller;
    private ItemsSelectListener itemsSelectListener;
    TutorialFrame() {
        controller = new Controller();
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
        indexField.setColumns(COLUMNS_NUMBER);
        JButton previousButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");
        buttonPanel.add(indexField);
        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);
        previousButton.addActionListener(new PreviousCardListener());
        nextButton.addActionListener(new NextCardListener());
        Action action = new IndexFieldListener();
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
        itemsSelectListener = new ItemsSelectListener();
        items.addListSelectionListener(itemsSelectListener);
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

    /**
     *  Listeners
     */
    private class IndexFieldListener extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            int givenIndex;
            try {
                givenIndex = Integer.parseInt(indexField.getText());
            } catch (NumberFormatException nfe) {
                givenIndex = index;
            }
            if(givenIndex == index || givenIndex < 0 || givenIndex > tutorial.size() + 1) {
                indexField.setText("" + index);
            } else {
                index = givenIndex;
                controller.setState(State.INDEX);
                controller.updateEntry();
                controller.showCard(index);
            }
        }
    }
    private class NextCardListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            controller.setState(State.NEXT);
            controller.updateEntry();
            controller.showCard();
        }
    }
    private class PreviousCardListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            controller.setState(State.PREVIOUS);
            controller.updateEntry();
            controller.showCard();
        }
    }
    private class SaveMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            Entry entry = controller.createEntry();
            if(currentCardAdded) {
                tutorial.set(entry);
            } else {
                tutorial.add(entry);
                currentCardAdded = true;
            }

            JFileChooser fileSave = new JFileChooser();
            fileSave.showSaveDialog(TutorialFrame.this);
            controller.saveFile(fileSave.getSelectedFile());
        }
    }
    private class NewMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            tutorial.clear();
            controller.clearCard();
        }
    }
    private class OpenMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            JFileChooser fileOpen = new JFileChooser();
            fileOpen.showOpenDialog(TutorialFrame.this);
            controller.loadFile(fileOpen.getSelectedFile());
        }
    }
    private class ItemsSelectListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            index = items.getSelectedIndex();
            controller.setState(State.SELECTED);
            controller.updateEntry();
            controller.showCard(index);
        }
    }

    /**
     *  class Controller
     */
    private class Controller {
        private State state;

        Controller() {state = State.NEXT;}
        void setState(State state) {this.state = state;}
        private void clearCard() {
            question.setText("");
            answer.setText("");
            question.requestFocus();
        }
        void saveFile(File file) {
            tutorial.save(file);
            currentCardAdded = true;
        }
        void loadFile(File file) {
            tutorial.load(file);
            setState(State.NEXT);
            showCard();
        }
        void showCard() {
            Entry entry = null;
            if(state == State.PREVIOUS) {
                entry = tutorial.previous();
            } else if(state == State.NEXT) {
                entry = tutorial.next();
            }
            showCard(entry);
            updateIndexField(entry);
            updateCurrentCardAdded(entry);
        }
        void showCard(int index) {
            Entry entry = tutorial.get(index);
            showCard(entry);
            updateIndexField(entry);
            updateCurrentCardAdded(entry);
        }
        void showCard(Entry entry) {
            if(entry != null) {
                question.setText(entry.getQuestion());
                answer.setText(entry.getAnswer());
            }
        }
        void updateIndexField(Entry entry) {
            if(entry != null) {
                index = tutorial.indexOf(entry);
            } else {
                if(state == State.PREVIOUS) {
                    index = 0;
                } else if(state == State.NEXT) {
                    index = tutorial.size();
                    listModel.addElement(index);
                } else if(state == State.INDEX) {
                    listModel.addElement(index);
                }
            }
            if(state != State.INDEX) {
                indexField.setText("" + index);
            }
            items.removeListSelectionListener(itemsSelectListener);
            items.setSelectedIndex(index);
            items.addListSelectionListener(itemsSelectListener);
        }
        void updateCurrentCardAdded(Entry entry) {
            if(state == State.PREVIOUS) {
                currentCardAdded = true;
            } else if(state == State.NEXT || state == State.INDEX || state == State.SELECTED) {
                currentCardAdded = (entry != null);
            }
        }
        Entry createEntry() {return new Entry(question.getText(), answer.getText());}
        void updateEntry() {
            Entry entry = createEntry();
            if(currentCardAdded) {
                tutorial.set(entry);
            } else  {
                tutorial.add(entry);
            }
            clearCard();
            LOGGER.info(tutorial.toString());
        }
    }
}
