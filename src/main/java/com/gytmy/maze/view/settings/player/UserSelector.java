package com.gytmy.maze.view.settings.player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComboBox;

import com.gytmy.maze.view.game.Cell;
import com.gytmy.sound.AudioFileManager;
import com.gytmy.sound.User;

/**
 * This class is used to select a user. It is part of a Observer pattern. It is
 * the Observer.
 */
public class UserSelector extends JComboBox<User> {

    private static final Color FOREGROUND_COLOR = Cell.WALL_COLOR;
    private static final Color BACKGROUND_COLOR = Cell.PATH_COLOR;

    public UserSelector() {
        AvailableUsers.getInstance().addObserver(this);

        setForeground(FOREGROUND_COLOR);
        setBackground(BACKGROUND_COLOR);
        updateUserList();
    }

    private void updateUserList() {
        List<User> availableUsers = AvailableUsers.getInstance().getUsers();
        availableUsers.forEach(user -> addItem(new UserNameUser(user)));
    }

    public void update() {
        if (!isEnabled()) {
            return;
        }

        User lastUser = (User) getSelectedItem();

        removeAllItems();
        updateUserList();

        if (AvailableUsers.getInstance().getUsers().contains(lastUser)) {
            setSelectedItem(lastUser);
        }

        revalidate();
        repaint();
    }

    public void lockChoice() {
        setEnabled(false);
        AvailableUsers.getInstance().removeUser((User) getSelectedItem());
    }

    public void unlockChoice() {
        setEnabled(true);
        AvailableUsers.getInstance().addUser((User) getSelectedItem());
    }

    /**
     * This method is used to clean the data of the class. It is used when the
     * class is no longer used. It is used to remove the observer from the
     * AvailableUsers class.
     */
    public void cleanData() {
        AvailableUsers.getInstance().removeObserver(this);
    }

    /**
     * This class exists only to override the toString method of the User class.
     * This will allow the JComboBox to display the user name instead of the default
     * toString method.
     */
    private static class UserNameUser extends User {

        public UserNameUser(User user) {
            super(user);
        }

        @Override
        public String toString() {
            return getUserName();
        }
    }

    /**
     * This class is used to store the available users in the system. It uses the
     * Singleton pattern and is part of an Observer pattern. It is the subject.
     */
    protected static class AvailableUsers {

        private Set<User> users;
        private List<UserSelector> observers = new ArrayList<>();
        private static AvailableUsers instance = null;

        private AvailableUsers() {
            users = new HashSet<>(AudioFileManager.getUsers());
        }

        public static AvailableUsers getInstance() {
            if (instance == null) {
                instance = new AvailableUsers();
            }
            return instance;
        }

        public List<User> getUsers() {
            return new ArrayList<>(users);
        }

        public void removeUser(User user) {
            users.remove(user);
            notifyObservers();
        }

        public void addUser(User user) {
            users.add(user);
            notifyObservers();
        }

        public void notifyObservers() {
            observers.forEach(UserSelector::update);
        }

        public void addObserver(UserSelector observer) {
            observers.add(observer);
        }

        public void removeObserver(UserSelector observer) {
            observers.remove(observer);
        }

        public void updateUsers() {
            users = new HashSet<>(AudioFileManager.getUsers());
            notifyObservers();
        }

    }
}
