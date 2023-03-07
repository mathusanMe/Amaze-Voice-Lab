package com.gytmy.labyrinth.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;

import com.gytmy.sound.AudioFileManager;
import com.gytmy.sound.AudioPlayer;
import com.gytmy.sound.PlayingTimer;
import com.gytmy.sound.User;
import com.gytmy.utils.FileTree;
import com.gytmy.utils.WordsToRecord;

public class AudioMenu extends JPanel {
    private JFrame frame;

    private StartMenu startMenu;

    private JPanel userPanel;
    private JComboBox<User> userSelector;
    private JComboBox<String> wordSelector = new JComboBox<>();

    // User Panel components
    private JButton deleteUserButton;
    private JButton editUserButton;
    private JButton addUserButton;

    // File Tree Panel Components
    private JScrollPane scrollPane;
    private JTree fileNavigator;
    private static final String JTREE_ROOT_PATH = "src/resources/audioFiles/";
    private String actualJTreeRootPath = JTREE_ROOT_PATH;
    private static final User ALL_USERS = new User("ALL", "USERS", 0, "EVERYONE");

    // Word Panel Components
    private JLabel totalOfWords;
    private JButton recordButton;
    private JButton deleteRecord;

    private JProgressBar timeProgress;
    private JLabel labelDuration = new JLabel("00:00");
    private AudioPlayer player = new AudioPlayer();
    private Thread playbackThread;
    private PlayingTimer timer;
    private boolean isPlaying = false;

    private JButton playAndStopButton;

    private String audioToLoad = "";

    // Colors
    private static final Color BUTTON_COLOR = Cell.WALL_COLOR;
    private static final Color TEXT_COLOR = Cell.PATH_COLOR;
    private static final Color BACK_BUTTON_COLOR = Cell.EXIT_CELL_COLOR;

    public AudioMenu(JFrame frame, StartMenu startMenu) {
        this.frame = frame;
        this.startMenu = startMenu;
        this.frame.setTitle("Be AMazed" + "\t( AudioSettings )");

        setLayout(new BorderLayout());

        initUserPanel();
        loadFileNavigator();
        initWordPanel();
    }

    /**
     * User Panel is the top panel of the OptionsMenu.
     * It's used to select a user, add a new one, or edit / delete an existing one.
     */
    private void initUserPanel() {

        userPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        initUserSelector(c);
        initDeleteButton(c);
        initEditButton(c);
        initAddButton(c);

        add(userPanel, BorderLayout.NORTH);
    }

    private void initUserSelector(GridBagConstraints c) {
        userSelector = new JComboBox<>();
        addUsersToJComboBox(userSelector);
        userSelector.addActionListener(e -> userHasBeenChanged());
        userSelector.setBackground(BUTTON_COLOR);
        userSelector.setForeground(TEXT_COLOR);
        addComponentToUserPanel(userSelector, c, 0, 0, 0.46, false);
    }

    private void addUsersToJComboBox(JComboBox<User> userSelector) {
        List<User> users = AudioFileManager.getUsers();
        userSelector.addItem(ALL_USERS);

        for (User user : users) {
            userSelector.addItem(user);
        }
    }

    private void initDeleteButton(GridBagConstraints c) {
        deleteUserButton = new JButton("Delete");
        deleteUserButton.setToolTipText("This will delete the current user and all his recordings");
        deleteUserButton.setEnabled(false);
        deleteUserButton.addActionListener(e -> deleteUser());
        initColors(deleteUserButton);
        addComponentToUserPanel(deleteUserButton, c, 1, 0, 0.1, true);
    }

    private void initEditButton(GridBagConstraints c) {
        editUserButton = new JButton("Edit");
        editUserButton.setToolTipText("This will edit the current user");
        editUserButton.setEnabled(false);
        editUserButton.addActionListener(
                e -> editOrAddUser("Edit User",
                        new EditCreateUsersPage(frame, this, (User) userSelector.getSelectedItem())));
        initColors(editUserButton);
        addComponentToUserPanel(editUserButton, c, 2, 0, 0.1, true);
    }

    private void initAddButton(GridBagConstraints c) {
        addUserButton = new JButton("Add");
        addUserButton.setToolTipText("This will add a new user");
        addUserButton.addActionListener(e -> editOrAddUser("Add New User", new EditCreateUsersPage(frame, this)));
        initColors(addUserButton);
        addComponentToUserPanel(addUserButton, c, 3, 0, 0.1, true);
    }

    private void editOrAddUser(String title, EditCreateUsersPage page) {
        frame.setContentPane(page);
        frame.setSize(800, 500);
        frame.revalidate();
        frame.setTitle(title);
    }

    private void initColors(JComponent component) {
        component.setBackground(BUTTON_COLOR);
        component.setForeground(TEXT_COLOR);
    }

    private void addComponentToUserPanel(JComponent component, GridBagConstraints c, int gridx, int gridy,
            double weightx, boolean setPreferredSize) {

        c.gridx = gridx;
        c.gridy = gridy;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = weightx;

        userPanel.add(component, c);

        if (setPreferredSize) {
            component.setPreferredSize(
                    new Dimension(component.getPreferredSize().height, component.getPreferredSize().height));
        }
    }

