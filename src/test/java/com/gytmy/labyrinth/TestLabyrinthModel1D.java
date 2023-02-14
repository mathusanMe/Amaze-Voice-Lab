package com.gytmy.labyrinth;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.gytmy.TestingUtils;

public class TestLabyrinthModel1D {

    @Test
    void testConstructorInvalidLength() {
        String expectedErrorMessage = "Cannot initialize a labyrinth of size <= 1";

        TestingUtils.assertArgumentExceptionMessage(
                () -> new LabyrinthModel1D(0, null), expectedErrorMessage);

        TestingUtils.assertArgumentExceptionMessage(

                () -> new LabyrinthModel1D(1, null), expectedErrorMessage);

        TestingUtils.assertArgumentExceptionMessage(

                () -> new LabyrinthModel1D(-1, null), expectedErrorMessage);

    }

    @Test
    void testConstructorValidLength() {

        assertValidBoard(2);
        assertValidBoard(50);
        assertValidBoard(100);

    }

    /**
     * Asserts if the board's content is valid
     * according to the definition
     * 
     * @param length must be >= 1
     */
    private void assertValidBoard(int length) {

        LabyrinthModel1D labyrinth = new LabyrinthModel1D(length, null);

        // Do not forget the left and right borders
        boolean[][] array = new boolean[3][length + 1];

        for (int row = 0; row < array.length; ++row) {
            // The walkable path with the first cell being a wall
            if (row == 1) {
                Arrays.fill(array[row], true);
                array[row][0] = false;
            } else
                Arrays.fill(array[row], false); // Top and Bottom walls
        }

        assertArrayEquals(labyrinth.getBoard(), array);
    }

    @Test
    void testPlayerMovesFirstCell() {

        for (int lengthPath = 2; lengthPath < 102; ++lengthPath) {
            LabyrinthModel1D labyrinth = new LabyrinthModel1D(lengthPath, null);
            int firstCell = 1;

            assertPlayerWillNotMove(firstCell, Direction.UP, labyrinth);
            assertPlayerWillNotMove(firstCell, Direction.DOWN, labyrinth);
            assertPlayerWillNotMove(firstCell, Direction.LEFT, labyrinth);
            assertPlayerWillMoveCorrectly(firstCell, Direction.RIGHT, labyrinth);
        }
    }

    @Test
    void testPlayerMovesCellsInBetween() {

        for (int lengthPath = 2; lengthPath < 102; ++lengthPath) {
            LabyrinthModel1D labyrinth = new LabyrinthModel1D(lengthPath, null);

            for (int position = 2; position < lengthPath; ++position) {
                assertPlayerWillNotMove(position, Direction.UP, labyrinth);
                assertPlayerWillNotMove(position, Direction.DOWN, labyrinth);
                assertPlayerWillMoveCorrectly(position, Direction.LEFT, labyrinth);
                assertPlayerWillMoveCorrectly(position, Direction.RIGHT, labyrinth);
            }
        }
    }

    @Test
    void testPlayerMovesLastCell() {

        for (int lengthPath = 2; lengthPath < 102; ++lengthPath) {
            LabyrinthModel1D labyrinth = new LabyrinthModel1D(lengthPath, null);
            int lastCell = labyrinth.getExitCell().getX();

            assertPlayerWillNotMove(lastCell, Direction.UP, labyrinth);
            assertPlayerWillNotMove(lastCell, Direction.DOWN, labyrinth);
            assertPlayerWillMoveCorrectly(lastCell, Direction.LEFT, labyrinth);
            assertPlayerWillNotMove(lastCell, Direction.RIGHT, labyrinth);
        }
    }

    /**
     * @param initPosition
     * @param direction
     * @param labyrinth
     */
    private void assertPlayerWillNotMove(
            int initPosition, Direction direction,
            LabyrinthModel1D labyrinth) {

        PlayerImplementation player = new PlayerImplementation(initPosition, 1);
        labyrinth.movePlayer(player, direction);
        int endPosition = player.getX();
        assertEquals(initPosition, endPosition);
    }

    /**
     * Asserts that a player will move correctly in
     * the given direction in the given labyrinth
     * 
     * @param labyrinth
     * @param player
     */
    private void assertPlayerWillMoveCorrectly(int initPosition,
            Direction direction, LabyrinthModel1D labyrinth) {

        PlayerImplementation player = new PlayerImplementation(initPosition, 1);
        int endPosition = player.getX() + direction.getStep();
        labyrinth.movePlayer(player, direction);
        assertEquals(endPosition, player.getX());
    }

    @Test
    void testValidityOfMovesFirstCell() {

        for (int lengthPath = 2; lengthPath < 102; ++lengthPath) {
            LabyrinthModel1D labyrinth = new LabyrinthModel1D(lengthPath, null);

            int firstCell = 1;
            PlayerImplementation player = new PlayerImplementation(firstCell, 1);

            assertFalse(labyrinth.isMoveValid(player, Direction.UP));
            assertFalse(labyrinth.isMoveValid(player, Direction.DOWN));
            assertFalse(labyrinth.isMoveValid(player, Direction.LEFT));
            assertTrue(labyrinth.isMoveValid(player, Direction.RIGHT));
        }
    }

    @Test
    void testValidityOfMovesCellsInBetween() {

        for (int lengthPath = 2; lengthPath < 102; ++lengthPath) {
            LabyrinthModel1D labyrinth = new LabyrinthModel1D(lengthPath, null);

            for (int position = 2; position < lengthPath; ++position) {
                PlayerImplementation player = new PlayerImplementation(position, 1);

                assertFalse(labyrinth.isMoveValid(player, Direction.UP));
                assertFalse(labyrinth.isMoveValid(player, Direction.DOWN));
                assertTrue(labyrinth.isMoveValid(player, Direction.LEFT));
                assertTrue(labyrinth.isMoveValid(player, Direction.RIGHT));
            }
        }
    }

    @Test
    void testValidityOfMovesLastCell() {

        for (int lengthPath = 2; lengthPath < 102; ++lengthPath) {
            LabyrinthModel1D labyrinth = new LabyrinthModel1D(lengthPath, null);

            int lastCell = labyrinth.getExitCell().getX();
            PlayerImplementation player = new PlayerImplementation(lastCell, 1);

            assertFalse(labyrinth.isMoveValid(player, Direction.UP));
            assertFalse(labyrinth.isMoveValid(player, Direction.DOWN));
            assertTrue(labyrinth.isMoveValid(player, Direction.LEFT));
            assertFalse(labyrinth.isMoveValid(player, Direction.RIGHT));
        }
    }

    @Test
    void testIsPlayerAtExit() {

        assertIsPlayerAtExit(2);
        assertIsPlayerAtExit(50);
        assertIsPlayerAtExit(100);

    }

    /**
     * Asserts if the function {@link LabyrinthModel1D#isPlayerAtExit(Player)}
     * works well for a labyrinth of the given length according to
     * the definition
     * 
     * @param labyrinthLength must be >= 1
     */
    private void assertIsPlayerAtExit(int labyrinthLength) {
        LabyrinthModel1D labyrinth = new LabyrinthModel1D(labyrinthLength, null);

        for (int position = 0; position < labyrinthLength; ++position) {
            Player player = new PlayerImplementation(position, 1);

            assertEquals(position == labyrinth.getExitCell().getX(),
                    labyrinth.isPlayerAtExit(player));

        }
    }

}