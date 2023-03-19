package com.gytmy.labyrinth.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.gytmy.labyrinth.model.LabyrinthModel;

public class GameOverPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = Cell.WALL_COLOR;
    private static final Color FOREGROUND_COLOR = Cell.PATH_COLOR;

    private LabyrinthModel model;
    private JPanel buttonsPanel;
    private JFrame frame;

    public GameOverPanel(LabyrinthModel model, JFrame frame) {
        this.model = model;
        this.frame = frame;

        initComponents();
    }

    private void initComponents() {
        BorderLayout layout = new BorderLayout();
        setLayout(layout);

        setBackground(BACKGROUND_COLOR);

        initHeader();
        initScoreboard();
        initButtons();
    }

    private void initHeader() {
        JLabel header = new JLabel("Game over");
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setBackground(BACKGROUND_COLOR);
        header.setForeground(FOREGROUND_COLOR);

        add(header, BorderLayout.NORTH);
    }

    private void initScoreboard() {
        add(new ScoreBoardPanel(model), BorderLayout.CENTER);
    }

    private void initButtons() {
        buttonsPanel = new JPanel();
        GridLayout buttonsLayout = new GridLayout(1, 3, 5, 5);
        buttonsPanel.setLayout(buttonsLayout);

        initGoToStartMenuButton();
        initPlayAgainButton();
        initQuitButton();

        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void initQuitButton() {
        initButton("Quit", e -> frame.dispose());
    }

    private void initPlayAgainButton() {
        initButton("Play again",
                e -> GameFrameHandler.goToSettingsMenu());
    }

    private void initGoToStartMenuButton() {
        initButton("Go to menu",
                e -> GameFrameHandler.goToStartMenu());
    }

    private void initButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        buttonsPanel.add(button);
    }

}
