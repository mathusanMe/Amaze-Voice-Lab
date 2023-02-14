package com.gytmy.labyrinth.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import com.gytmy.utils.Boolean2DArraysOperations;
import com.gytmy.utils.Vector2;

/**
 * class only handles correctly the case where the maze has an odd number of
 * rows and columns. If the maze has an even number of rows and columns, the
 * maze will be generated with up to 2 extra rows and columns.
 * 
 * @see <a href=
 *      "https://en.wikipedia.org/wiki/Maze_generation_algorithm#Randomized_depth-first_search">Wikipedia</a>
 */
public class DepthFirstGenerator implements BoardGenerator {
    Random rand = new Random();

    int width;
    int height;
    Vector2 start;

    Stack<Vector2> stack = new Stack<>();
    boolean[][] board;
    boolean[][] visited;
    Vector2 current;

    @Override
    public boolean[][] generate(int width, int height) {
        initArguments(width, height, null);
        return generate();
    }

    @Override
    public boolean[][] generate(int width, int height, Vector2 start) {
        initArguments(width, height, start);
        return generate();
    }

    private boolean[][] generate() {
        board = new boolean[height][width];
        generateBoard();
        return getBorderedBoard();
    }

    private void initArguments(int width, int height, Vector2 start) {
        handleInvalidArguments(width, height);
        // -1 because we want to have a border around the labyrinth
        this.width = width - 1;
        this.height = height - 1;
        if (start == null) {
            this.start = generateRandomStart();
        } else {
            handleInvalidStart(start);
            this.start = start;
        }
    }

    private void handleInvalidArguments(int width, int height) {
        if (width % 2 == 0) {
            throw new IllegalArgumentException("The width must be odd");
        }
        if (height % 2 == 0) {
            throw new IllegalArgumentException("The height must be odd");
        }
        if (width < 5) {
            throw new IllegalArgumentException("The width must be at least 5");
        }
        if (height < 5) {
            throw new IllegalArgumentException("The height must be at least 5");
        }
    }

    private void handleInvalidStart(Vector2 start) {
        if (start == null) {
            throw new IllegalArgumentException("The start cannot be null");
        }
        if (!isInsideBorders(start)) {
            throw new IllegalArgumentException("The start cell must be inside the labyrinth");
        }
    }

    private boolean isInsideBorders(Vector2 start) {
        return start.getX() >= 1 && start.getX() < width && start.getY() >= 1 && start.getY() < height;
    }

    private Vector2 generateRandomStart() {
        int row = rand.nextInt(height - 2) + 1; // 1 to height-1
        int col = rand.nextInt(width - 2) + 1; // 1 to width-1

        return new Vector2(col, row);
    }

    private boolean[][] generateBoard() {
        initializeVariables();

        while (!stack.isEmpty()) {
            current = stack.pop();
            Vector2 neighbor = getRandomNotVisitedNeighbor();
            if (neighbor == null) {
                // no neighbor found, go back to the previous cell
                continue;
            }
            updateVariables(neighbor);
        }

        return board;
    }

    /**
     * Initialize the variables to start the generation
     */
    private void initializeVariables() {
        board[start.getY()][start.getX()] = true;
        visited = new boolean[height][width];
        visited[start.getY()][start.getX()] = true;
        stack.push(start);
        current = start;
    }

    /**
     * 
     * @return a random neighbor of the current cell that has not been visited yet
     *         or null if there is no such neighbor
     */
    private Vector2 getRandomNotVisitedNeighbor() {
        Vector2[] neighbors = getPossibleNeighbors();
        List<Vector2> validNeighbors = getValidNeighbors(neighbors);

        if (validNeighbors.isEmpty()) {
            return null;
        } else {
            // Random choice between the valid neighbors
            return validNeighbors.get(rand.nextInt(validNeighbors.size()));
        }
    }

    /**
     * 
     * @return the 4 possible neighbors of the current cell
     */
    private Vector2[] getPossibleNeighbors() {
        Vector2[] neighbors = new Vector2[4];
        neighbors[0] = new Vector2(current.getX(), current.getY() - 2);
        neighbors[1] = new Vector2(current.getX() + 2, current.getY());
        neighbors[2] = new Vector2(current.getX(), current.getY() + 2);
        neighbors[3] = new Vector2(current.getX() - 2, current.getY());
        return neighbors;
    }

    /**
     * 
     * @param neighbors the 4 possible neighbors of the current cell
     * @return the neighbors that are not out of bounds and have not been visited
     *         yet
     */
    private List<Vector2> getValidNeighbors(Vector2[] neighbors) {
        List<Vector2> validNeighbors = new ArrayList<>();
        for (Vector2 v : neighbors) {
            if (isValideNeighbor(v)) {
                validNeighbors.add(v);
            }
        }
        return validNeighbors;
    }

    private boolean isValideNeighbor(Vector2 v) {
        return !isOutOfBounds(v) && !isVisited(v);
    }

    private boolean isOutOfBounds(Vector2 v) {
        return v.getX() < 0 || v.getX() >= width || v.getY() < 0 || v.getY() >= height;
    }

    private boolean isVisited(Vector2 v) {
        return board[v.getY()][v.getX()];
    }

    /**
     * Update the variables to prepare the next iteration of the loop
     * 
     * @param neighbor the neighbor that has been chosen
     */
    private void updateVariables(Vector2 neighbor) {
        stack.push(current.copy());
        board[neighbor.getY()][neighbor.getX()] = true;
        // remove wall between current and neighbor
        board[(neighbor.getY() + current.getY()) / 2][(neighbor.getX() + current.getX()) / 2] = true;
        visited[neighbor.getY()][neighbor.getX()] = true;
        stack.push(neighbor);
    }

    /**
     * @return a copy of the board with a border around it
     */
    private boolean[][] getBorderedBoard() {
        boolean[][] finalBoard = board;
        if (!Boolean2DArraysOperations.isRowEmpty(finalBoard, 0)) {
            finalBoard = addTopBorder(finalBoard);
        }
        if (!Boolean2DArraysOperations.isRowEmpty(finalBoard, finalBoard.length - 1)) {
            finalBoard = addBottomBorder(finalBoard);
        }
        if (!Boolean2DArraysOperations.isColumnEmpty(finalBoard, 0)) {
            finalBoard = addLeftBorder(finalBoard);
        }
        if (!Boolean2DArraysOperations.isColumnEmpty(finalBoard, finalBoard[0].length - 1)) {
            finalBoard = addRightBorder(finalBoard);
        }
        return finalBoard;
    }

    private boolean[][] addTopBorder(boolean[][] finalBoard) {
        return Boolean2DArraysOperations.addEmptyRow(finalBoard, 0);
    }

    private boolean[][] addBottomBorder(boolean[][] finalBoard) {
        return Boolean2DArraysOperations.addEmptyRow(finalBoard, finalBoard.length);
    }

    private boolean[][] addLeftBorder(boolean[][] finalBoard) {
        return Boolean2DArraysOperations.addEmptyColumn(finalBoard, 0);
    }

    private boolean[][] addRightBorder(boolean[][] finalBoard) {
        return Boolean2DArraysOperations.addEmptyColumn(finalBoard, finalBoard[0].length);
    }

}