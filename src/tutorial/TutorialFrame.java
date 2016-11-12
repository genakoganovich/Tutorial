package tutorial;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.logging.Logger;

enum NavigationState {NEXT, PREVIOUS, INDEX, SELECTED}
enum CardState {NOT_ADDED, ADDED}

class TutorialFrame extends JFrame {
    private static final int COLUMNS_NUMBER = 3;
    private JTextArea question;
    private JTextArea answer;
    private Tutorial tutorial;
    private JTextField indexField;
    private JList<Integer> items;
    private static final Logger LOGGER = Logger.getLogger(TutorialFrame.class.getName());
    private DefaultListModel<Integer> listModel;
    private int index;
    private Controller controller;
    private ItemsSelectListener itemsSelectListener;
    private File file;

    TutorialFrame() {
        controller = new Controller();
        tutorial = new Tutorial();
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
        newMenuItem.addActionListener(new NewMenuListener());
        fileMenu.add(newMenuItem);

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(new SaveMenuListener());
        fileMenu.add(saveMenuItem);

        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        saveAsMenuItem.addActionListener(new SaveAsMenuListener());
        fileMenu.add(saveAsMenuItem);

        JMenuItem loadMenuItem = new JMenuItem("Load");
        loadMenuItem.addActionListener(new OpenMenuListener());
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
        private int parseIndex() {
            int givenIndex;
            try {
                givenIndex = Integer.parseInt(indexField.getText());
            } catch (NumberFormatException nfe) {
                givenIndex = index;
            }
            return givenIndex;
        }
        @Override
        public void actionPerformed(ActionEvent e)
        {
            int givenIndex = parseIndex();
            if(givenIndex == index || givenIndex < 0 || givenIndex > tutorial.size() + 1) {
                indexField.setText("" + index);
            } else {
                index = givenIndex;
                controller.setNavigationState(NavigationState.INDEX);
                controller.updateEntry();
                controller.clearCard();
                controller.showCard(index);
            }
        }
    }
    private class NextCardListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            controller.setNavigationState(NavigationState.NEXT);
            controller.updateEntry();
            controller.clearCard();
            controller.showCard();
        }
    }
    private class PreviousCardListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            controller.setNavigationState(NavigationState.PREVIOUS);
            controller.updateEntry();
            controller.clearCard();
            controller.showCard();
        }
    }
    private class SaveMenuListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            controller.updateEntry();
            /*if(file == null) {
                controller.chooseSaveFile();
            }*/
            //controller.saveFile(file);
            controller.setCardState(CardState.ADDED);
        }
    }
    private class SaveAsMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            controller.updateEntry();
            controller.chooseSaveFile();
            controller.saveFile(file);
        }
    }
    private class NewMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            file = null;
            tutorial.clear();
            controller.clearCard();
        }
    }
    private class OpenMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            JFileChooser fileOpen = new JFileChooser();
            fileOpen.showOpenDialog(TutorialFrame.this);
            file = fileOpen.getSelectedFile();
            controller.loadFile(file);
        }
    }
    private class ItemsSelectListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            index = items.getSelectedIndex();
            controller.setNavigationState(NavigationState.SELECTED);
            controller.updateEntry();
            controller.clearCard();
            controller.showCard(index);
        }
    }

    /**
     *  class Controller
     */
    private class Controller {
        private NavigationState navigationState;
        private CardState cardState;

        Controller() {
            navigationState = NavigationState.NEXT;
            cardState = CardState.NOT_ADDED;
        }
        void setNavigationState(NavigationState navigationState) {this.navigationState = navigationState;}
        private void clearCard() {
            question.setText("");
            answer.setText("");
            question.requestFocus();
        }
        void saveFile(File file) {
            tutorial.save(file);
            cardState = CardState.ADDED;
        }
        void setCardState(CardState cardState) {this.cardState = cardState;}
        void chooseSaveFile() {
            JFileChooser fileSave = new JFileChooser();
            fileSave.showSaveDialog(TutorialFrame.this);
            file = fileSave.getSelectedFile();
        }
        void loadFile(File file) {
            tutorial.load(file);
            listModel = createListModel();
            items.removeListSelectionListener(itemsSelectListener);
            items.setModel(listModel);
            items.setSelectedIndex(0);
            items.addListSelectionListener(itemsSelectListener);
            showCard(0);
        }
        void showCard() {
            Entry entry = null;
            if(navigationState == NavigationState.PREVIOUS) {
                entry = tutorial.previous();
            } else if(navigationState == NavigationState.NEXT) {
                entry = tutorial.next();
            }
            showCard(entry);
            updateIndexField(entry);
            updateCardState(entry);
        }
        void showCard(int index) {
            Entry entry = tutorial.get(index);
            showCard(entry);
            updateIndexField(entry);
            updateCardState(entry);
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
                if(navigationState == NavigationState.PREVIOUS) {
                    index = 0;
                } else if(navigationState == NavigationState.NEXT) {
                    index = tutorial.size();
                    listModel.addElement(index);
                } else if(navigationState == NavigationState.INDEX) {
                    listModel.addElement(index);
                }
            }
            if(navigationState != NavigationState.INDEX) {
                indexField.setText("" + index);
            }
            items.removeListSelectionListener(itemsSelectListener);
            items.setSelectedIndex(index);
            items.addListSelectionListener(itemsSelectListener);
        }
        void updateCardState(Entry entry) {
            if(navigationState == NavigationState.PREVIOUS) {
                cardState = CardState.ADDED;
            } else if(navigationState == NavigationState.NEXT || navigationState == NavigationState.INDEX || navigationState == NavigationState.SELECTED) {
                if(entry == null){
                    cardState = CardState.NOT_ADDED;
                } else {
                    cardState = CardState.ADDED;
                }
            }
        }
        Entry createEntry() {return new Entry(question.getText(), answer.getText());}
        void updateEntry() {
            Entry entry = createEntry();
            if(cardState == CardState.ADDED) {
                tutorial.set(entry);
            } else if(cardState == CardState.NOT_ADDED) {
                tutorial.add(entry);
            }
            LOGGER.info(tutorial.toString());
        }
        private DefaultListModel<Integer> createListModel() {
            DefaultListModel<Integer> model = new DefaultListModel<>();
            for (int i = 0; i < tutorial.size(); i++) {
                model.addElement(i);
            }
            return model;
        }
    } // class Controller
} // class TutorialFrame
