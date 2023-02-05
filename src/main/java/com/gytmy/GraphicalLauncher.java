package com.gytmy;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class GraphicalLauncher implements Runnable {

    public static void main(String[] args) {
        EventQueue.invokeLater(new GraphicalLauncher());
    }

    @Override
    public void run() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        StartMenu menu = new StartMenu(frame);
        frame.add(menu);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        frame.setResizable(false);
    }

}
