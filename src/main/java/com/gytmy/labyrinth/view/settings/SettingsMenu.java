package com.gytmy.labyrinth.view.settings;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.gytmy.labyrinth.controller.LabyrinthController;
import com.gytmy.labyrinth.controller.LabyrinthControllerImplementation;
import com.gytmy.labyrinth.model.GameData;
import com.gytmy.labyrinth.model.gamemode.GameMode;
import com.gytmy.labyrinth.model.gamemode.GameModeData;
import com.gytmy.labyrinth.model.player.Player;
import com.gytmy.labyrinth.view.Cell;
import com.gytmy.labyrinth.view.GameFrameToolbox;
import com.gytmy.labyrinth.view.LabyrinthView;
import com.gytmy.labyrinth.view.settings.gamemode.SelectionPanel;
import com.gytmy.labyrinth.view.settings.player.PlayerSelectionPanel;

/**
 * This class is used to display the settings menu. It is a singleton.
 */
public class SettingsMenu extends JPanel {

    private PlayerSelectionPanel playerSelectionPanel;
    private SelectionPanel gameModeSelectionPanel;
    private JLabel gameGifLabel;
    private JButton startGameButton;

    private static final String ANIMATED_GAME_GIF_PATH = "src/resources/images/settings_menu/MAZE_LOGO_ROTATED.gif";
    private static final Color BACKGROUND_COLOR = Cell.WALL_COLOR;

    private static SettingsMenu instance = null;

    public static SettingsMenu getInstance() {
        if (instance == null) {
            instance = new SettingsMenu();
        }
        return instance;
    }

    private SettingsMenu() {

        setLayout(new GridBagLayout());
        setBackground(BACKGROUND_COLOR);

        initPlayerSelectionPanel();
        initGameGifLabel();
        initGameSelectionPanel();
        initStartGameButton();

        addEscapeKeyBind();

        updateGUI();
    }

    private void initPlayerSelectionPanel() {
        playerSelectionPanel = PlayerSelectionPanel.getInstance();
        GridBagConstraints gbc = getDefaultConstraints(0, 0);
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        add(playerSelectionPanel, gbc);
    }

    private void initGameGifLabel() {
        gameGifLabel = new JLabel();
        ImageIcon imageIcon = new ImageIcon(ANIMATED_GAME_GIF_PATH);
        gameGifLabel.setIcon(imageIcon);
        GridBagConstraints gbc = getDefaultConstraints(0, 1);
        gbc.gridheight = 2;
        add(gameGifLabel, gbc);
    }

    private void initGameSelectionPanel() {
        gameModeSelectionPanel = new SelectionPanel();
        GridBagConstraints gbc = getDefaultConstraints(1, 1);
        gbc.weightx = 0.7;
        gbc.insets = new Insets(20, 20, 20, 20);
        add(gameModeSelectionPanel, gbc);
    }

    private void initStartGameButton() {
        startGameButton = new JButton("Start Game");
        startGameButton.addActionListener(e -> startGame());
        GridBagConstraints gbc = getDefaultConstraints(1, 2);
        gbc.weightx = 0.7;
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(startGameButton, gbc);
    }

    private GridBagConstraints getDefaultConstraints(int gridx, int gridy) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.fill = GridBagConstraints.BOTH;
        return gbc;
    }

    private void startGame() {
        if (!playerSelectionPanel.arePlayersReady()) {
            JOptionPane.showMessageDialog(this, "Not all players are ready", "", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Player[] players = playerSelectionPanel.getSelectedPlayers();
        GameModeData gameModeSettings = gameModeSelectionPanel.getGameModeData();
        GameMode gameMode = gameModeSelectionPanel.getSelectedGameMode();
        GameData gameData = new GameData(gameModeSettings, gameMode, players);

        JFrame frame = GameFrameToolbox.getMainFrame();
        LabyrinthController labyrinthController = new LabyrinthControllerImplementation(gameData, frame);
        LabyrinthView labyrinthView = labyrinthController.getView();

        frame.setContentPane(labyrinthView);
        GameFrameToolbox.frameUpdate("View Labyrinth" + gameMode);
    }

    private void addEscapeKeyBind() {
        // define the action to be performed when the shortcut is pressed
        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                GameFrameToolbox.goToStartMenu();
            }
        };

        // create a KeyStroke object to represent the key combination (Escape key)
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

        // map the key combination to the action using the component's input map
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "customAction");
        getActionMap().put("customAction", action);
    }

    private void updateGUI() {
        revalidate();
        repaint();
    }

}