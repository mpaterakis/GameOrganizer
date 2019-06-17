/*
 * mpaterakis, 2018
 */
package GameOrganizer;

import java.awt.Image;
import javax.swing.ImageIcon;

/**
 * Objects of this class each represent a game with all its necessary data
 *
 * @author mpaterakis
 */
public final class Game {

    /**
     * Create a Game object.
     * 
     * @param gameIconPath String containing the game's icon path
     * @param gamePath String containing the game's path
     * @param gameName String containing the game's name
     * @param frameScale Double containing the MainFrame's frame scale
     */
    public Game(String gameIconPath, String gamePath, String gameName, double frameScale) {
        this.frameScale = frameScale;
        this.gamePath = gamePath;
        this.gameName = gameName;
        setGameIconPath(gameIconPath);
    }

    /**
     * Create a Game object.
     * 
     * @param gamePath String containing the game's path
     * @param gameName String containing the game's name
     */
    public Game(String gamePath, String gameName) {
        this.gamePath = gamePath;
        this.gameName = gameName;
    }

    /**
     * Get the Game's name.
     * 
     * @return String containing the Game's name
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Set the Game's name.
     * 
     * @param gameName String containing the Game's name
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * Get the Game's path.
     * 
     * @return String containing the Game's path
     */
    public String getGamePath() {
        return gamePath;
    }

    /**
     * Set the Game's path.
     * 
     * @param gamePath String containing the Game's path
     */
    public void setGamePath(String gamePath) {
        this.gamePath = gamePath;
    }

    /**
     * Get the Game's icon.
     * 
     * @return ImageIcon containing the Game's icon
     */
    public ImageIcon getGameIcon() {
        return gameIcon;
    }

    /**
     * Get the Game's icon path.
     * 
     * @return String containing the Game's icon path
     */
    public String getGameIconPath() {
        return gameIconPath;
    }

    /**
     * Set the Game's icon path.
     * 
     * @param gameIconPath String containing the Game's icon path
     */
    public void setGameIconPath(String gameIconPath) {
        this.gameIconPath = gameIconPath;
        this.gameIcon = new ImageIcon(new ImageIcon(gameIconPath).getImage().getScaledInstance((int) (256 * frameScale), (int) (256 * frameScale), Image.SCALE_SMOOTH));
    }

    /**
     * Get the MainFrame's frame scale.
     * 
     * @return Double containing the MainFrame's frame scale
     */
    public double getFrameScale() {
        return frameScale;
    }

    /**
     * Set the MainFrame's frame scale.
     * 
     * @param frameScale Double containing the MainFrame's frame scale
     */
    public void setFrameScale(double frameScale) {
        this.frameScale = frameScale;
        setGameIconPath(gameIconPath);
    }

    // Fields
    private ImageIcon gameIcon;
    private String gamePath, gameIconPath, gameName;
    private double frameScale;
}
