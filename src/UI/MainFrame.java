/*
 * mpaterakis, 2018
 */
package UI;

import Plugins.*;
import DataManagement.ProcessXML;
import GameOrganizer.Game;
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
        
        // Set JFrame parameters
        setResizable(false);
        InputStream stream = getClass().getResourceAsStream("/Files/Icon.png");
        try {
            setIconImage(new ImageIcon(ImageIO.read(stream)).getImage());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "IconImage Error: Cannot load statusbar icon", "Icon Error", JOptionPane.ERROR_MESSAGE);
        }
        setTitle("Game Organizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        ProcessXML.LoadXML(this);
        setBorderAndSize(hasBorder, borderColor);
        paintShadow();
        setOpacity(0);
        setLocationRelativeTo(null);
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

    public boolean getHasShadow() {
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

    public boolean usesFocusing() {
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
                setSize(810, 860);
            } else {
                mainPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, borderColor));
                setSize(810, 860);
            }
            shadowPanel.remove(mainPanel);
            shadowPanel.add(mainPanel);
        } else {
            gameGridPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.WHITE));
            if (hasBorder) {
                mainPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor));
                setSize(777, 797);
            } else {
                mainPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, borderColor));
                setSize(778, 798);
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
    private void fadeInJFrame() {
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

    // Fade out animation
    private void fadeOutJFrame() {
        for (float i = 1; i > 0; i -= 0.03) {
            setOpacity(i);
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
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
            GameLabel gameLabel = new GameLabel(new Game(iconFile, files[0].getAbsoluteFile().getAbsolutePath(), gameName), this);
            gameLabels.add(gameLabel);

            // Add gameLabel to gameGridPanel
            gameGridPanel.add(gameLabel);

            // Redraw the gameGridPanel
            redrawGameGridPanel(gameLabels);
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
    private String gameName, titleText = "Game Organizer";
    private Font customFont;
}