    private void userHasBeenChanged() {
        if (!(userSelector.getSelectedItem() instanceof User)) {
            return;
        }

        User user = (User) userSelector.getSelectedItem();

        actualJTreeRootPath = JTREE_ROOT_PATH;
        if (user == ALL_USERS) {
            deleteUserButton.setEnabled(false);
            editUserButton.setEnabled(false);
            recordButton.setEnabled(false);

        } else {
            actualJTreeRootPath += user.getFirstName();
            deleteUserButton.setEnabled(true);
            editUserButton.setEnabled(true);

            if (!wordSelector.getSelectedItem().equals("ALL")) {
                recordButton.setEnabled(true);
            }
        }

        loadFileNavigator();
        loadTotalOfWords();
    }

    private void deleteUser() {

        String confirmationDialog = "Are you sure you want to delete this user? Everything will be lost.";
        int userIsDeleted = JOptionPane.showConfirmDialog(frame, confirmationDialog, "DELETE USER ?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (userIsDeleted == JOptionPane.YES_OPTION) {
            User user = (User) userSelector.getSelectedItem();
            AudioFileManager.removeUser(user);
            userSelector.removeItem(user);
        }
    }

    /**
     * File Navigator Panel is the center panel of the OptionsMenu.
     * It's used to navigate through the user's audio files.
     */
    public void loadFileNavigator() {

        if (scrollPane != null) {
            remove(scrollPane);
        }

        audioToLoad = "";

        if (deleteRecord != null) {
            deleteRecord.setEnabled(false);
        }

        fileNavigator = new FileTree(actualJTreeRootPath);
        fileNavigator.addTreeSelectionListener(e -> {
            if (isPlaying) {
                stop();
            }
            audioToLoad = ((FileTree) fileNavigator).getSelectedFilePath();
            if (audioToLoad.endsWith(".wav")) {
                playAndStopButton.setEnabled(true);
                deleteRecord.setEnabled(true);
            } else {
                playAndStopButton.setEnabled(false);
                deleteRecord.setEnabled(false);
            }
        });

        scrollPane = new JScrollPane(fileNavigator);

        add(scrollPane, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    /**
     * Word Selector Panel is the panel at the right of the OptionsMenu. It's used
     * to select the word among the list to record. It displays the number of
     * records existing for the selected word and the selected user.
     * 
     * You can also go back to the main menu from it.
     */
    private void initWordPanel() {
        JPanel audioPanel = new JPanel(new GridLayout(8, 1));
        audioPanel.setBackground(BUTTON_COLOR);

        initWordSelector(audioPanel);
        initCountOfWords(audioPanel);
        initDeleteRecordButton(audioPanel);
        initRecordButton(audioPanel);
        initLabelDuration(audioPanel);
        initProgressBar(audioPanel);
        initMediaPlayer(audioPanel);
        initBackButton(audioPanel);

        add(audioPanel, BorderLayout.EAST);
    }

    private void initWordSelector(JComponent parentComponent) {
        addWordsToJComboBox(wordSelector);
        wordSelector.addActionListener(e -> wordHasBeenChanged());
        initColors(wordSelector);
        ((JLabel) wordSelector.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        parentComponent.add(wordSelector);
    }

    private void initCountOfWords(JComponent parenComponent) {
        totalOfWords = new JLabel(getTotalOfWords());
        initColors(totalOfWords);
        totalOfWords.setHorizontalAlignment(SwingConstants.CENTER);
        parenComponent.add(totalOfWords);
    }

    private void initRecordButton(JComponent parentComponent) {
        recordButton = new JButton("Record");
        recordButton.setToolTipText("Record a new audio for the selected word");
        recordButton.addActionListener(e -> recordAudio());
        recordButton.setEnabled(false);
        initColors(recordButton);
        parentComponent.add(recordButton);
    }

    private void recordAudio() {
        frame.setContentPane(
                new RecordPage(frame, this, (User) userSelector.getSelectedItem(),
                        (String) wordSelector.getSelectedItem()));
        frame.revalidate();
        frame.setTitle("RECORD STUDIO");
    }

    private void addWordsToJComboBox(JComboBox<String> wordSelector) {

        wordSelector.addItem("ALL");

        WordsToRecord[] words = WordsToRecord.values();
        for (WordsToRecord word : words) {
            wordSelector.addItem(word.name());
        }
    }

    private void wordHasBeenChanged() {
        loadTotalOfWords();

        recordButton.setEnabled(false);
        if (wordSelector.getSelectedItem().equals("ALL")) {
            recordButton.setEnabled(false);
        } else if ((User) userSelector.getSelectedItem() != ALL_USERS) {
            recordButton.setEnabled(true);
        }
    }

    private void loadTotalOfWords() {
        totalOfWords.setText(getTotalOfWords());
    }

    private String getTotalOfWords() {
        if (!(userSelector.getSelectedItem() instanceof User)) {
            return "Not an user";
        }

        String label = "Total of audios : ";

        User user = (User) userSelector.getSelectedItem();
        String word = wordSelector == null ? "ALL" : (String) wordSelector.getSelectedItem();

        if (user == ALL_USERS) {
            return label + getTotalOfWordsForAllUsers(word);
        }

        if (word.equals("ALL")) {
            return label + AudioFileManager.totalNumberOfAudioFilesForUser(user.getFirstName());
        }

        return label + AudioFileManager.numberOfRecordings(user.getFirstName(), word);
    }

    private int getTotalOfWordsForAllUsers(String word) {
        if (word.equals("ALL")) {
            return AudioFileManager.totalNumberOfAudioFiles();
        }

        int totalForASpecificWord = 0;
        for (User usr : AudioFileManager.getUsers()) {
            totalForASpecificWord += AudioFileManager.numberOfRecordings(usr.getFirstName(), word);
        }
        return totalForASpecificWord;
    }

    private void initDeleteRecordButton(JComponent parentComponent) {
        deleteRecord = new JButton("Delete");
        deleteRecord.setToolTipText("Delete the selected audio");
        deleteRecord.setEnabled(false);
        deleteRecord.addActionListener(e -> deleteWAV());
        initColors(deleteRecord);
        parentComponent.add(deleteRecord);
    }

    private void deleteWAV() {
        if (audioToLoad != null) {

            String[] path = audioToLoad.split("/");
            String userFirstName = path[3];
            String word = path[4];

            String wordIndex = extractNumberFromWord(path[5]);

            int totalRecordsBeforeDelete = AudioFileManager.numberOfRecordings(
                    userFirstName, word);
            AudioFileManager.deleteRecording(audioToLoad);

            AudioFileManager.renameAudioFiles(userFirstName, word, Integer.valueOf(wordIndex),
                    totalRecordsBeforeDelete);
            loadFileNavigator();
            loadTotalOfWords();

            playAndStopButton.setEnabled(false);
        }
    }

    private String extractNumberFromWord(String string) {
        String newString = "";

        for (int i = 0; i < string.length(); i++) {
            if (Character.isDigit(string.charAt(i))) {
                newString += string.charAt(i);
            }
        }
        return newString;
    }

    private void initLabelDuration(JComponent parentComponent) {
        initColors(labelDuration);
        labelDuration.setHorizontalAlignment(SwingConstants.CENTER);
        parentComponent.add(labelDuration);
    }

    private void initProgressBar(JComponent parentComponent) {
        timeProgress = new JProgressBar();
        timeProgress.setEnabled(false);
        timeProgress.setValue(0);
        parentComponent.add(timeProgress);
    }

    private void initMediaPlayer(JComponent parentComponent) {
        JPanel playPausePanel = new JPanel(new GridLayout(1, 1));
        playAndStopButton = new JButton("|>");

        initColors(playAndStopButton);

        playAndStopButton.setEnabled(false);
        playAndStopButton.addActionListener(e -> {
            if (!isPlaying) {
                play();
            } else {
                stop();
            }
        });

        playPausePanel.add(playAndStopButton);

        parentComponent.add(playPausePanel);
    }

    private void play() {
        timer = new PlayingTimer(labelDuration, timeProgress);
        timer.start();
        isPlaying = true;
        playbackThread = new Thread(() -> {
            try {
                initAudioPlaying();

            } catch (UnsupportedAudioFileException ex) {
                displayExceptionMessage("The audio format is unsupported!");
            } catch (LineUnavailableException ex) {
                displayExceptionMessage("Could not play the audio file because line is unavailable!");
            } catch (IOException ex) {
                displayExceptionMessage("I/O error while playing the audio file!");
            } finally {
                stop();
            }
        });

        playbackThread.start();
    }

    private void initAudioPlaying()
            throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        playAndStopButton.setText("||");
        playAndStopButton.setEnabled(true);

        player.load(audioToLoad);
        timer.setAudioClip(player.getAudioClip());
        timeProgress.setMaximum((int) player.getClipSecondLength());

        player.play();
    }

    private void displayExceptionMessage(String errorMessage) {
        JOptionPane.showMessageDialog(AudioMenu.this,
                errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void stop() {
        timer.reset();
        timer.interrupt();

        playAndStopButton.setText("|>");
        isPlaying = false;

        player.stop();
        playbackThread.interrupt();
    }

    private void initBackButton(JComponent parentComponent) {
        JButton goBackButton = new JButton("Go back");
        goBackButton.setToolTipText("Go back to start menu");
        goBackButton.addActionListener(e -> goBackToStartMenu());
        goBackButton.setBackground(BACK_BUTTON_COLOR);
        goBackButton.setForeground(TEXT_COLOR);
        parentComponent.add(goBackButton);
    }

    public void goBackToStartMenu() {
        frame.setContentPane(startMenu);
        frame.revalidate();
    }

    public StartMenu getStartMenu() {
        return startMenu;
    }
}