package com.gytmy.maze.view.game.end_transition;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import com.gytmy.maze.view.MenuFrameHandler;

public class GlassPaneWrapper extends JLayeredPane implements AnimationObserver {

    private DrawShapes glassPanel;
    private JComponent openingComponent;
    private JComponent closingComponent;
    private Runnable endFunction;
    private JFrame frame;
    private static final Dimension DEFAULT_DIMENSION = MenuFrameHandler.DEFAULT_DIMENSION;

    private boolean didEndAnimationRan = false;

    public GlassPaneWrapper(JComponent startComponent, JComponent endComponent, Runnable endFunction) {

        this.openingComponent = startComponent;
        this.closingComponent = endComponent;
        this.endFunction = endFunction;
        this.frame = MenuFrameHandler.getMainFrame();

        glassPanel = new DrawShapes(startComponent.getPreferredSize());
        glassPanel.addAnimationObserver(this);

        glassPanel.setOpaque(false);
        glassPanel.setVisible(true);
        glassPanel.setFocusable(true);

        openingComponent.setSize(openingComponent.getPreferredSize());
        setPreferredSize(openingComponent.getPreferredSize());
        setVisible(true);

        add(openingComponent, JLayeredPane.DEFAULT_LAYER);
        add(glassPanel, JLayeredPane.PALETTE_LAYER);

        glassPanel.setPreferredSize(frame.getPreferredSize());
        glassPanel.setSize(glassPanel.getPreferredSize());

    }

    @Override
    public void endAnimationUpdate() {

        if (didEndAnimationRan) {
            endFunction.run();
            return;
        }

        didEndAnimationRan = true;

        remove(openingComponent);
        closingComponent.setSize(DEFAULT_DIMENSION);
        closingComponent.setVisible(true);
        add(closingComponent, JLayeredPane.DEFAULT_LAYER);

        glassPanel.setSize(DEFAULT_DIMENSION);
        glassPanel.setPreferredSize(DEFAULT_DIMENSION);

        frame.setSize(DEFAULT_DIMENSION);
        frame.setPreferredSize(DEFAULT_DIMENSION);

        glassPanel.clearAnimation();
    }
}
