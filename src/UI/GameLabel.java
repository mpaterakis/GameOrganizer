/*
 * mpaterakis, 2018
 */
package UI;

import Plugins.*;
import GameOrganizer.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        bgImage = new AlphaImageIcon(new ImageIcon(game.getGameIcon().getImage().getScaledInstance(256, 256, Image.SCALE_SMOOTH)), 1.0f);
        setIcon(bgImage);

        // Add MouseListener()
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Launch the game
                launchGame(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // Focus on this game
                focusOnGameLabel();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Regain focus of all game
                resetGameLabelFocus();
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
                    JOptionPane.showMessageDialog(null, "Error opening file", "File Error", JOptionPane.ERROR_MESSAGE);
                }

            } else if (e.getButton() == MouseEvent.BUTTON3) {

                // Open game's settings if right click is pressed
                new GameSettingsDialog(game, mainFrame);
                bgImage = new AlphaImageIcon(new ImageIcon(game.getGameIcon().getImage().getScaledInstance(256, 256, Image.SCALE_SMOOTH)), 1.0f);
                setIcon(bgImage);
            }
        }

    }

    // Focus on this game by making the rest of the window transparent
    private void focusOnGameLabel() {
        if (mainFrame.usesFocusing()) {
            // Wait for the main window to fully load
            while (!mainFrame.isFullyBooted()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            for (int i = 0; i < mainFrame.getGameLabels().size(); i++) {
                if (mainFrame.getGameLabels().get(i) != this) {
                    mainFrame.getGameLabels().get(i).setIcon(new AlphaImageIcon(new ImageIcon(mainFrame.getGameLabels().get(i).getGame().getGameIcon().getImage().getScaledInstance(256, 256, Image.SCALE_SMOOTH)), 0.5f));
                }
            }
        }
    }

    // Reset focus of all the GameLabels
    private void resetGameLabelFocus() {
        if (mainFrame.usesFocusing()) {
            for (int i = 0; i < mainFrame.getGameLabels().size(); i++) {
                if (mainFrame.getGameLabels().get(i) != this) {
                    mainFrame.getGameLabels().get(i).setIcon(new AlphaImageIcon(new ImageIcon(mainFrame.getGameLabels().get(i).getGame().getGameIcon().getImage().getScaledInstance(256, 256, Image.SCALE_SMOOTH)), 1.0f));
                }
            }
        }
    }

    // Fields
    private Game game;
    private MainFrame mainFrame;
    private AlphaImageIcon bgImage;
}
