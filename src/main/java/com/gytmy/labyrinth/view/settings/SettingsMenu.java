package com.gytmy.labyrinth.view.settings;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.gytmy.labyrinth.view.Cell;
import com.gytmy.labyrinth.view.settings.game_mode.GameModeSelectionPanel;
import com.gytmy.labyrinth.view.settings.player.PlayerSelectionPanel;

public class SettingsMenu extends JPanel {

    private PlayerSelectionPanel playerSelectionPanel;
    private GameModeSelectionPanel gameModeSelectionPanel;
    private JLabel gameGifLabel;
    private JButton startGameButton;

    private static final String ANIMATED_GAME_GIF_PATH = "src/resources/images/settings_menu/MAZE_LOGO_ROTATED.gif";
    private static final Color BACKGROUND_COLOR = Cell.WALL_COLOR;

    public SettingsMenu() {
        setLayout(new GridBagLayout());
        setBackground(BACKGROUND_COLOR);

        initPlayerSelectionPanel();
        initGameGifLabel();
        initGameSelectionPanel();
        initStartGameButton();

        updateGUI();
    }

    private void initPlayerSelectionPanel() {
        playerSelectionPanel = new PlayerSelectionPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        add(playerSelectionPanel, gbc);
    }

    private void initGameGifLabel() {
        gameGifLabel = new JLabel();
        ImageIcon imageIcon = new ImageIcon(ANIMATED_GAME_GIF_PATH);
        gameGifLabel.setIcon(imageIcon);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(gameGifLabel, gbc);
    }

    private void initGameSelectionPanel() {
        gameModeSelectionPanel = new GameModeSelectionPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 20, 20, 20);
        add(gameModeSelectionPanel, gbc);
    }

    private void initStartGameButton() {
        startGameButton = new JButton("Start Game");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.BOTH;
        add(startGameButton, gbc);
    }

    private void startGame() {

    }

    private void updateGUI() {
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        JPanel panel = new SettingsMenu();

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(800, 500));
        frame.add(panel);
        frame.setVisible(true);
    }

}
