package com.gytmy.maze.view.settings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.CompletableFuture;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.gytmy.maze.model.player.Player;
import com.gytmy.maze.view.MenuFrameHandler;
import com.gytmy.maze.view.TimerPanel;
import com.gytmy.maze.view.game.Cell;
import com.gytmy.maze.view.game.GameplayStatus;
import com.gytmy.sound.AudioFileManager;
import com.gytmy.sound.AudioRecognitionResult;
import com.gytmy.sound.AudioRecorder;
import com.gytmy.sound.AudioToFile;
import com.gytmy.sound.RecordObserver;
import com.gytmy.sound.User;
import com.gytmy.sound.AlizeRecognitionResultParser.AlizeRecognitionResult;
import com.gytmy.utils.HotkeyAdder;

public class RecognizeUserPage extends JPanel implements RecordObserver {

    private JPanel playerPanel;
    private JLabel playerName;
    private JLabel recordStatus;
    private TimerPanel timerPanel;
    private JLabel triesStatus;

    private Player[] players;

    private int indexCurrentPlayer = 0;
    private Player currentPlayer;

    private static final String TRIES_STATUS_TEXT = "Tries left: ";
    private int recognitionTriesLeft = 3;

    private boolean isRecordingEnabled = true;

    private static final Color BACKGROUND_COLOR = Cell.WALL_COLOR;
    private static final Color FOREGROUND_COLOR = Cell.PATH_COLOR;

    private static final int RECORD_DURATION_SENTENCE_IN_SECONDS = 30;

    private static final String FILE_NAME = "currentGameAudio";
    private static final String AUDIO_GAME_PATH = "src/resources/audioFiles/client/audio/" + FILE_NAME + ".wav";
    private static final int MAX_RECOGNITION_TRIES = 3;

    public RecognizeUserPage(Player[] players) {

        if (players.length == 0) {
            throw new IllegalArgumentException("The array of players must not be empty");
        }

        this.players = players;
        currentPlayer = players[indexCurrentPlayer];

        setLayout(new GridBagLayout());
        setBackground(BACKGROUND_COLOR);
        setVisible(true);
        setPreferredSize(MenuFrameHandler.DEFAULT_DIMENSION);
        setSize(getPreferredSize());

        AudioRecorder.addObserver(this);

        initTriesStatus();
        initPlayerPanel();
        initRecordStatus();
        initTimerPanel();
        initKeyBindRecord();
        placeComp(Box.createVerticalBox(), this, 3, 0, 1, 7, 1.0, 1.0, GridBagConstraints.BOTH);
        placeComp(Box.createHorizontalBox(), this, 0, 1, 1, 6, 1.0, 1.0, GridBagConstraints.BOTH);
        placeComp(Box.createHorizontalBox(), this, 1, 0, 2, 1, 1.0, 1.0, GridBagConstraints.BOTH);
        placeComp(Box.createHorizontalBox(), this, 1, 4, 2, 1, 1.0, 1.0, GridBagConstraints.BOTH);
        placeComp(Box.createHorizontalBox(), this, 1, 6, 2, 1, 1.0, 1.0, GridBagConstraints.BOTH);

        updateGUI();
    }

    private void initTriesStatus() {
        triesStatus = new JLabel(TRIES_STATUS_TEXT + recognitionTriesLeft);
        triesStatus.setForeground(FOREGROUND_COLOR);
        triesStatus.setHorizontalAlignment(JLabel.CENTER);
        placeComp(triesStatus, this, 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NONE);
    }

    private void initPlayerPanel() {
        playerPanel = new JPanel(new BorderLayout());
        playerPanel.setPreferredSize(new Dimension(400, 200));
        playerPanel.setSize(getPreferredSize());
        playerPanel.setBackground(currentPlayer.getColor());
        placeComp(playerPanel, this, 1, 1, 2, 3, 3.0, 4.0, GridBagConstraints.BOTH);

        playerName = new JLabel();
        playerName.setText(currentPlayer.getName());
        playerName.setForeground(FOREGROUND_COLOR);
        playerName.setHorizontalAlignment(JLabel.CENTER);
        playerPanel.add(playerName, BorderLayout.CENTER);
    }

