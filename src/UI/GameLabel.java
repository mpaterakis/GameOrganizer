/*
 * mpaterakis, 2018
 */
package UI;

import GameOrganizer.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

/**
 * A GameLabel object displays the game icon, can launch the game and edit its properties
 *
 * @author mpaterakis
 */
public class GameLabel extends JLabel {

    // Constructor
    public GameLabel(Game game, MainFrame mainFrame) {
        this.game = game;
        this.mainFrame = mainFrame;
        initComponents();
    }

    // Initialize components
    private void initComponents() {

        // Add background image to JLabel
        bgImage = new ImageIcon(game.getGameIcon().getImage().getScaledInstance(256, 256, Image.SCALE_SMOOTH));
        setIcon(bgImage);

        // Add MouseListener()
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Launch the game
                launchGame(e);
            }
        });

        // Set JLabel properties
        setVisible(true);
    }

    // Setters
    public Game getGame() {
        return game;
    }

    // Custom methods
    // Launch game if click was on the image
    private void launchGame(MouseEvent e) {
        // Check if the click was not outside the image
        Point p = e.getPoint();
        int clickX = (int) p.getX();
        int clickY = (int) p.getY();
        if ((mainFrame.hasSpace() && clickX < 256 && clickY < 263 && clickY > 6) || (!mainFrame.hasSpace() && clickX < 256 && clickY < 263)) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                try {
                    // Launch game if left click is pressed
                    Desktop.getDesktop().open(new File(game.getGamePath()));
                    
                    // If autoExit is set, close the program
                    if (mainFrame.getAutoExit()) {
                        mainFrame.doExit();
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "This file has no parent directory", "No Parent Directory", JOptionPane.ERROR_MESSAGE);
                }

            } else if (e.getButton() == MouseEvent.BUTTON3) {

                // Open game's settings if right click is pressed
                new GameSettingsDialog(game, mainFrame);
                bgImage = new ImageIcon(game.getGameIcon().getImage().getScaledInstance(256, 256, Image.SCALE_SMOOTH));
                setIcon(bgImage);
            }
        }
        
    }

    // Fields
    private Game game;
    private MainFrame mainFrame;
    private ImageIcon bgImage;
}
