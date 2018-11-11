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
import org.jdesktop.swingx.util.OS;

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
        bgImage = new AlphaImageIcon(game.getGameIcon(), 1.0f);
        setIcon(bgImage);

        // Add MouseListener()
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Launch the game
                manageClick(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // Focus on this game
                focusOnGameLabel();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Regain focus of all game
                resetGameLabelFocus(mainFrame);
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
    // Launch game
    public void launchGame() {
        try {
            // Launch game if left click is pressed
            if (OS.isWindows()) {
                // Run bat with 'explorer' command
                if (".bat".equals(game.getGamePath().substring(game.getGamePath().length() - 4))) {
                    Runtime.getRuntime().exec("explorer  \"" + game.getGamePath() + "\"");
                } else {
                    // Run everything else with 'start' command
                    Runtime.getRuntime().exec("cmd /c \"cd /d \"" + (new File(game.getGamePath()).getParentFile()).toString()
                            + "\" & start \"\" \"" + (new File(game.getGamePath()).getName()) + "\"\"");
                }
            } else {
                Desktop.getDesktop().open(new File(game.getGamePath()));
            }

            // If autoExit is set, close the program
            if (mainFrame.getAutoExit()) {
                mainFrame.doExit();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error opening file", "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Manage the click that was issued and make an action
    private void manageClick(MouseEvent e) {
        // Check if the click was not outside the image
        Point p = e.getPoint();
        int clickX = (int) p.getX();
        int clickY = (int) p.getY();
        if ((mainFrame.hasSpace() && clickX < 256 * mainFrame.getFrameScale() && clickY < 263 * mainFrame.getFrameScale() && clickY > 6) 
                || (!mainFrame.hasSpace() && clickX < 256  * mainFrame.getFrameScale() && clickY < 263  * mainFrame.getFrameScale())) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                launchGame();

            } else if (e.getButton() == MouseEvent.BUTTON3) {

                // Open game's settings if right click is pressed
                new GameSettingsDialog(game, mainFrame);
                bgImage = new AlphaImageIcon(game.getGameIcon(), 1.0f);
                setIcon(bgImage);
            }
        }

    }

    // Focus on this game by making the rest of the window transparent
    public void focusOnGameLabel() {
        if (mainFrame.hasFocusing()) {
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
                    mainFrame.getGameLabels().get(i).setIcon(new AlphaImageIcon(mainFrame.getGameLabels().get(i).getGame().getGameIcon(), 0.5f));
                }
            }
            setIcon(new AlphaImageIcon(getGame().getGameIcon(), 1.0f));
            mainFrame.setFocusedGameLabel(this);
        }
    }

    // Reset focus of all the GameLabels
    public static void resetGameLabelFocus(MainFrame mainFrame) {
        if (mainFrame.hasFocusing()) {
            for (int i = 0; i < mainFrame.getGameLabels().size(); i++) {
                mainFrame.getGameLabels().get(i).setIcon(new AlphaImageIcon(mainFrame.getGameLabels().get(i).getGame().getGameIcon(), 1.0f));
            }
            mainFrame.setFocusedGameLabel(null);
        }
    }

    // Fields
    private Game game;
    private MainFrame mainFrame;
    private AlphaImageIcon bgImage;
}
