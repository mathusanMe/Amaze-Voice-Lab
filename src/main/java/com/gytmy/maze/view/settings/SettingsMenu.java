package com.gytmy.maze.view.settings;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.gytmy.maze.controller.MazeController;
import com.gytmy.maze.controller.MazeControllerImplementation;
import com.gytmy.maze.model.GameData;
import com.gytmy.maze.model.gamemode.GameMode;
import com.gytmy.maze.model.gamemode.GameModeData;
import com.gytmy.maze.model.player.Player;
import com.gytmy.maze.view.MenuFrameHandler;
import com.gytmy.maze.view.WaitingMenu;
import com.gytmy.maze.view.game.Cell;
import com.gytmy.maze.view.game.MazeView;
import com.gytmy.maze.view.settings.gamemode.SelectionPanel;
import com.gytmy.maze.view.settings.player.PlayerSelectionPanel;
import com.gytmy.sound.AudioRecorder;
import com.gytmy.sound.ModelManager;
import com.gytmy.sound.User;
import com.gytmy.utils.HotkeyAdder;
import com.gytmy.utils.ImageManipulator;

/**
 * This class is used to display the settings menu. It is a singleton.
 */
public class SettingsMenu extends JPanel {

    private PlayerSelectionPanel playerSelectionPanel;
    private SelectionPanel gameModeSelectionPanel;
    private GameGIFLabel gameGifLabel;
    private JLabel startGameButton;

    private int playerCount;
    private int comparedPlayerCount = 0;
    private int recognizedPlayerCount = 0;

    private static final Color BACKGROUND_COLOR = Cell.WALL_COLOR;
    private static final String START_GAME_BUTTON_IMAGE_PATH = "src/resources/images/settings_menu/StartButton.png";

    private RecognizeUserPage recognizeUserPage;
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
        gameGifLabel = GameGIFLabel.getInstance();
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

        startGameButton = new JLabel();
        startGameButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                startGame();
            }
        });

        startGameButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                SettingsMenu.getInstance().setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                SettingsMenu.getInstance().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        startGameButton
                .setIcon(ImageManipulator.resizeImage(START_GAME_BUTTON_IMAGE_PATH, 128, 56));

        GridBagConstraints gbc = getStartButtonGridBagConstraints();
        add(startGameButton, gbc);
    }

    public void updateStartButtonPosition() {
        GridBagConstraints gbc = getStartButtonGridBagConstraints();
        remove(startGameButton);
        add(startGameButton, gbc);
        updateGUI();
    }

    private GridBagConstraints getStartButtonGridBagConstraints() {
        GridBagConstraints gbc = getDefaultConstraints(1, 2);
        gbc.weightx = 0.7;
        gbc.insets = new Insets(20,
                (MenuFrameHandler.getMainFrame().getWidth() - gameGifLabel.getIcon().getIconWidth() - 128) / 2, 20, 0);
        return gbc;
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
            JOptionPane.showMessageDialog(this, "Not all players are ready", "Message", JOptionPane.WARNING_MESSAGE);
            return;
        }

        playerCount = playerSelectionPanel.getSelectedPlayers().length;

        List<User> users = playerSelectionPanel.getSelectedUsers();

        // Handle model creation prompting
        if (!User.areUpToDate(users)) {
            promptUserToCreateModelOfAllUsers();
        } else {
            launchRecognition();
        }
    }

    private void launchRecognition() {
        Player[] players = playerSelectionPanel.getSelectedPlayers();
        recognizePlayers(players);
    }

    private void recognizePlayers(Player[] players) {
        recognizeUserPage = new RecognizeUserPage(players);

        recognizeUserPage.setPreferredSize(MenuFrameHandler.getMainFrame().getSize());
        MenuFrameHandler.getMainFrame().setContentPane(recognizeUserPage);
        MenuFrameHandler.frameUpdate("Recognize Players");
    }

    private void launchGame() {

        Player[] players = playerSelectionPanel.getSelectedPlayers();

        GameModeData gameModeSettings = gameModeSelectionPanel.getGameModeData();
        GameMode gameMode = gameModeSelectionPanel.getSelectedGameMode();
        GameData gameData = new GameData(gameModeSettings, gameMode, players);

        JFrame frame = MenuFrameHandler.getMainFrame();
        MazeController mazeController = new MazeControllerImplementation(gameData, frame);
        MazeView mazeView = mazeController.getView();

        playerSelectionPanel.setPlayersToUnready();

        frame.setContentPane(mazeView);

        MenuFrameHandler.frameUpdate(gameMode.toString());

        mazeView.setGamePreferredSize(frame.getSize());
    }

    public void updateRecognized(Player player, boolean recognized) {
        comparedPlayerCount++;
        if (recognized) {
            recognizedPlayerCount++;
        } else {
            playerSelectionPanel.remove(player);
        }

        if (comparedPlayerCount == playerCount) {
            AudioRecorder.removeObserver(recognizeUserPage);

            if (recognizedPlayerCount == 0) {
                MenuFrameHandler.goToSettingsMenu();
                JOptionPane.showMessageDialog(this, "No players recognized", "Message", JOptionPane.WARNING_MESSAGE);
                return;
            }

            comparedPlayerCount = 0;
            recognizedPlayerCount = 0;
            launchGame();
        }
    }

    private void promptUserToCreateModelOfAllUsers() {
        int recreateValue = JOptionPane.showConfirmDialog(
                this,
                "At least one selected player's model is not up-to-date.\nWould you like to recreate all the users' models?\nThe game will, most likely, not work properly if you don't.",
                "The models are not up-to-date",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (recreateValue == JOptionPane.YES_OPTION) {

            JPanel queuePanel = new WaitingMenu();

            MenuFrameHandler.getMainFrame().setContentPane(queuePanel);

            ModelManager.recreateModelOfAllUsers(this::launchGame);

        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "The models have not been recreated.",
                    "Models recreation : Skipped",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addEscapeKeyBind() {
        HotkeyAdder.addHotkey(this, KeyEvent.VK_ESCAPE, SettingsMenu::goToStartMenu, "Go to Start Menu");
    }

    private void updateGUI() {
        revalidate();
        repaint();
    }

    /**
     * Updates the users in the player selection panel. Ths is used when a new user
     * is created or when the state of a user is changed, for example when a user is
     * deleted or they change their name.
     */
    public void updateUsers() {
        playerSelectionPanel.updateUsers();
    }

    private static void goToStartMenu() {

        SettingsMenu instance = SettingsMenu.getInstance();
        instance.playerSelectionPanel.setPlayersToUnready();
        MenuFrameHandler.goToStartMenu();
    }

    public static Object getSelectedUser(Player currentPlayer) {
        return instance.playerSelectionPanel.getSelectedUser(currentPlayer);
    }
}
