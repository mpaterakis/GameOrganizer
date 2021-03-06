/*
 * mpaterakis, 2018
 */
package UI;

import Plugins.*;
import GameOrganizer.*;
import Sound.SoundTypes;
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
 * A GameLabel object displays the game icon, can launch the game and open its GameSettingsDialog.
 *
 * @author mpaterakis
 */
public class GameLabel extends JLabel {

    /**
     * Create a GameLabel object.
     *
     * @param game Game object for this GameLabel
     * @param mainFrame MainFrame containing this GameLabel
     */
    public GameLabel(Game game, MainFrame mainFrame) {
        this.game = game;
        this.mainFrame = mainFrame;
        initComponents();
    }

    /**
     * Initialize the GameLabel's components.
     */
    private void initComponents() {

        // Add background image to JLabel
        bgImage = new AlphaImageIcon(game.getGameIcon(), 1.0f);
        setIcon(bgImage);
        if (game.getGameIcon().getImageLoadStatus() == MediaTracker.ERRORED) {
            showImageError();
        }

        // Add MouseListener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Launch the game
                manageClick(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // Focus on this game
                if (!mainFrame.isIgnoreMouse()) {
                    focusOnGameLabel();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Reset focus of all games
                if (!mainFrame.isIgnoreMouse()) {
                    resetActiveGameLabelFocus(mainFrame);
                }
            }
        });

        // Add MouseMotionListener
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (mainFrame.isIgnoreMouse() && (mainFrame.getFrameMousePosition()[0] != e.getXOnScreen() || mainFrame.getFrameMousePosition()[1] != e.getYOnScreen())) {
                    // Focus on this game
                    mainFrame.setFrameMousePosition(e.getXOnScreen(), e.getYOnScreen());
                    resetActiveGameLabelFocus(mainFrame);
                    focusOnGameLabel();
                    mainFrame.enableCursor();
                }
            }
        });

        // Set JLabel properties
        setVisible(true);
    }

    /**
     * Get the GameLabel's Game object.
     *
     * @return Game
     */
    public Game getGame() {
        return game;
    }

    /**
     * Launch the GameLabel's game.
     */
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

    /**
     * Manage the click that was issued and make an action.
     *
     * @param e MouseEvent that was issued
     */
    private void manageClick(MouseEvent e) {
        // Check if the click was not outside the image
        Point p = e.getPoint();
        int clickX = (int) p.getX();
        int clickY = (int) p.getY();
        if ((mainFrame.hasSpace() && clickX < 256 * mainFrame.getFrameScale() && clickY < 263 * mainFrame.getFrameScale() && clickY > 6)
                || (!mainFrame.hasSpace() && clickX < 256 * mainFrame.getFrameScale() && clickY < 263 * mainFrame.getFrameScale())) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                launchGame();

            } else if (e.getButton() == MouseEvent.BUTTON3) {

                // Open game's settings if right click is pressed
                mainFrame.playSound(SoundTypes.HIGH_CLICK);
                new GameSettingsDialog(this, mainFrame);
                bgImage = new AlphaImageIcon(game.getGameIcon(), 1.0f);
                setIcon(bgImage);
            }
        }
    }

    /**
     * Focus on this game by making the rest of the window transparent.
     */
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
            for (int i = 0; i < mainFrame.getActiveGameLabels().size(); i++) {
                if (mainFrame.getActiveGameLabels().get(i) != this) {
                    mainFrame.getActiveGameLabels().get(i).setIcon(new AlphaImageIcon(mainFrame.getActiveGameLabels().get(i).getGame().getGameIcon(), 0.5f));
                }
            }
            setIcon(new AlphaImageIcon(getGame().getGameIcon(), 1.0f));
            mainFrame.setFocusedGameLabel(this);
            mainFrame.playSound(SoundTypes.BASE_CLICK);
        }
    }

    /**
     * Reset focus of all the GameLabels.
     *
     * @param mainFrame MainFrame object containing this GameLabel
     */
    public static void resetActiveGameLabelFocus(MainFrame mainFrame) {
        if (mainFrame.hasFocusing()) {
            for (int i = 0; i < mainFrame.getActiveGameLabels().size(); i++) {
                mainFrame.getActiveGameLabels().get(i).setIcon(new AlphaImageIcon(mainFrame.getActiveGameLabels().get(i).getGame().getGameIcon(), 1.0f));
            }
            mainFrame.setFocusedGameLabel(null);
        }
    }

    /**
     * Reset focus of all the GameLabels.
     *
     * @param mainFrame MainFrame object containing this GameLabel
     */
    public static void resetAllGameLabelFocus(MainFrame mainFrame) {
        for (int i = 0; i < mainFrame.getGameLabelLists().size(); i++) {
            for (int j = 0; j < mainFrame.getGameLabelLists().get(i).size(); j++) {
                mainFrame.getGameLabelLists().get(i).get(j).setIcon(new AlphaImageIcon(mainFrame.getGameLabelLists().get(i).get(j).getGame().getGameIcon(), 1.0f));
            }
        }
    }

    /**
     * Show "Image not found" error on GameLabel.
     */
    public void showImageError() {
        setText("Image not found");
        setFont(mainFrame.getCustomFont().deriveFont(20f));
        setHorizontalAlignment(CENTER);
        setForeground(new Color(16777215 - mainFrame.getBackgroundColor().getRGB()));
        setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(16777215 - mainFrame.getBackgroundColor().getRGB())));
    }

    // Fields
    private final Game game;
    private final MainFrame mainFrame;
    private AlphaImageIcon bgImage;
}
