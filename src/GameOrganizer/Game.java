/*
 * mpaterakis, 2018
 */
package GameOrganizer;

import java.awt.Image;
import javax.swing.ImageIcon;

/**
 * Objects of this class each represent a game with all its necessary data
 * @author mpaterakis
 */
public class Game {

    // Constructors
    public Game(ImageIcon gameIcon, String gamePath, String gameName) {
        this.gameIcon = new ImageIcon (gameIcon.getImage().getScaledInstance(256, 256, Image.SCALE_SMOOTH));
        this.gamePath = gamePath;
        this.gameName = gameName;
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
    public void setGameIcon(ImageIcon gameIcon) {
        this.gameIcon = new ImageIcon (gameIcon.getImage().getScaledInstance(256, 256, Image.SCALE_SMOOTH));
    }

    // Fields
    private ImageIcon gameIcon;
    private String gamePath;
    private String gameName;
}
