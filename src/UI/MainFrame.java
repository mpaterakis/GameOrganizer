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
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.border.DropShadowBorder;

/**
 * This class represents the main window
 *
 * @author mpaterakis
 */
public class MainFrame extends JFrame {

    // Constructor
    public MainFrame() {
        initComponents();
    }

    // Initialize the object
    private void initComponents() {

        // JButtons
        exitButton = new JButton("X");
        exitButton.setBorderPainted(false);
        exitButton.setFocusPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setPreferredSize(new Dimension(15, 16));
        exitButton.setBorder(null);
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/Files/Xolonium-Regular.ttf")).deriveFont(12f);
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
                        System.out.println(button.toString());
                        doControllerButtonAction(button);
                    }
                }
            });

            // Make a new thread for listening to controller keys, if controller is connected
            if (controller.poll()) {
                Thread listenToControllerThread = new Thread(() -> {
                    long timeSinceLastAnalogAction = System.currentTimeMillis();
                    while (true) {
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
                        controller.poll();
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

    // Getters
    public boolean hasSpace() {
        return hasSpace;
    }

    public ArrayList<GameLabel> getGameLabels() {
        return gameLabels;
    }

    public boolean hasBorder() {
        return hasBorder;
    }

    public boolean isHasSpace() {
        return hasSpace;
    }

    public Color getButtonColor() {
        return buttonColor;
    }

    public Color getBarColor() {
        return barColor;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public boolean getAutoExit() {
        return autoExit;
    }

    public boolean hasShadow() {
        return hasShadow;
    }

    public String getTitleText() {
        return titleText;
    }

    public Color getShadowColor() {
        return shadowColor;
    }

    public boolean isFullyBooted() {
        return fullyBooted;
    }

    public boolean hasFocusing() {
        return focusing;
    }

    // Setters
    public void setAutoExit(boolean autoExit) {
        this.autoExit = autoExit;
    }

    public void setButtonColor(Color buttonColor) {
        this.buttonColor = buttonColor;
        exitButton.setForeground(buttonColor);
        programSettingsButton.setForeground(buttonColor);
        titleLabel.setForeground(buttonColor);
    }

    public void setBarColor(Color barColor) {
        this.barColor = barColor;
        statusBarPanel.setBackground(barColor);
        buttonsPanel.setBackground(barColor);
    }

    public void setAddedGameName(String addedGameName) {
        this.gameName = addedGameName;
    }

    public void setHasBorder(boolean hasBorder) {
        this.hasBorder = hasBorder;
    }

    public void setFocusing(boolean focusing) {
        this.focusing = focusing;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public void setHasSpace(boolean hasSpace) {
        this.hasSpace = hasSpace;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
        titleLabel.setText("  " + titleText);
    }

    public void setGameLabels(ArrayList<GameLabel> gameLabels) {
        this.gameLabels = gameLabels;
    }

    public void setHasShadow(boolean hasShadow) {
        this.hasShadow = hasShadow;
    }

    public void setShadowColor(Color shadowColor) {
        this.shadowColor = shadowColor;
        paintShadow();
    }

    public double getFrameScale() {
        return frameScale;
    }

    public void setFrameScale(double frameScale) {
        this.frameScale = frameScale;
        setBorderAndSize(hasBorder, borderColor);
        for (int i = 0; i < gameLabels.size(); i++) {
            gameLabels.get(i).getGame().setFrameScale(frameScale);
            gameLabels.get(i).setIcon(gameLabels.get(i).getGame().getGameIcon());
        }
    }

    public JLabel getFocusedGameLabel() {
        return focusedGameLabel;
    }

    public void setFocusedGameLabel(GameLabel focusedGameLabel) {
        this.focusedGameLabel = focusedGameLabel;
    }

    // Custom functions
    // Redraw the GridLayout with filled blank tiles
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

    // Set the border and window size
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

    // Change the background color
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        gameGridPanel.setBackground(backgroundColor);
        setBorderAndSize(hasBorder, borderColor);
    }

    // Set window's shadow
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

    // Switches the hasSpace var
    public void switchHasSpace() {
        hasSpace = !hasSpace;
        setBorderAndSize(hasBorder, borderColor);
    }

    // Fade in animation
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

    // Fade out animation
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

    // Close the program
    public void doExit() {
        // Save to XML on exit
        ArrayList<Game> gameList = new ArrayList<>();
        for (int i = 0; i < gameLabels.size(); i++) {
            gameList.add(gameLabels.get(i).getGame());
        }
        ProcessXML.WriteXML(this);

        // Fade Out animation and close program
        fadeOutJFrame();
        dispose();
        System.exit(0);
    }

    // Open the program's Settings JDialog
    private void doOpenProgramSettings() {
        new SettingsDialog(this);
        requestFocus();
    }

    // Select all of the game's properties after it is dropped
    private void doDropFile(java.io.File[] files) {
        // If there are 9 games, then don't a new one
        if (numberOfGames < 9) {

            // Select Image
            String iconFile = ExtraDialogs.createGameIconPicker();

            // Select Game Name                    
            ExtraDialogs.createGameNameDialog(this, files[0].getAbsoluteFile().getName());

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

    // Changes the main window's appearance according to each keypress
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

    // (Re)Center main window
    public void centerWindow() {
        fadeOutJFrame();
        setLocationRelativeTo(null);
        fadeInJFrame();
    }

    // Decrease the window's scale
    public void decreaseScale() {
        if (frameScale > 0.5) {
            fadeOutJFrame();
            frameScale -= 0.1;
            setFrameScale(frameScale);
            fadeInJFrame();
        }
    }

    // Increase the window's scale
    public void increaseScale() {
        if (frameScale < 1.5) {
            fadeOutJFrame();
            frameScale += 0.1;
            setFrameScale(frameScale);
            fadeInJFrame();
        }
    }

    // Navigate the menu according to each controller button press
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

    // Changes focused GameLabel
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
    private JButton exitButton, programSettingsButton;
    private JLabel emptyGridLabel, titleLabel;
    private boolean hasBorder = true, hasSpace = false, autoExit = false, hasShadow = true, fullyBooted = false, focusing = true;
    private Color buttonColor = Color.BLACK, barColor = new Color(204, 204, 204), borderColor = Color.GRAY, backgroundColor = Color.WHITE, shadowColor = Color.BLACK;
    private ArrayList<GameLabel> gameLabels = new ArrayList<>();
    private int numberOfGames = 0;
    private double frameScale = 1.0;
    private String gameName, titleText = "Game Organizer";
    private Font customFont;
    private XInputDevice controller;
    private GameLabel focusedGameLabel = null;
}
