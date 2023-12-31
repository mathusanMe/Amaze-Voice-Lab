package com.gytmy.maze.model.player;

import java.awt.Color;

import com.gytmy.maze.model.Direction;
import com.gytmy.utils.Coordinates;

public class PlayerImplementation implements Player {

    private Coordinates coordinates;
    private String name;
    private Color color;

    private int numberOfMovements;
    private int timePassedInSeconds;

    public PlayerImplementation(Coordinates coordinates) {
        this(coordinates.copy(),
                Player.UNNAMED_PLAYER,
                Player.UNINITIALIZED_COLOR);
    }

    public PlayerImplementation(int x, int y) {
        this(new Coordinates(x, y),
                Player.UNNAMED_PLAYER,
                Player.UNINITIALIZED_COLOR);
    }

    public PlayerImplementation() {
        this(Coordinates.UNINITIALIZED_COORDINATES,
                Player.UNNAMED_PLAYER,
                Player.UNINITIALIZED_COLOR);
    }

    public PlayerImplementation(String name, Color color) {
        this(Coordinates.UNINITIALIZED_COORDINATES, name, color);
    }

    public PlayerImplementation(Coordinates coordinates, String name, Color color) {
        this.coordinates = coordinates;
        this.name = name;
        this.color = color;

        this.numberOfMovements = 0;
        this.timePassedInSeconds = 0;

    }

    @Override
    public int getX() {
        return coordinates.getX();
    }

    @Override
    public int getY() {
        return coordinates.getY();
    }

    @Override
    public Coordinates getCoordinates() {
        return coordinates.copy();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setX(int x) {
        coordinates.setX(x);
    }

    @Override
    public void setY(int y) {
        coordinates.setY(y);
    }

    @Override
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates.copy();
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void move(Direction direction)
            throws IllegalArgumentException {
        switch (direction) {
            case LEFT:
            case RIGHT:

                int newHorizontalPosition = getX() + direction.getStep();
                setX(newHorizontalPosition);
                break;
            case UP:
            case DOWN:
                int newVerticalPosition = getY() + direction.getStep();
                setY(newVerticalPosition);
                break;
            default:
                throw new IllegalArgumentException("Direction " + direction + " is not supported");
        }
        ++numberOfMovements;
    }

    @Override
    public int getNumberOfMovements() {
        return numberOfMovements;
    }

    @Override
    public int getTimePassedInSeconds() {
        return timePassedInSeconds;
    }

    @Override
    public void setTimePassedInSeconds(int timePassedInSeconds) {
        this.timePassedInSeconds = timePassedInSeconds;
    }

    @Override
    public String toString() {
        return "PlayerImplementation [coordinates=" + coordinates + ", name=" + name + ", color=" + color + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Player)) {
            return false;
        }

        Player player = (Player) obj;
        return player.getName().equals(getName())
                && player.getColor().equals(getColor());
    }

    @Override
    public int hashCode() {
        return getName().hashCode() + getColor().hashCode();
    }
}
