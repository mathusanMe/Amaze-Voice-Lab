package com.gytmy.labyrinth;

public interface LabyrinthModel {

    /**
     * Gets the copy of the board representing the Labyrinth,
     * 
     * @return the Board according to its Dimension
     */
    Object getBoard();

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
     */
    void movePlayer(Player player, Direction direction);

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

}