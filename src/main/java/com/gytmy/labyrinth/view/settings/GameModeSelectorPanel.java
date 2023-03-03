package com.gytmy.labyrinth.view.settings;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.gytmy.labyrinth.model.LabyrinthModelFactory;
import com.gytmy.utils.input.UserInputFieldNumberInBounds;

public class GameModeSelectorPanel extends JPanel {

    private GameModeSelector gameModeSelector;
    protected JPanel gameModeSettingsPanel;

    enum GameMode {
        CLASSIC,
    }

    public GameModeSelectorPanel() {
        initGameModeSelector();
        initGameModeSettingsPanel();
    }

    private void initGameModeSelector() {
        gameModeSelector = new GameModeSelector();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        add(gameModeSelector, gbc);
        revalidate();
        repaint();
    }

    private void initGameModeSettingsPanel() {
        gameModeSettingsPanel = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.PAGE_END;
        gbc.weightx = 1;
        gbc.weighty = 1;
        add(gameModeSettingsPanel, gbc);
        gameModeSelector.updateGameModeSettingsPanel((GameMode) gameModeSelector.getSelectedItem());
        revalidate();
        repaint();
    }

    public class GameModeSelector extends JComboBox<GameMode> {
        public GameModeSelector() {
            for (GameMode gameMode : GameMode.values()) {
                addItem(gameMode);
            }

            addActionListener(e -> {
                System.out.println("Game mode changed to " + getSelectedItem());
                GameMode gameMode = (GameMode) getSelectedItem();
                updateGameModeSettingsPanel(gameMode);
            });
        }

        private void updateGameModeSettingsPanel(GameMode gameMode) {
            GameModeSettingsPanelHandler handler = GameModeSettingsPanelFactory
                    .getGameModeSettingsPanel(gameMode);
            handler.initPanel(gameModeSettingsPanel);

        }

    }

    public static class GameModeSettingsPanelFactory {
        private GameModeSettingsPanelFactory() {
        }

        public static GameModeSettingsPanelHandler getGameModeSettingsPanel(GameMode gameMode) {
            switch (gameMode) {
                case CLASSIC:
                    return new ClassicGameModeSettingsPanelHandler();
                default:
                    throw new IllegalArgumentException("Game mode not supported");
            }
        }

    }

    public static interface GameModeSettingsPanelHandler {

        public void initPanel(JPanel settingsPanel);

        public void cleanPanel(JPanel settingsPanel);

        public SettingsData getSettingsData();

    }

    public static class ClassicGameModeSettingsPanelHandler implements GameModeSettingsPanelHandler {

        private JTextField widthInputField;
        private JLabel widthLabel;
        private JTextField heightInputField;
        private JLabel heightLabel;

        public ClassicGameModeSettingsPanelHandler() {
            widthInputField = new UserInputFieldNumberInBounds(LabyrinthModelFactory.MINIMUM_WIDTH_2D,
                    LabyrinthModelFactory.MAXIMUM_SIZE).getTextField();
            widthLabel = new JLabel("Width: ");
            heightInputField = new UserInputFieldNumberInBounds(LabyrinthModelFactory.MINIMUM_WIDTH_2D,
                    LabyrinthModelFactory.MAXIMUM_SIZE).getTextField();
            heightLabel = new JLabel("Height: ");
        }

        @Override
        public void initPanel(JPanel settingsPanel) {
            settingsPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            settingsPanel.add(widthLabel, gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            gbc.weighty = 0.5;
            settingsPanel.add(widthInputField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 0.3;
            gbc.weighty = 0.5;
            settingsPanel.add(heightLabel, gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            settingsPanel.add(heightInputField, gbc);
        }

        @Override
        public void cleanPanel(JPanel settingsPanel) {
            settingsPanel.remove(widthLabel);
            settingsPanel.remove(widthInputField);
            settingsPanel.remove(heightLabel);
            settingsPanel.remove(heightInputField);

        }

        @Override
        public SettingsData getSettingsData() {
            return new ClassicSettingsData(Integer.parseInt(widthInputField.getText()),
                    Integer.parseInt(heightInputField.getText()));
        }

    }

    public static interface SettingsData {

    }

    public static class ClassicSettingsData implements SettingsData {

        private int width;
        private int height;

        public ClassicSettingsData(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

    }

    public static void main(String[] args) {
        GameModeSelectorPanel panel = new GameModeSelectorPanel();

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(800, 600));
        frame.add(panel);
        frame.setVisible(true);

    }
}
