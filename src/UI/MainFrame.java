/*
 * mpaterakis, 2018
 */
package UI;

import Plugins.*;
import DataManagement.ProcessXML;
import GameOrganizer.Game;
import com.ivan.xinput.XInputDevice;
import com.ivan.xinput.enums.XInputAxis;
import com.ivan.xinput.enums.XInputButton;
import com.ivan.xinput.exceptions.XInputNotLoadedException;
import com.ivan.xinput.listener.SimpleXInputDeviceListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.border.DropShadowBorder;

/**
 * Custom JFrame representing the main window.
 *
 * @author mpaterakis
 */
public class MainFrame extends JFrame {

    /**
     * Create a MainFrame object.
     */
    public MainFrame() {
        initComponents();
    }

    /**
     * Initialize the MainFrame's components.
     */
    private void initComponents() {

        // JButtons
        exitButton = new JButton("X");
        exitButton.setBorderPainted(false);
        exitButton.setFocusPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setPreferredSize(new Dimension(15, 16));
        exitButton.setBorder(null);
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/Files/Xolonium-Special.ttf")).deriveFont(12f);
        } catch (FontFormatException ex) {
            JOptionPane.showMessageDialog(null, "FontFormat Error: Cannot load custom font", "Font Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "IO Error: Cannot read custom font file", "Font Error", JOptionPane.ERROR_MESSAGE);
        }
        exitButton.setFont(customFont);
        exitButton.setForeground(buttonColor);
        exitButton.addActionListener(e -> doExit());

        programSettingsButton = new JButton("\u2699");
        programSettingsButton.setBorderPainted(false);
        programSettingsButton.setFocusPainted(false);
        programSettingsButton.setContentAreaFilled(false);
        programSettingsButton.setPreferredSize(new Dimension(15, 16));
        programSettingsButton.setBorder(null);
        programSettingsButton.setFont(customFont.deriveFont(15f));
        programSettingsButton.setForeground(buttonColor);
        programSettingsButton.addActionListener(e -> doOpenProgramSettings());
        
        steamButton = new JButton("\uE800");
        steamButton.setBorderPainted(false);
        steamButton.setFocusPainted(false);
        steamButton.setContentAreaFilled(false);
        steamButton.setPreferredSize(new Dimension(15, 16));
        steamButton.setBorder(null);
        steamButton.setFont(customFont.deriveFont(12f));
        steamButton.setForeground(buttonColor);
        steamButton.addActionListener(e -> doLaunchSteam());

        // JLabels
        emptyGridLabel = new JLabel("Drop a game exe here to add it!", SwingConstants.CENTER);
        emptyGridLabel.setFont(customFont.deriveFont(16f));
        titleLabel = new JLabel("  " + titleText);
        titleLabel.setFont(customFont.deriveFont(12f));

        // JPanels
        gameGridPanel = new JPanel(new GridLayout(3, 3));
        gameGridPanel.setBackground(Color.WHITE);
        FileDrop fileDrop = new FileDrop(gameGridPanel, (java.io.File[] files) -> {
            doDropFile(files);
        });
        buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.add(steamButton);
        buttonsPanel.add(programSettingsButton);
        buttonsPanel.add(exitButton);
        buttonsPanel.setBackground(barColor);
        statusBarPanel = new JPanel(new GridLayout(1, 2));
        statusBarPanel.setBackground(barColor);
        statusBarPanel.add(titleLabel);
        statusBarPanel.add(buttonsPanel);
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(gameGridPanel, BorderLayout.CENTER);
        mainPanel.add(statusBarPanel, BorderLayout.NORTH);

        // Add JXPanel
        shadowPanel = new JXPanel(new BorderLayout());
        shadowPanel.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));

        // Add JPanels
        shadowPanel.add(mainPanel);
        add(shadowPanel);

        // WindowListener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Save to xml and fade out if the program is closed without clicking the X button
                doExit();
            }
        });

        // KeyListener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Change the main window's appearance according to each keypress
                doKeyAction(e);
            }
        });

        // XInputDeviceListener
        try {
            controller = XInputDevice.getDeviceFor(0);
            controller.addListener(new SimpleXInputDeviceListener() {
                @Override
                public void buttonChanged(XInputButton button, boolean pressed) {
                    if (pressed) {
                        doControllerButtonAction(button);
                    }
                }
            });

            // Make a new thread for listening to controller keys, if controller is connected
            if (controller.poll()) {
                Thread listenToControllerThread = new Thread(() -> {
                    long timeSinceLastAnalogAction = System.currentTimeMillis();
                    while (controller.poll()) {
                        if (System.currentTimeMillis() - timeSinceLastAnalogAction > 200) {
                            if (controller.getComponents().getAxes().get(XInputAxis.LEFT_THUMBSTICK_X) > 0.9) {
                                doControllerButtonAction(XInputButton.DPAD_RIGHT);
                                timeSinceLastAnalogAction = System.currentTimeMillis();
                            } else if (controller.getComponents().getAxes().get(XInputAxis.LEFT_THUMBSTICK_X) < -0.8) {
                                doControllerButtonAction(XInputButton.DPAD_LEFT);
                                timeSinceLastAnalogAction = System.currentTimeMillis();
                            }
                            if (controller.getComponents().getAxes().get(XInputAxis.LEFT_THUMBSTICK_Y) > 0.8) {
                                doControllerButtonAction(XInputButton.DPAD_UP);
                                timeSinceLastAnalogAction = System.currentTimeMillis();
                            } else if (controller.getComponents().getAxes().get(XInputAxis.LEFT_THUMBSTICK_Y) < -0.8) {
                                doControllerButtonAction(XInputButton.DPAD_DOWN);
                                timeSinceLastAnalogAction = System.currentTimeMillis();
                            }

                        }

                        try {
                            Thread.sleep(5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                listenToControllerThread.start();
            }

        } catch (XInputNotLoadedException ex) {
            // Do nothing, controller functions will be disabled
        }

        // Set JFrame parameters
        setFocusable(true);
        setResizable(false);
        setTitle("Game Organizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        InputStream stream = getClass().getResourceAsStream("/Files/Icon.png");
        try {
            setIconImage(new ImageIcon(ImageIO.read(stream)).getImage());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "IconImage Error: Cannot load statusbar icon", "Icon Error", JOptionPane.ERROR_MESSAGE);
        }

        // Load data from XML
        ProcessXML.LoadXML(this);

        // If screen is too small, adjust the frame scale
        int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        if ((height < 870 || width < 890) && getFrameScale() > Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 870) {
            setFrameScale(Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 870);
        }

        setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        paintShadow();
        setOpacity(0);
        setVisible(true);
        fadeInJFrame();

        // Add ComponentMover on status bar
        new ComponentMover(this, statusBarPanel);

    }

    /**
     * Check if MainFrame uses spacing.
     * 
     * @return Boolean containing value representative of the check
     */
    public boolean hasSpace() {
        return hasSpace;
    }

    /**
     * Get MainFrame's GameLabels ArrayList.
     * 
     * @return GameLabels ArrayList of this MainFrame
     */
    public ArrayList<GameLabel> getGameLabels() {
        return gameLabels;
    }

    /**
     * Check if MainFrame has a border.
     * 
     * @return Boolean containing value representative of the check
     */
    public boolean hasBorder() {
        return hasBorder;
    }

    /**
     * Get the buttons' color.
     * 
     * @return Color object containing the buttons' color
     */
    public Color getButtonColor() {
        return buttonColor;
    }

    /**
     * Get the bar's color.
     * 
     * @return Color object containing the bar's color
     */
    public Color getBarColor() {
        return barColor;
    }

    /**
     * Get the border's color.
     * 
     * @return Color object containing the border's color
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * Get the background's color.
     * 
     * @return Color object containing the background's color
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Check if MainFrame uses auto-exit after a game launch.
     * 
     * @return Boolean containing value representative of the check
     */
    public boolean getAutoExit() {
        return autoExit;
    }

    /**
     * Check if MainFrame has a shadow.
     * 
     * @return Boolean containing value representative of the check
     */
    public boolean hasShadow() {
        return hasShadow;
    }

    /**
     * Get the MainFrame's title text.
     * 
     * @return String containing the title's text
     */
    public String getTitleText() {
        return titleText;
    }

    /**
     * Get the shadow's color.
     * 
     * @return Color object containing the shadow's color
     */
    public Color getShadowColor() {
        return shadowColor;
    }

    /**
     * Check if MainFrame has fully booted.
     * 
     * @return Boolean containing value representative of the check
     */
    public boolean isFullyBooted() {
        return fullyBooted;
    }

    /**
     * Check if MainFrame uses focusing.
     * 
     * @return Boolean containing value representative of the check
     */
    public boolean hasFocusing() {
        return focusing;
    }

    /**
     * Set the auto-exit value (after a game is launched).
     * 
     * @param autoExit Boolean containing the new auto-exit value.
     */
    public void setAutoExit(boolean autoExit) {
        this.autoExit = autoExit;
    }

    /**
     * Set the button color.
     * 
     * @param buttonColor Color object containing the new button color
     */
    public void setButtonColor(Color buttonColor) {
        this.buttonColor = buttonColor;
        exitButton.setForeground(buttonColor);
        programSettingsButton.setForeground(buttonColor);
        steamButton.setForeground(buttonColor);
        titleLabel.setForeground(buttonColor);
    }

    /**
     * Set the bar color.
     * 
     * @param barColor Color object containing the new bar color
     */
    public void setBarColor(Color barColor) {
        this.barColor = barColor;
        statusBarPanel.setBackground(barColor);
        buttonsPanel.setBackground(barColor);
    }

    /**
     * Set the added Game's name.
     * 
     * @param addedGameName String containing the added Game's name
     */
    public void setAddedGameName(String addedGameName) {
        this.gameName = addedGameName;
    }

    /**
     * Set the hasBorder value.
     * 
     * @param hasBorder Boolean containing the new hasBorder value
     */
    public void setHasBorder(boolean hasBorder) {
        this.hasBorder = hasBorder;
    }

    /**
     * Set the focusing value.
     * 
     * @param focusing Boolean containing the new focusing value
     */
    public void setFocusing(boolean focusing) {
        this.focusing = focusing;
    }

    /**
     * Set the border color.
     * 
     * @param borderColor Color object containing the new border color
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * Set the hasSpace value.
     * 
     * @param hasSpace Boolean containing the new hasSpace value
     */
    public void setHasSpace(boolean hasSpace) {
        this.hasSpace = hasSpace;
    }

    /**
     * Set the MainFrame's title text.
     * 
     * @param titleText String object containing the MainFrame's title
     */
    public void setTitleText(String titleText) {
        this.titleText = titleText;
        titleLabel.setText("  " + titleText);
    }

    /**
     * Set the MainFrame's GameLabels.
     * 
     * @param gameLabels GameLabels ArrayList to be set
     */
    public void setGameLabels(ArrayList<GameLabel> gameLabels) {
        this.gameLabels = gameLabels;
    }

    /**
     * Set the hasShadow value.
     * 
     * @param hasShadow Boolean containing the new hasShadow value
     */
    public void setHasShadow(boolean hasShadow) {
        this.hasShadow = hasShadow;
    }

    /**
     * Set the shadow color.
     * 
     * @param shadowColor Color object containing the new shadow color
     */
    public void setShadowColor(Color shadowColor) {
        this.shadowColor = shadowColor;
        paintShadow();
    }

    /**
     * Get the MainFrame's frame scale.
     * 
     * @return Double object containing the frame scale value.
     */
    public double getFrameScale() {
        return frameScale;
    }

    /**
     * Set the MainFrame's frame scale.
     * 
     * @param frameScale Double object containing the frame scale value.
     */
    public void setFrameScale(double frameScale) {
        this.frameScale = frameScale;
        setBorderAndSize(hasBorder, borderColor);
        for (int i = 0; i < gameLabels.size(); i++) {
            gameLabels.get(i).getGame().setFrameScale(frameScale);
            gameLabels.get(i).setIcon(gameLabels.get(i).getGame().getGameIcon());
        }
    }

    /**
     * Get the currently focused GameLabel.
     * 
     * @return GameLabel that is focused
     */
    public JLabel getFocusedGameLabel() {
        return focusedGameLabel;
    }

    /**
     * Set the currently focused GameLabel.
     * 
     * @param focusedGameLabel GameLabel to be focused
     */
    public void setFocusedGameLabel(GameLabel focusedGameLabel) {
        this.focusedGameLabel = focusedGameLabel;
    }

    public boolean isUsingSteam() {
        return useSteam;
    }

    public void setUseSteam(boolean useSteam) {
        this.useSteam = useSteam;
        steamButton.setVisible(useSteam);
    }

    /**
     * Redraw the GridLayout with filled blank tiles.
     * 
     * @param gameLabels GameLabel ArrayList to be used for the redrawing
     */
    public void redrawGameGridPanel(ArrayList<GameLabel> gameLabels) {
        this.gameLabels = gameLabels;
        numberOfGames = gameLabels.size();
        if (numberOfGames == 0) {
            gameGridPanel.removeAll();
            gameGridPanel.add(emptyGridLabel);
        } else {
            // Emptying gameGridPanel to avoid adding more tiles than necessary
            gameGridPanel.removeAll();
            for (int i = 0; i < gameLabels.size(); i++) {
                gameGridPanel.add(gameLabels.get(i));
            }
            for (int i = gameLabels.size(); i < 9; i++) {
                gameGridPanel.add(new JLabel());
            }
        }
        gameGridPanel.revalidate();
        gameGridPanel.repaint();
    }

    /**
     * Set the border and window size.
     * 
     * @param hasBorder Boolean object (true if MainFrame has border, false otherwise)
     * @param borderColor Color object containing the new border Color
     */
    public void setBorderAndSize(boolean hasBorder, Color borderColor) {
        this.hasBorder = hasBorder;
        this.borderColor = borderColor;
        if (hasSpace) {
            gameGridPanel.setBorder(BorderFactory.createMatteBorder(8, 8, 1, 1, backgroundColor));
            if (hasBorder) {
                mainPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor));
                setSize((int) (810 * frameScale), (int) (860 * frameScale));
            } else {
                mainPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, borderColor));
                setSize((int) (810 * frameScale), (int) (860 * frameScale));
            }
            shadowPanel.remove(mainPanel);
            shadowPanel.add(mainPanel);
        } else {
            gameGridPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.WHITE));
            if (hasBorder) {
                mainPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor));
                setSize((int) (777 * frameScale), (int) (797 * frameScale));
            } else {
                mainPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, borderColor));
                setSize((int) (778 * frameScale), (int) (798 * frameScale));
            }
        }
    }

    /**
     * Set the background color.
     * 
     * @param backgroundColor Color object containing the new background color
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        gameGridPanel.setBackground(backgroundColor);
        setBorderAndSize(hasBorder, borderColor);
    }

    /**
     * Set the MainFrame's shadow
     */
    public void paintShadow() {
        DropShadowBorder shadow = new DropShadowBorder();
        shadow.setShowLeftShadow(true);
        shadow.setShowRightShadow(true);
        shadow.setShowBottomShadow(true);
        shadow.setShowTopShadow(true);
        shadow.setShadowColor(shadowColor);
        shadow.setShadowOpacity(0.0f);
        repaint();
        if (hasShadow) {
            shadow.setShadowOpacity(0.3f);
        }
        shadowPanel.setBorder(shadow);
    }

    /**
     * Switches the hasSpace Boolean.
     */
    public void switchHasSpace() {
        hasSpace = !hasSpace;
        setBorderAndSize(hasBorder, borderColor);
    }

    /**
     * Start fade in animation.
     */
    public void fadeInJFrame() {
        if (!fullyBooted) {
            for (float i = 0; i < 1; i += 0.03) {
                setOpacity(i);
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            setOpacity(1);
            fullyBooted = true;
        }
    }

    /**
     * Start fade out animation.
     */
    public void fadeOutJFrame() {
        if (fullyBooted) {
            fullyBooted = false;
            for (float i = 1; i > 0; i -= 0.03) {
                setOpacity(i);
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Close the program.
     */
    public void doExit() {
        // Save to XML on exit
        ProcessXML.WriteXML(this);

        // Fade Out animation and close program
        fadeOutJFrame();
        dispose();
        System.exit(0);
    }

    /**
     * Open the program's Settings JDialog.
     */
    private void doOpenProgramSettings() {
        new SettingsDialog(this);
        requestFocus();
    }
    
    /**
     * Launch Steam
     */
    private void doLaunchSteam() {
        try {
            Desktop.getDesktop().browse(new URI("steam://open/games"));
        } catch (URISyntaxException ex) {
            JOptionPane.showMessageDialog(null, "Steam Error: Steam is not installed", "Steam Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Steam Error: Steam is not installed", "Steam Error", JOptionPane.ERROR_MESSAGE);
        }
        doExit();
    }

    /**
     * Select all of the game's properties after it is dropped.
     * 
     * @param files File object containing the Game file that was dropped
     */
    private void doDropFile(java.io.File[] files) {
        // If there are 9 games, then don't a new one
        if (numberOfGames < 9) {

            // Select Image
            String iconFile = SpareDialogs.createGameIconPicker();

            // Select Game Name                    
            SpareDialogs.createGameNameDialog(this, files[0].getAbsoluteFile().getName());

            numberOfGames = gameLabels.size() + 1;

            // Create new GameLabel object
            GameLabel gameLabel = new GameLabel(new Game(iconFile, files[0].getAbsoluteFile().getAbsolutePath(), gameName, frameScale), this);
            gameLabels.add(gameLabel);

            // Add gameLabel to gameGridPanel
            gameGridPanel.add(gameLabel);

            // Redraw the gameGridPanel
            redrawGameGridPanel(gameLabels);
        }
    }

    /**
     * Changes the main window's appearance according to each key press.
     * 
     * @param e KeyEvent that was triggered
     */
    private void doKeyAction(KeyEvent e) {

        // If F5 is pressed, center the main window
        if (e.getKeyCode() == KeyEvent.VK_F5) {
            centerWindow();
        } // If + is pressed, increase the window's scale
        else if (e.getKeyCode() == KeyEvent.VK_ADD) {
            increaseScale();
        } // If - is pressed, decrease the window's scale
        else if (e.getKeyCode() == KeyEvent.VK_SUBTRACT) {
            decreaseScale();
        } // If Enter is pressed, launch the focused game
        else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (focusedGameLabel != null) {
                focusedGameLabel.launchGame();
            }
        } // If an arrow key is pressed, navigate through the tiles
        else if (e.getKeyCode() == KeyEvent.VK_UP) {
            changeFocusedGamelabel(-3);
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            changeFocusedGamelabel(3);
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            changeFocusedGamelabel(-1);
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            changeFocusedGamelabel(1);
        }
    }

    /**
     * (Re)Center the MainFrame.
     */
    public void centerWindow() {
        fadeOutJFrame();
        setLocationRelativeTo(null);
        fadeInJFrame();
    }

    /**
     * Decrease the MainFrame's scale.
     */
    public void decreaseScale() {
        if (frameScale > 0.5) {
            fadeOutJFrame();
            frameScale -= 0.1;
            setFrameScale(frameScale);
            fadeInJFrame();
        }
    }

    /**
     * Increase the MainFrame's scale.
     */
    public void increaseScale() {
        if (frameScale < 1.5) {
            fadeOutJFrame();
            frameScale += 0.1;
            setFrameScale(frameScale);
            fadeInJFrame();
        }
    }

    /**
     * Navigate the menu according to each controller button press.
     * 
     * @param button XInputButton object containing the button that was pressed
     */
    private void doControllerButtonAction(XInputButton button) {
        switch (button.toString()) {

            // If A is pressed, launch the focused game
            case "A": {
                if (focusedGameLabel != null) {
                    focusedGameLabel.launchGame();
                }
                break;
            }

            // If B is pressed, exit the program
            case "B": {
                doExit();
                break;
            }

            // If Y is pressed, center the main window
            case "Y": {
                centerWindow();
                break;
            }

            // If LT is pressed, decrease the window's scale
            //case "" XInputAxis.LEFT_THUMBSTICK_X
            case "LEFT_SHOULDER": {
                decreaseScale();
                break;
            }

            // If RT is pressed, increase the window's scale
            case "RIGHT_SHOULDER": {
                increaseScale();
                break;
            }

            // If a DPAD button is pressed, navigate the gameLabels
            case "DPAD_LEFT": {
                changeFocusedGamelabel(-1);
                break;
            }
            case "DPAD_RIGHT": {
                changeFocusedGamelabel(+1);
                break;
            }
            case "DPAD_UP": {
                changeFocusedGamelabel(-3);
                break;
            }
            case "DPAD_DOWN": {
                changeFocusedGamelabel(+3);
                break;
            }
            default:
                break;
        }
    }

    /**
     * Change the focused GameLabel.
     * 
     * @param indexDelta Integer containing the delta (difference) of the new 
     * focused GameLabel compared to the previous one.
     */
    private void changeFocusedGamelabel(int indexDelta) {
        if (getFocusedGameLabel() != null) {
            for (int i = 0; i < gameLabels.size(); i++) {
                if (focusedGameLabel == gameLabels.get(i) && i + indexDelta >= 0 && i + indexDelta < gameLabels.size()) {
                    gameLabels.get(i + indexDelta).focusOnGameLabel();
                    break;
                }
            }
        } else {
            if (!gameLabels.isEmpty()) {
                switch (indexDelta) {
                    case 1: {
                        if (gameLabels.size() >= 2) {
                            gameLabels.get(2).focusOnGameLabel();
                        } else {
                            gameLabels.get(0).focusOnGameLabel();
                        }
                        break;
                    }
                    case 3: {
                        if (gameLabels.size() >= 1) {
                            gameLabels.get(1).focusOnGameLabel();
                        } else {
                            gameLabels.get(0).focusOnGameLabel();
                        }
                        break;
                    }
                    case -3: {
                        if (gameLabels.size() >= 7) {
                            gameLabels.get(7).focusOnGameLabel();
                        } else {
                            gameLabels.get(0).focusOnGameLabel();
                        }
                        break;
                    }
                    case -1: {
                        gameLabels.get(0).focusOnGameLabel();
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    }

    // Fields
    private JXPanel shadowPanel;
    private JPanel gameGridPanel, statusBarPanel, buttonsPanel, mainPanel;
    private JButton exitButton, programSettingsButton, steamButton;
    private JLabel emptyGridLabel, titleLabel;
    private boolean hasBorder = true, hasSpace = false, autoExit = false, hasShadow = true, fullyBooted = false, focusing = true, useSteam=true;
    private Color buttonColor = Color.BLACK, barColor = new Color(204, 204, 204), borderColor = Color.GRAY, backgroundColor = Color.WHITE, shadowColor = Color.BLACK;
    private ArrayList<GameLabel> gameLabels = new ArrayList<>();
    private int numberOfGames = 0;
    private double frameScale = 1.0;
    private String gameName, titleText = "Game Organizer", steamLocation = "C:\\Program Files (x86)\\Steam\\Steam.exe";
    private Font customFont;
    private XInputDevice controller;
    private GameLabel focusedGameLabel = null;
}
