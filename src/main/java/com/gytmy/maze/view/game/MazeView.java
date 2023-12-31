package com.gytmy.maze.view.game;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.gytmy.maze.controller.MazeController;
import com.gytmy.maze.model.Direction;
import com.gytmy.maze.model.gamemode.GameMode;
import com.gytmy.maze.model.player.Player;

public abstract class MazeView extends JPanel {

    public abstract void update(Player player, Direction direction);

    public abstract MazePanel getMazePanel();

    public abstract int getTimerCounterInSeconds();

    public abstract boolean isTimerCounting();

    public abstract void startTimer();

    public abstract void stopTimer();

    public abstract void showGameOverPanel();

    public abstract void notifyGameStarted();

    public abstract void notifyGameOver();

    public abstract GameMode getGameMode();

    public abstract void toggleKeyboardMovement(boolean enabled);

    public abstract Dimension getGamePreferredSize();

    public abstract void setGamePreferredSize(Dimension dimension);

    public abstract void updateRecordStatus(GameplayStatus status);

    public abstract JComponent getKeyboardMovementSwitchPanel();

    public abstract MazeController getController();

    public abstract JComponent getRecordStatusPanel();

    public abstract void updatePlayerInfoPanel(Player player);
}
