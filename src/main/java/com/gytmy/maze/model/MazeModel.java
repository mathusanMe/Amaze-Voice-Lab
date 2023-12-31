package com.gytmy.maze.model;

import java.util.List;

import com.gytmy.maze.model.player.Player;
import com.gytmy.maze.model.score.ScoreCalculator;
import com.gytmy.maze.model.score.ScoreType;
import com.gytmy.utils.Coordinates;

public interface MazeModel {

    /**
     * Gets the copy of the board representing the Maze,
     * 
     * @return the Board according to its Dimension
     */
    boolean[][] getBoard();

    Coordinates getInitialCell();

    Coordinates getExitCell();

    Player[] getPlayers();

    int getNbPlayers();

    public List<Player> getPlayersAtCoordinates(Coordinates coordinates);

    boolean isInitialCell(Coordinates coordinates);

    boolean isExitCell(Coordinates coordinates);

    boolean isWall(int x, int y);

    boolean isWall(Coordinates coordinates);

    /**
     * Checks if the given move is valid for the given player
     * depending on its position on the board
     * 
     * The criteria:
     * A move is valid when the future position of the player,
     * after the move, won't be outside of the board
     * and won't be a wall
     * 
     * @param player
     * @param direction of the move
     * @return true if the move is valid according to the criteria;
     *         false otherwise
     */
    boolean isMoveValid(Player player, Direction direction);

    /**
     * Moves the given player to the given direction
     * 
     * @param player
     * @param direction
     * @return true if the move was successful;
     */
    boolean movePlayer(Player player, Direction direction);

    /**
     * Checks if the given player's position is on the exit's position
     * 
     * @param player
     * @return true if the player is at the exit;
     *         false otherwise
     */
    boolean isPlayerAtExit(Player player);

    /**
     * @return true if the game has ended;
     *         false otherwise
     */
    boolean isGameOver();

    /**
     * @return the minimum path length from the initial cell to the exit cell
     */
    int getMinimumPathLength();

    int getScore(Player player);

    /**
     * @return the initialized ScoreCalculator for the given Player
     */
    ScoreCalculator getScoreCalculator(Player player);

    /**
     * @return the initialized ScoreCalculator for the given ScoreType and Player
     */
    ScoreCalculator getScoreCalculator(ScoreType type, Player player);

}
