package com.gytmy.maze.view.game;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;

import com.gytmy.maze.model.Direction;
import com.gytmy.maze.model.MazeModel;
import com.gytmy.maze.model.player.Player;
import com.gytmy.utils.Coordinates;

public class MazePanel extends JPanel {

    private MazeModel model;
    private int nbRows;
    private int nbCols;
    private Cell[][] cells;

    public MazePanel(MazeModel model) {
        this.model = model;
        this.nbRows = model.getBoard().length;
        this.nbCols = model.getBoard()[0].length;
        this.cells = new Cell[nbRows][nbCols];

        setLayout(new GridLayout(nbRows, nbCols));
        initCells();
        Dimension preferredSize = new Dimension(
                Cell.CELL_SIZE * nbCols,
                Cell.CELL_SIZE * nbRows);
        setPreferredSize(preferredSize);
    }

    private void initCells() {
        for (int row = 0; row < nbRows; ++row) {
            for (int col = 0; col < nbCols; ++col) {
                initNewCell(col, row);
            }
        }
    }

    private void initNewCell(int col, int row) {
        Coordinates coordinates = new Coordinates(col, row);
        List<Player> players = model.getPlayersAtCoordinates(coordinates);
        Cell cell = new Cell(coordinates, players, model);
        add(cell);
        cells[row][col] = cell;
    }

    public void update(Player player, Direction direction) {
        removePlayerFromPreviousCell(player, direction);
        addPlayerInNewCell(player);
    }

    private void removePlayerFromPreviousCell(Player player, Direction direction) {
        Cell playerPreviousCell = getPlayerPreviousCell(player, direction);
        playerPreviousCell.removePlayer(player);
        playerPreviousCell.update();
    }

    private Cell getPlayerPreviousCell(Player player, Direction direction) {
        Coordinates coordinates = getPlayerPreviousCoordinates(player, direction);
        return getCell(coordinates);
    }

    private Coordinates getPlayerPreviousCoordinates(Player player, Direction direction) {
        Coordinates coordinates = player.getCoordinates();
        switch (direction) {
            case UP:
            case DOWN:
                coordinates.setY(coordinates.getY() - direction.getStep());
                break;
            case LEFT:
            case RIGHT:
                coordinates.setX(coordinates.getX() - direction.getStep());
                break;
        }
        return coordinates;
    }

    private void addPlayerInNewCell(Player player) {
        Cell playerNewCell = getPlayerNewCell(player);
        playerNewCell.addPlayer(player);
        playerNewCell.update();
    }

    private Cell getPlayerNewCell(Player player) {
        Coordinates coordinates = player.getCoordinates();
        return getCell(coordinates);
    }

    private Cell getCell(Coordinates coordinates) {
        return cells[coordinates.getY()][coordinates.getX()];
    }

    public Dimension getMazeSize() {
        return new Dimension(nbCols, nbRows);
    }
}