    private void initRecordStatus() {
        recordStatus = new JLabel("RECORD");
        recordStatus.setForeground(FOREGROUND_COLOR);
        updateStatus();
        recordStatus.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (AudioRecorder.isRecording()) {
                    stopTimer();
                } else {
                    startRecord();
                }
            }
        });

        placeComp(recordStatus, this, 1, 5, 1, 1, 1.0, 1.0, GridBagConstraints.NONE);
    }

    private void initKeyBindRecord() {
        HotkeyAdder.addHotkey(this, KeyEvent.VK_SPACE, this::startRecord, "Record Sentence");
    }

    private void initTimerPanel() {
        timerPanel = new TimerPanel(RECORD_DURATION_SENTENCE_IN_SECONDS);
        placeComp(timerPanel, this, 2, 5, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH);
    }

    private void startRecord() {
        AudioRecorder recorder = AudioRecorder.getInstance();

        if (!isRecordingEnabled) {
            return;
        }

        if (AudioRecorder.isRecording()) {
            recorder.finish();
            return;
        }

        timerPanel.start();
        recorder.start(AUDIO_GAME_PATH, RECORD_DURATION_SENTENCE_IN_SECONDS);

        updateStatus();

        new Thread(() -> {
            while (AudioRecorder.isRecording()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stopTimer();
        }).start();
    }

    private void stopTimer() {

        timerPanel.stop();

        try {
            AudioToFile.stop();
        } catch (Exception e) {
        }

        this.remove(timerPanel);
        initTimerPanel();
        updateGUI();
    }

    private void compareAudioWithModel() {
        isRecordingEnabled = false;
        updateStatus();

        CompletableFuture<AlizeRecognitionResult> futureRecognitionResult = AudioRecognitionResult
                .askRecognitionResult();
        futureRecognitionResult.thenAccept(recognitionResult -> {

            if (recognitionResult == null) {
                updateStatus();
            }

            User recognizedUser = AudioFileManager.getUser(recognitionResult.getName());

            if (recognizedUser == null) {
                updateStatus();
            }

            isRecordingEnabled = true;
            updateStatus();
            if (isThatYou(recognizedUser)) {
                changeToNextPlayer();
            }
        });
    }

    /**
     * Returns true if we change to the next player, false otherwise
     * 
     * @param recognizedUser
     * @return
     */
    private boolean isThatYou(User recognizedUser) {

        if (recognizedUser.equals(SettingsMenu.getSelectedUser(currentPlayer))) {
            JOptionPane.showMessageDialog(this, "That's you!\nYou were successfully recognized.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            SettingsMenu.getInstance().updateRecognized(currentPlayer, true);
            return true;
        }

        recognitionTriesLeft--;
        triesStatus.setText(TRIES_STATUS_TEXT + recognitionTriesLeft);

        if (recognitionTriesLeft == 0) {
            JOptionPane.showMessageDialog(this, "You have no more tries.\nYou were not recognized.", "Failure",
                    JOptionPane.ERROR_MESSAGE);

            SettingsMenu.getInstance().updateRecognized(currentPlayer, false);
            return true;
        }

        JOptionPane.showMessageDialog(this, "That's not you!\nTry again.", "Failure",
                JOptionPane.ERROR_MESSAGE);
        updateStatus();
        return false;
    }

    private void changeToNextPlayer() {
        resetToDefault();
        indexCurrentPlayer++;

        if (indexCurrentPlayer < players.length) {
            currentPlayer = players[indexCurrentPlayer];

            playerName.setText(currentPlayer.getName());
            playerPanel.setBackground(currentPlayer.getColor());
        }
    }

    private void resetToDefault() {
        isRecordingEnabled = true;
        recognitionTriesLeft = MAX_RECOGNITION_TRIES;
        triesStatus.setText(TRIES_STATUS_TEXT + recognitionTriesLeft);
    }

    private void updateStatus() {
        Icon iconToSet = GameplayStatus.getStatusAccordingToGameplay(true, AudioRecorder.isRecording(),
                isRecordingEnabled).getIcon();
        recordStatus.setIcon(iconToSet);
    }

    @Override
    public void startRecordUpdate() {
        updateStatus();
    }

    @Override
    public void endRecordUpdate() {
        compareAudioWithModel();
        updateStatus();
    }

    private static void placeComp(Component comp, JPanel panel, int x, int y, int w, int h,
            double wx, double wh, int fill) {
        GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = x;
        cons.gridy = y;
        cons.gridwidth = w;
        cons.gridheight = h;
        cons.weightx = wx;
        cons.weighty = wh;
        panel.add(comp, cons);
    }

    private void updateGUI() {
        updateStatus();
        revalidate();
        repaint();
    }
}
