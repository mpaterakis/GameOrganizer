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
public class Game {

    // Constructors
    public Game(String gameIconPath, String gamePath, String gameName, double frameScale) {
        this.frameScale = frameScale;
        setGameIconPath(gameIconPath);
        this.gamePath = gamePath;
        this.gameName = gameName;
        this.gameIconPath = gameIconPath;
    }

    public Game(String gamePath, String gameName) {
        this.gamePath = gamePath;
        this.gameName = gameName;
    }

    // Getters and Setters
    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGamePath() {
        return gamePath;
    }

    public void setGamePath(String gamePath) {
        this.gamePath = gamePath;
    }

    public ImageIcon getGameIcon() {
        return gameIcon;
    }

    public String getGameIconPath() {
        return gameIconPath;
    }

    public void setGameIconPath(String gameIconPath) {
        this.gameIconPath = gameIconPath;
        this.gameIcon = new ImageIcon(new ImageIcon(gameIconPath).getImage().getScaledInstance((int) (256 * frameScale), (int) (256 * frameScale), Image.SCALE_SMOOTH));
    }

    public double getFrameScale() {
        return frameScale;
    }

    public void setFrameScale(double frameScale) {
        this.frameScale = frameScale;
        setGameIconPath(gameIconPath);
    }

    // Fields
    private ImageIcon gameIcon;
    private String gamePath, gameIconPath, gameName;
    private double frameScale;
}
