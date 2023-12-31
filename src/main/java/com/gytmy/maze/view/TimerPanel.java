package com.gytmy.maze.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.gytmy.maze.controller.MazeController;
import com.gytmy.maze.view.game.Cell;

public class TimerPanel extends JPanel implements ActionListener {
    private JLabel timerLabel;
    private Timer timer;
    private MazeController controller;

    private static final Color BACKGROUND_COLOR = Cell.WALL_COLOR;
    private static final Color COUNTDOWN_COLOR = Cell.EXIT_CELL_COLOR;
    private static final Color FOREGROUND_COLOR = Cell.PATH_COLOR;

    private static final String FORMAT = "[ %02d:%02d ]";

    private int counterInSeconds;
    private int countdownInSeconds;

    public static final int DEFAULT_COUNTDOWN_TIME_SECONDS = 3;
    public static final int DEFAULT_STARTING_TIME_SECONDS = 0;

    public static final Font FONT = new Font("Arial", Font.BOLD, 20);

    // Flag to know if the timer is counting or not, it is used to avoid
    // starting the timer twice or starting it before the countdown is over.
    private boolean isCounting = false;

    public TimerPanel() {
        this(DEFAULT_COUNTDOWN_TIME_SECONDS, DEFAULT_STARTING_TIME_SECONDS);
    }

    public TimerPanel(MazeController controller) {
        this(DEFAULT_COUNTDOWN_TIME_SECONDS, DEFAULT_STARTING_TIME_SECONDS);
        this.controller = controller;
    }

    public TimerPanel(int countdownTime, MazeController controller) {
        this(countdownTime, DEFAULT_STARTING_TIME_SECONDS);
        this.controller = controller;
    }

    public TimerPanel(int countdownTime) {
        this(countdownTime, DEFAULT_STARTING_TIME_SECONDS);
    }

    public TimerPanel(int countdownTimeInSeconds, int startingTimeInSeconds) {
        countdownInSeconds = countdownTimeInSeconds;
        counterInSeconds = startingTimeInSeconds;

        setBackground(BACKGROUND_COLOR);

        timerLabel = new JLabel(getStringTime(countdownInSeconds));
        timerLabel.setFont(FONT);
        timerLabel.setForeground(COUNTDOWN_COLOR);
        add(timerLabel);

        initTimerCountdown();

    }

    private void initTimerCountdown() {
        // Timer for the countdown
        timer = new Timer(1000, event -> {
            if (countdownInSeconds > 0) {
                countdownInSeconds--;
                timerLabel.setText(getStringTime(countdownInSeconds));

            } else {
                // If the countdown is over, the timer is reset
                // and the timer for the game starts
                timer.stop();

                if (controller != null) {
                    controller.notifyGameStarted();
                }

                timerLabel.setForeground(FOREGROUND_COLOR);
                timerLabel.setText(getStringTime(counterInSeconds));
                timer = new Timer(1000, this);
                isCounting = true;
                timer.start();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isCounting) {
            return;
        }
        counterInSeconds++;
        timerLabel.setText(getStringTime(counterInSeconds));
    }

    private String getStringTime(int timeInSeconds) {
        int minutes = (timeInSeconds % 3600) / 60;
        int seconds = timeInSeconds % 60;
        return String.format(FORMAT, minutes, seconds);
    }

    public void start() {
        if (isCounting) {
            return;
        }
        if (countdownInSeconds <= 0) {
            isCounting = true;
        }
        timer.start();
    }

    public void stop() {
        isCounting = false;
        timer.stop();
    }

    public boolean isCounting() {
        return isCounting;
    }

    public int getCounterInSeconds() {
        return counterInSeconds;
    }
}
