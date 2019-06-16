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
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

        addMenuButton = new JButton("+");
        addMenuButton.setBorderPainted(false);
        addMenuButton.setFocusPainted(false);
        addMenuButton.setContentAreaFilled(false);
        addMenuButton.setPreferredSize(new Dimension(15, 16));
        addMenuButton.setBorder(null);
        addMenuButton.setFont(customFont.deriveFont(12f));
        addMenuButton.setForeground(buttonColor);
        addMenuButton.addActionListener(e -> doAddNewGameMenu());

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
        buttonsPanel.add(addMenuButton);
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

        // MouseAdapter
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // Enable the cursor on main window when the mouse moves
                if (mouseX != e.getXOnScreen() || mouseY != e.getYOnScreen()) {
                    mouseX = e.getXOnScreen();
                    mouseY = e.getYOnScreen();
                    enableCursor();
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                // If scroll up
                if (notches < 0) {
                    goToPreviousGameMenu();
                } else {
                    goToNextGameMenu();
                }
            }
        };
        mouseX = MouseInfo.getPointerInfo().getLocation().x;
        mouseY = MouseInfo.getPointerInfo().getLocation().y;

        // MouseMotionListener
        addMouseMotionListener(mouseAdapter);
        mainPanel.addMouseWheelListener(mouseAdapter);
        statusBarPanel.addMouseMotionListener(mouseAdapter);
        statusBarPanel.addMouseWheelListener(mouseAdapter);

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
                        if (System.currentTimeMillis() - timeSinceLastAnalogAction > 5) {
                            if (controller.getComponents().getAxes().get(XInputAxis.RIGHT_THUMBSTICK_X) > 0.6) {
                                moveWindow(2, 0);
                                timeSinceLastAnalogAction = System.currentTimeMillis();
                                disableCursor();
                            } else if (controller.getComponents().getAxes().get(XInputAxis.RIGHT_THUMBSTICK_X) < -0.5) {
                                moveWindow(-2, 0);
                                timeSinceLastAnalogAction = System.currentTimeMillis();
                                disableCursor();
                            }
                            if (controller.getComponents().getAxes().get(XInputAxis.RIGHT_THUMBSTICK_Y) > 0.5) {
                                moveWindow(0, -2);
                                timeSinceLastAnalogAction = System.currentTimeMillis();
                                disableCursor();
                            } else if (controller.getComponents().getAxes().get(XInputAxis.RIGHT_THUMBSTICK_Y) < -0.5) {
                                moveWindow(0, 2);
                                timeSinceLastAnalogAction = System.currentTimeMillis();
                                disableCursor();
                            }
                        }
                        if (System.currentTimeMillis() - timeSinceLastAnalogAction > 5) {
                            if (controller.getComponents().getAxes().get(XInputAxis.RIGHT_TRIGGER) > 0.5) {
                                increaseScale();
                                timeSinceLastAnalogAction = System.currentTimeMillis();
                                disableCursor();
                            } else if (controller.getComponents().getAxes().get(XInputAxis.LEFT_TRIGGER) > 0.5) {
                                decreaseScale();
                                timeSinceLastAnalogAction = System.currentTimeMillis();
                                disableCursor();
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
                disableCursor();
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
     * Get MainFrame's active GameLabels ArrayList.
     *
     * @return GameLabels ArrayList of this MainFrame
     */
    public ArrayList<GameLabel> getActiveGameLabels() {
        return activeGameLabels;
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
        addMenuButton.setForeground(buttonColor);
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
     * Set the MainFrame's active GameLabels.
     *
     * @param activeGameLabels GameLabels ArrayList to be set
     */
    public void setActiveGameLabels(ArrayList<GameLabel> activeGameLabels) {
        this.activeGameLabels = activeGameLabels;
    }

    /**
     * get the MainFrame's GameLabels list.
     *
     * @return gameLabelLists MainFrame's GameLabels list
     */
    public ArrayList<ArrayList<GameLabel>> getGameLabelLists() {
        return gameLabelLists;
    }

    /**
     * Set the MainFrame's GameLabels list.
     *
     * @param gameLabelLists GameLabels ArrayList to be set
     */
    public void setGameLabelLists(ArrayList<ArrayList<GameLabel>> gameLabelLists) {
        this.gameLabelLists = gameLabelLists;
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
     * Get the maximum possible frame scale for the current screen.
     *
     * @return The maximum possible frame scale for the current screen
     */
    public double getMaxFrameScale() {
        double maxFrameScale = BigDecimal.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 860).setScale(1, RoundingMode.HALF_UP).doubleValue();
        return maxFrameScale;
    }

    /**
     * Set the MainFrame's frame scale.
     *
     * @param frameScale Double object containing the frame scale value.
     */
    public void setFrameScale(double frameScale) {
        // "Filter" possible noise caused by Double
        this.frameScale = BigDecimal.valueOf(frameScale).setScale(1, RoundingMode.HALF_UP).doubleValue();
        setBorderAndSize(hasBorder, borderColor);
        for (int i = 0; i < gameLabelLists.size(); i++) {
            for (int j = 0; j < gameLabelLists.get(i).size(); j++) {
                gameLabelLists.get(i).get(j).getGame().setFrameScale(frameScale);
                gameLabelLists.get(i).get(j).setIcon(gameLabelLists.get(i).get(j).getGame().getGameIcon());
            }
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

    /**
     * Check the status (enabled/disabled) of the Steam icon.
     *
     * @return The status (enabled/disabled) of the Steam icon.
     */
    public boolean isUsingSteam() {
        return useSteam;
    }

    /**
     * Set the new status (enabled/disabled) of the Steam icon.
     *
     * @param useSteam The new status of the Steam Icon
     */
    public void setUseSteam(boolean useSteam) {
        this.useSteam = useSteam;
        steamButton.setVisible(useSteam);
    }

    public boolean isShowingNewMenuButton() {
        return showingNewMenuButton;
    }

    public void setShowingNewMenuButton(boolean showingNewMenuButton) {
        this.showingNewMenuButton = showingNewMenuButton;
        if (showingNewMenuButton && menuIndex == gameLabelLists.size() - 1) {
            showAddNewGameButton();
        } else {
            hideAddNewGameButton();
        }
    }

    /**
     * Get the status of ignoreMouse.
     *
     * @return The status of ignoreMouse
     */
    public boolean isIgnoreMouse() {
        return ignoreMouse;
    }

    /**
     * Set the status of ignoreMouse.
     *
     * @param ignoreMouse The new status of ignoreMouse
     */
    public void setIgnoreMouse(boolean ignoreMouse) {
        this.ignoreMouse = ignoreMouse;
    }

    /**
     * Get the status of secretGamesShown
     *
     * @return The status of secretGamesShown
     */
    public boolean secretGamesShown() {
        return secretGamesShown;
    }

    /**
     * Get mouse position.
     *
     * @return Integer table with the X and Y position of the mouse
     */
    public int[] getFrameMousePosition() {
        int[] mousePosition = new int[2];
        mousePosition[0] = mouseX;
        mousePosition[1] = mouseY;
        return mousePosition;
    }

    /**
     * Set mouse position.
     *
     * @param mouseX X position of mouse
     * @param mouseY Y position of mouse
     */
    public void setFrameMousePosition(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    /**
     * Redraw the GridLayout with filled blank tiles.
     *
     * @param activeGameLabels GameLabel ArrayList to be used for the redrawing
     */
    public void redrawGameGridPanel(ArrayList<GameLabel> activeGameLabels) {
        if (activeGameLabels == null) {
            gameGridPanel.removeAll();
            gameGridPanel.add(emptyGridLabel);
            return;
        }
        this.activeGameLabels = activeGameLabels;
        numberOfGames = activeGameLabels.size();
        if (numberOfGames == 0) {
            gameGridPanel.removeAll();
            gameGridPanel.add(emptyGridLabel);
        } else {
            // Emptying gameGridPanel to avoid adding more tiles than necessary
            gameGridPanel.removeAll();
            for (int i = 0; i < activeGameLabels.size(); i++) {
                gameGridPanel.add(activeGameLabels.get(i));
            }
            for (int i = activeGameLabels.size(); i < 9; i++) {
                gameGridPanel.add(new JLabel());
            }
        }
        gameGridPanel.revalidate();
        gameGridPanel.repaint();
        updatePaging();
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
     * Set the MainFrame's shadow.
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
     * Launch Steam.
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
     * Add a new game menu.
     */
    private void doAddNewGameMenu() {
        gameLabelLists.add(new ArrayList<GameLabel>());
        goToNextGameMenu();
    }

    /**
     * Launch Steam in Big Picture mode.
     */
    private void doLaunchSteamBigPicture() {
        try {
            Desktop.getDesktop().browse(new URI("steam://open/bigpicture"));
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

            // Select Game Name
            String tempGameName = files[0].getAbsoluteFile().getName().split(".exe")[0].split(".bat")[0];
            String gameName = "";
            // Split on case change
            for (String w : tempGameName.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
                gameName += w + " ";
            }
            tempGameName = gameName.substring(0, gameName.length() - 1);
            gameName = "";
            // Split on number
            for (String w : tempGameName.split("(?<=\\D)(?=\\d)")) {
                gameName += w + " ";
            }
            SpareDialogs.createGameNameDialog(this, gameName.substring(0, gameName.length() - 1));

            numberOfGames = activeGameLabels.size() + 1;

            // Select Image
            String iconFile = SpareDialogs.createGameIconPicker(files[0].getParent(), this.gameName);

            // Create new GameLabel object
            GameLabel gameLabel = new GameLabel(new Game(iconFile, files[0].getAbsoluteFile().getAbsolutePath(), this.gameName, frameScale), this);
            activeGameLabels.add(gameLabel);
            if (gameLabelLists.isEmpty()) {
                gameLabelLists.add(activeGameLabels);
            }

            // Add gameLabel to gameGridPanel
            gameGridPanel.add(gameLabel);

            // Redraw the gameGridPanel
            redrawGameGridPanel(activeGameLabels);
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
        } else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            goToNextGameMenu();
        } else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            goToPreviousGameMenu();
        }
    }

    /**
     * Go to next Game menu.
     */
    public void goToNextGameMenu() {
        if (menuIndex < gameLabelLists.size() - 1) {
            menuIndex++;
            if (focusedGameLabels.size() < gameLabelLists.size()) {
                focusedGameLabels.add(focusedGameLabel);
                focusedGameLabel = null;
            } else {
                focusedGameLabels.set(menuIndex - 1, focusedGameLabel);
                focusedGameLabel = focusedGameLabels.get(menuIndex);
            }
            activeGameLabels = gameLabelLists.get(menuIndex);
            redrawGameGridPanel(activeGameLabels);
        }
    }

    /**
     * Go to previous Game menu.
     */
    public void goToPreviousGameMenu() {
        if (menuIndex > 0) {
            menuIndex--;
            if (focusedGameLabels.size() < gameLabelLists.size()) {
                focusedGameLabels.add(focusedGameLabel);
            } else {
                focusedGameLabels.set(menuIndex + 1, focusedGameLabel);
                focusedGameLabel = focusedGameLabels.get(menuIndex);
            }
            focusedGameLabel = focusedGameLabels.get(menuIndex);
            activeGameLabels = gameLabelLists.get(menuIndex);
            redrawGameGridPanel(activeGameLabels);
        }
    }

    /**
     * Go to specified Game menu.
     */
    public void goToGameMenu(int newMenuIndex) {
        this.menuIndex = newMenuIndex;
        if (focusedGameLabels.size() < gameLabelLists.size()) {
            focusedGameLabels.add(focusedGameLabel);
        } else {
            focusedGameLabels.set(menuIndex + 1, focusedGameLabel);
            focusedGameLabel = focusedGameLabels.get(menuIndex);
        }
        focusedGameLabel = focusedGameLabels.get(menuIndex);
        activeGameLabels = gameLabelLists.get(menuIndex);
        redrawGameGridPanel(activeGameLabels);
    }

    /**
     * Show the "Add new game menu" button
     */
    public void showAddNewGameButton() {
        addMenuButton.setVisible(true);
    }

    /**
     * Hide the "Add new game menu" button
     */
    public void hideAddNewGameButton() {
        addMenuButton.setVisible(false);
    }

    /**
     * Show the game menu index on the title.
     */
    public void updatePaging() {
        if (menuIndex != 0) {
            titleLabel.setText("  " + getTitleText() + " [" + (menuIndex + 1) + "]");
        } else {
            titleLabel.setText("  " + getTitleText());
        }
        if (menuIndex == gameLabelLists.size() - 1 && activeGameLabels.size() > 0 && showingNewMenuButton) {
            showAddNewGameButton();
        } else {
            hideAddNewGameButton();
        }
    }

    /**
     * Toggle the secret GameLabels.
     */
    public void toggleSecretGameLabels() {
        if (secretGamesShown) {
            goToPreviousGameMenu();
        } else {
            goToNextGameMenu();
        }
        redrawGameGridPanel(activeGameLabels);
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
            fixWindowPosition();
            fadeInJFrame();
        }
    }

    /**
     * Increase the MainFrame's scale.
     */
    public void increaseScale() {
        if (frameScale < getMaxFrameScale()) {
            fadeOutJFrame();
            frameScale += 0.1;
            setFrameScale(frameScale);
            fixWindowPosition();
            fadeInJFrame();
        }
    }

    /**
     * Adjust main window's position if it's out of bounds.
     */
    public void fixWindowPosition() {
        if (getLocation().x + mainPanel.getSize().width >= Toolkit.getDefaultToolkit().getScreenSize().getWidth()) {
            setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - mainPanel.getSize().width), getLocation().y);
        } else if (getLocation().x < 0) {
            setLocation(-7, getLocation().y);
        }
        if (getLocation().y + mainPanel.getSize().height >= Toolkit.getDefaultToolkit().getScreenSize().getHeight()) {
            setLocation(getLocation().x, (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - mainPanel.getSize().height));
        } else if (getLocation().y < -0) {
            setLocation(getLocation().x, -7);
        }
    }

    /**
     * Navigate the menu according to each controller button press.
     *
     * @param button XInputButton object containing the button that was pressed
     */
    private void doControllerButtonAction(XInputButton button) {
        disableCursor();
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
                goToPreviousGameMenu();
                break;
            }

            // If RT is pressed, increase the window's scale
            case "RIGHT_SHOULDER": {
                goToNextGameMenu();
                break;
            }

            // If a DPAD button is pressed, navigate the activeGameLabels
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
            case "GUIDE_BUTTON": {
                doLaunchSteamBigPicture();
                break;
            }
            default:
                break;
        }
    }

    /**
     * Change the focused GameLabel.
     *
     * @param indexDelta Integer containing the delta (difference) of the new focused GameLabel compared to the previous one.
     */
    private void changeFocusedGamelabel(int indexDelta) {
        if (getFocusedGameLabel() != null) {
            for (int i = 0; i < activeGameLabels.size(); i++) {
                if (focusedGameLabel == activeGameLabels.get(i) && i + indexDelta >= 0 && i + indexDelta < activeGameLabels.size()) {
                    activeGameLabels.get(i + indexDelta).focusOnGameLabel();
                    break;
                }
            }
        } else {
            if (!activeGameLabels.isEmpty()) {
                switch (indexDelta) {
                    case 1: {
                        if (activeGameLabels.size() > 2) {
                            activeGameLabels.get(2).focusOnGameLabel();
                        } else {
                            activeGameLabels.get(0).focusOnGameLabel();
                        }
                        break;
                    }
                    case 3: {
                        if (activeGameLabels.size() > 1) {
                            activeGameLabels.get(1).focusOnGameLabel();
                        } else {
                            activeGameLabels.get(0).focusOnGameLabel();
                        }
                        break;
                    }
                    case -3: {
                        if (activeGameLabels.size() > 7) {
                            activeGameLabels.get(7).focusOnGameLabel();
                        } else {
                            activeGameLabels.get(0).focusOnGameLabel();
                        }
                        break;
                    }
                    case -1: {
                        activeGameLabels.get(0).focusOnGameLabel();
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Move main window on the x and y axis according to given values.
     */
    private void moveWindow(int xIncrement, int yIncrement) {
        int frameSizeDiff = shadowPanel.getSize().height - mainPanel.getSize().height;
        if (getLocation().x + xIncrement >= -frameSizeDiff && getLocation().y + yIncrement >= -frameSizeDiff
                && getLocation().x + xIncrement + mainPanel.getSize().width <= Toolkit.getDefaultToolkit().getScreenSize().getWidth()
                && getLocation().y + yIncrement + mainPanel.getSize().height <= Toolkit.getDefaultToolkit().getScreenSize().getHeight()) {
            setLocation(getLocation().x + xIncrement, getLocation().y + yIncrement);
        }
    }

    /**
     * Disable the cursor on the main window.
     */
    private void disableCursor() {
        if (!ignoreMouse) {
            // Create a new blank cursor.
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                    new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank cursor");
            // Set the blank cursor to the JFrame.
            setCursor(blankCursor);
            statusBarPanel.setCursor(blankCursor);
            ignoreMouse = true;
            mouseX = MouseInfo.getPointerInfo().getLocation().x;
            mouseY = MouseInfo.getPointerInfo().getLocation().y;
        }
    }

    /**
     * Enable the cursor on the main window.
     */
    public void enableCursor() {
        if (ignoreMouse) {
            setCursor(Cursor.getDefaultCursor());
            statusBarPanel.setCursor(Cursor.getDefaultCursor());
            ignoreMouse = false;
        }
    }

    // Fields
    private JXPanel shadowPanel;
    private JPanel gameGridPanel, statusBarPanel, buttonsPanel, mainPanel;
    private JButton exitButton, programSettingsButton, steamButton, addMenuButton;
    private JLabel emptyGridLabel, titleLabel;
    private boolean hasBorder = true, hasSpace = false, autoExit = false, hasShadow = true, fullyBooted = false, focusing = true, useSteam = true, ignoreMouse = false, secretGamesShown = false, showingNewMenuButton = true;
    private Color buttonColor = Color.BLACK, barColor = new Color(204, 204, 204), borderColor = Color.GRAY, backgroundColor = Color.WHITE, shadowColor = Color.BLACK;
    private ArrayList<GameLabel> activeGameLabels = new ArrayList<>(), focusedGameLabels = new ArrayList<>();
    private ArrayList<ArrayList<GameLabel>> gameLabelLists = new ArrayList<>();
    private int numberOfGames = 0, mouseX = 0, mouseY = 0, menuIndex = 0;
    private double frameScale = 1.0;
    private String gameName, titleText = "Game Organizer";
    private Font customFont;
    private XInputDevice controller;
    private GameLabel focusedGameLabel = null;
}
