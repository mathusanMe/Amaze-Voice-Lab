package com.gytmy.maze.view;

import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.gytmy.utils.Bounds;

/**
 * A JPanelWithImages is a JPanel that can display images.
 * To do so, it needs the path of the images files, the coordinates of
 * the top left corner of the image and the width and height of the image.
 * 
 * When JPanel is displayed, the `paintComponent` method is called.
 * This method is overridden to display the images at given coordinates.
 */
public class JPanelWithImages extends JPanel {

    private List<Image> images;
    private Bounds[] bounds;

    public JPanelWithImages(String[] filesName, Bounds[] bounds)
            throws IllegalArgumentException {

        if (filesName.length != bounds.length) {
            throw new IllegalArgumentException("The number of files and the number of coordinates must be the same");
        }

        images = new ArrayList<>();
        this.bounds = bounds;

        for (String fileName : filesName) {
            images.add(createImage(fileName));
        }
    }

    private Image createImage(String fileName) {
        if (fileName.endsWith(".gif")) {
            return createGIF(fileName);
        }
        return createOther(fileName);
    }

    private Image createGIF(String fileName) {
        return Toolkit.getDefaultToolkit().createImage(fileName);
    }

    private Image createOther(String fileName) {
        try {
            return ImageIO.read(new File(fileName));
        } catch (IOException ioe) {
            System.out.println("Error while loading the image " + fileName);
            return null;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < images.size(); i++) {
            if (images.get(i) != null) {
                g.drawImage(images.get(i),
                        bounds[i].getX(), bounds[i].getY(),
                        bounds[i].getWidth(), bounds[i].getHeight(),
                        this);
            }
        }
    }
}