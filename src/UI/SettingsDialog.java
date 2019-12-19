/*
 * mpaterakis, 2018
 */
package UI;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;

/**
 * Creates the Settings dialog from which the user can manipulate the program's look.
 *
 * @author mpaterakis
 */
public class SettingsDialog extends JDialog {

    /**
     * Create a SettingsDialog object.
     *
     * @param mainFrame MainFrame object to be manipulated from this class
     */
    public SettingsDialog(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
    }

    /**
     * Initialize the SettingsDialog's components.
     */
    private void initComponents() {

        // Color Choosers
        barColorChooser = new JColorChooser();
        barColorChooser.setBorder(BorderFactory.createTitledBorder("Bar Color"));
        barColorChooser.setName("Bar Color");
        barColorChooser.setPreviewPanel(new JPanel());
        removeExcessChooserTabs(barColorChooser);
        barColorChooser.getSelectionModel().addChangeListener(e -> doSetBarColor());

        buttonColorChooser = new JColorChooser();
        buttonColorChooser.setBorder(BorderFactory.createTitledBorder("Bar Buttons Color"));
        buttonColorChooser.setName("Bar Buttons Color");
        buttonColorChooser.setPreviewPanel(new JPanel());
        removeExcessChooserTabs(buttonColorChooser);
        buttonColorChooser.getSelectionModel().addChangeListener(e -> doSetButtonColor());

        borderColorChooser = new JColorChooser();
        borderColorChooser.setColor(mainFrame.getBorderColor());
        borderColorChooser.setBorder(BorderFactory.createTitledBorder("Window Border Color"));
        borderColorChooser.setName("Window Border Color");
        borderColorChooser.setPreviewPanel(new JPanel());
        removeExcessChooserTabs(borderColorChooser);
        borderColorChooser.getSelectionModel().addChangeListener(e -> doSetBorderColor());

        backgroundColorChooser = new JColorChooser();
        backgroundColorChooser.setBorder(BorderFactory.createTitledBorder("Background Color"));
        backgroundColorChooser.setName("Background Color");
        backgroundColorChooser.setPreviewPanel(new JPanel());
        removeExcessChooserTabs(backgroundColorChooser);
        backgroundColorChooser.getSelectionModel().addChangeListener(e -> doSetBackgroundColor());

        shadowColorChooser = new JColorChooser();
        shadowColorChooser.setBorder(BorderFactory.createTitledBorder("Shadow Color"));
        shadowColorChooser.setName("Shadow Color");
        shadowColorChooser.setPreviewPanel(new JPanel());
        removeExcessChooserTabs(shadowColorChooser);
        shadowColorChooser.getSelectionModel().addChangeListener(e -> doSetShadowColor());

        // JSliders
        frameScaleSlider = new JSlider(5, (int) (mainFrame.getMaxFrameScale() * 10), (int) (mainFrame.getFrameScale() * 10));
        Hashtable sliderTable = new Hashtable();
        for (int i = 5; i < (int) (mainFrame.getMaxFrameScale() * 10) + 1; i++) {
            sliderTable.put(i, new JLabel(String.valueOf((double) i / 10)));
        }
        frameScaleSlider.setLabelTable(sliderTable);
        frameScaleSlider.setPaintLabels(true);
        frameScaleSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent me) {
                // Repaint the new window after resizing it
                createRepaintThread();
            }
        });
        frameScaleSlider.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                switch (ke.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (mainFrame.getFrameScale() > 0.5) {
                            mainFrame.setFrameScale(mainFrame.getFrameScale() - 0.1);
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (mainFrame.getFrameScale() < mainFrame.getMaxFrameScale()) {
                            mainFrame.setFrameScale(mainFrame.getFrameScale() + 0.1);
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        // JButtons
        changeTitleButton = new JButton("Change Window Title");
        changeTitleButton.setBackground(new Color(209, 209, 209));
        changeTitleButton.setFocusPainted(false);
        changeTitleButton.addActionListener(e -> doChangeTitle());

        revertDefaultsButton = new JButton("Revert to default");
        revertDefaultsButton.setBackground(new Color(209, 209, 209));
        revertDefaultsButton.setFocusPainted(false);
        revertDefaultsButton.addActionListener(e -> doRevertDefaults());

        changeSpacingOption = new JButton("Switch spacing");
        changeSpacingOption.setBackground(new Color(209, 209, 209));
        changeSpacingOption.setFocusPainted(false);
        changeSpacingOption.setFocusPainted(false);
        changeSpacingOption.addActionListener(e -> doChangeSpacing());

        borderColorButton = new JButton("Change Color");
        borderColorButton.addActionListener(e -> StaticDialogs.createColorPicker(borderColorChooser));
        borderColorButton.setBackground(new Color(209, 209, 209));
        borderColorButton.setFocusPainted(false);

        shadowColorButton = new JButton("Change Color");
        shadowColorButton.addActionListener(e -> StaticDialogs.createColorPicker(shadowColorChooser));
        shadowColorButton.setBackground(new Color(209, 209, 209));
        shadowColorButton.setFocusPainted(false);

        backgroundColorButton = new JButton("Change Background Color");
        backgroundColorButton.addActionListener(e -> StaticDialogs.createColorPicker(backgroundColorChooser));
        backgroundColorButton.setBackground(new Color(209, 209, 209));
        backgroundColorButton.setFocusPainted(false);

        barColorButton = new JButton("Change Bar Color");
        barColorButton.addActionListener(e -> StaticDialogs.createColorPicker(barColorChooser));
        barColorButton.setBackground(new Color(209, 209, 209));
        barColorButton.setFocusPainted(false);

        buttonsColorButton = new JButton("Change Bar Buttons Color");
        buttonsColorButton.addActionListener(e -> StaticDialogs.createColorPicker(buttonColorChooser));
        buttonsColorButton.setBackground(new Color(209, 209, 209));
        buttonsColorButton.setFocusPainted(false);

        // JCheckBoxes
        autoExitCheckbox = new JCheckBox();
        autoExitCheckbox.setSelected(mainFrame.getAutoExit());
        autoExitCheckbox.addActionListener(e -> {
            if (autoExitCheckbox.isSelected()) {
                doEnableAutoExit();
            } else {
                doDisableAutoExit();
            }
        });

        hasFocusingCheckbox = new JCheckBox();
        hasFocusingCheckbox.setSelected(mainFrame.hasFocusing());
        hasFocusingCheckbox.addActionListener(e -> {
            if (hasFocusingCheckbox.isSelected()) {
                doEnableFocusing();
            } else {
                doDisableFocusing();
            }
        });

        hasShadowCheckbox = new JCheckBox();
        hasShadowCheckbox.setSelected(mainFrame.hasShadow());
        hasShadowCheckbox.addActionListener(e -> {
            if (hasShadowCheckbox.isSelected()) {
                doEnableShadow();
            } else {
                doDisableShadow();
            }
        });

        hasBorderCheckBox = new JCheckBox();
        hasBorderCheckBox.setSelected(mainFrame.hasBorder());
        hasBorderCheckBox.addActionListener(e -> {
            if (hasBorderCheckBox.isSelected()) {
                doSetBorderColor();
            } else {
                doDisableBorder();
            }
        });

        usingSteamCheckBox = new JCheckBox();
        usingSteamCheckBox.setSelected(mainFrame.isUsingSteam());
        usingSteamCheckBox.addActionListener(e -> {
            if (usingSteamCheckBox.isSelected()) {
                doEnableSteamIcon();
            } else {
                doDisableSteamIcon();
            }
        });

        showMenuCheckbox = new JCheckBox();
        showMenuCheckbox.setSelected(mainFrame.isShowingNewMenuButton());
        showMenuCheckbox.addActionListener(e -> {
            if (showMenuCheckbox.isSelected()) {
                doEnableNewMenuButton();
            } else {
                doDisableNewMenuButton();
            }
        });

        // JTextFields
        titleField = new JTextField(mainFrame.getTitleText());

        // JPanels
        spareColorButtonsPanel = new JPanel(new FlowLayout());
        spareColorButtonsPanel.add(barColorButton);
        spareColorButtonsPanel.add(buttonsColorButton);
        spareColorButtonsPanel.add(backgroundColorButton);

        titleSettingsPanel = new JPanel(new BorderLayout());
        titleSettingsPanel.add(new JLabel("  Window Title: "), BorderLayout.WEST);
        titleSettingsPanel.add(titleField, BorderLayout.CENTER);
        titleSettingsPanel.add(changeTitleButton, BorderLayout.EAST);

        shadowButtonsPanel = new JPanel(new FlowLayout());
        shadowButtonsPanel.add(hasShadowCheckbox);
        shadowButtonsPanel.add(shadowColorButton);
        shadowPanel = new JPanel(new BorderLayout());
        shadowPanel.add(new JLabel("  Use drop shadow on main window: "), BorderLayout.WEST);
        shadowPanel.add(shadowButtonsPanel, BorderLayout.EAST);

        borderButtonsPanel = new JPanel(new FlowLayout());
        borderButtonsPanel.add(hasBorderCheckBox);
        borderButtonsPanel.add(borderColorButton);
        borderPanel = new JPanel(new BorderLayout());
        borderPanel.add(new JLabel("  Use border on main window: "), BorderLayout.WEST);
        borderPanel.add(borderButtonsPanel, BorderLayout.EAST);

        steamPanel = new JPanel(new BorderLayout());
        steamPanel.add(new JLabel("  Show Steam icon on main window: "), BorderLayout.WEST);
        steamPanel.add(usingSteamCheckBox, BorderLayout.EAST);

        menuPanel = new JPanel(new BorderLayout());
        menuPanel.add(new JLabel("  Show 'New Menu' button on main window: "), BorderLayout.WEST);
        menuPanel.add(showMenuCheckbox, BorderLayout.EAST);

        focusingPanel = new JPanel(new BorderLayout());
        focusingPanel.add(new JLabel("  Dim the games that are not focused: "), BorderLayout.WEST);
        focusingPanel.add(hasFocusingCheckbox, BorderLayout.EAST);

        gameAutoExitPanel = new JPanel(new BorderLayout());
        gameAutoExitPanel.add(new JLabel("  Exit GameOrganizer after a game is launched: "), BorderLayout.WEST);
        gameAutoExitPanel.add(autoExitCheckbox, BorderLayout.EAST);

        sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.add(new JLabel("  Window Scale:     "), BorderLayout.WEST);
        sliderPanel.add(frameScaleSlider, BorderLayout.CENTER);

        middlePanel = new JPanel(new GridLayout(9, 1));
        middlePanel.add(spareColorButtonsPanel);
        middlePanel.add(shadowPanel);
        middlePanel.add(borderPanel);
        middlePanel.add(focusingPanel);
        middlePanel.add(gameAutoExitPanel);
        middlePanel.add(steamPanel);
        middlePanel.add(menuPanel);
        middlePanel.add(titleSettingsPanel);
        middlePanel.add(sliderPanel);
        middlePanel.setBorder(new TitledBorder("Settings"));

        bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(changeSpacingOption);
        bottomPanel.add(revertDefaultsButton);

        // Add JPanel
        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add KeyListener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Change the main window's appearance according to each keypress
                doKeyAction(e);
            }
        });

        setFocusable(true);
        setSize(580, 400);
        setTitle("Program Settings");
        setResizable(false);
        setLocationRelativeTo(null);
        setModal(true);
        setVisible(true);
    }

    /**
     * Switch spacing of the MainFrame.
     */
    private void doChangeSpacing() {
        mainFrame.switchHasSpace();
    }

    /**
     * Set "default" values.
     */
    private void doRevertDefaults() {
        mainFrame.setHasSpace(false);
        mainFrame.setBarColor(new Color(204, 204, 204));
        mainFrame.setButtonColor(Color.BLACK);
        mainFrame.setBorderAndSize(true, new Color(204, 204, 204));
        mainFrame.setBackgroundColor(Color.WHITE);
        mainFrame.setTitleText("Game Organizer");
    }

    /**
     * Set the new MainFrame title.
     */
    private void doChangeTitle() {
        mainFrame.setTitleText(titleField.getText());
    }

    /**
     * Set the new border as no border (remove the border).
     */
    private void doDisableBorder() {
        // Change color to an unavailable swatch, needed for stateChanged
        mainFrame.setBorderAndSize(false, mainFrame.getBorderColor());
    }

    /**
     * Remove all tabs except "Swatches" from a JColorChooser.
     *
     * @param colorChooser JColorChooser object to be customized
     */
    private void removeExcessChooserTabs(JColorChooser colorChooser) {
        AbstractColorChooserPanel[] panels2 = colorChooser.getChooserPanels();
        for (AbstractColorChooserPanel accp : panels2) {
            if (!accp.getDisplayName().equals("Swatches")) {
                colorChooser.removeChooserPanel(accp);
            }
        }
    }

    /**
     * Set the new bar color.
     */
    private void doSetBarColor() {
        mainFrame.setBarColor(barColorChooser.getColor());
    }

    /**
     * Set the new border color.
     */
    private void doSetBorderColor() {
        hasBorderCheckBox.setSelected(true);
        mainFrame.setBorderAndSize(true, borderColorChooser.getColor());
    }

    /**
     * Set the new background color.
     */
    private void doSetBackgroundColor() {
        mainFrame.setBackgroundColor(backgroundColorChooser.getColor());
    }

    /**
     * Set the new shadow color.
     */
    private void doSetShadowColor() {
        mainFrame.setHasShadow(true);
        mainFrame.setShadowColor(shadowColorChooser.getColor());
        mainFrame.paintShadow();
        hasShadowCheckbox.setSelected(true);
    }

    /**
     * Set the new button color.
     */
    private void doSetButtonColor() {
        mainFrame.setButtonColor(buttonColorChooser.getColor());
    }

    /**
     * Enable auto-exit after game launch.
     */
    private void doEnableAutoExit() {
        mainFrame.setAutoExit(true);
    }

    /**
     * Disable auto-exit after game launch.
     */
    private void doDisableAutoExit() {
        mainFrame.setAutoExit(false);
    }

    /**
     * Enable shadow on MainFrame.
     */
    private void doEnableShadow() {
        mainFrame.setHasShadow(true);
        mainFrame.paintShadow();
    }

    /**
     * Disable shadow on MainFrame.
     */
    private void doDisableShadow() {
        mainFrame.setHasShadow(false);
        mainFrame.paintShadow();
    }

    /**
     * Enable the Steam icon on MainFrame.
     */
    private void doEnableSteamIcon() {
        mainFrame.setUseSteam(true);
    }

    /**
     * Disable the Steam icon on MainFrame.
     */
    private void doDisableSteamIcon() {
        mainFrame.setUseSteam(false);
    }

    /**
     * Enable the New Menu button on MainFrame.
     */
    private void doEnableNewMenuButton() {
        mainFrame.setShowingNewMenuButton(true);
    }

    /**
     * Disable the New Menu button on MainFrame.
     */
    private void doDisableNewMenuButton() {
        mainFrame.setShowingNewMenuButton(false);
    }

    /**
     * Enable focusing for GameLabels.
     */
    private void doEnableFocusing() {
        mainFrame.setFocusing(true);
        mainFrame.redrawGameGridPanel(mainFrame.getActiveGameLabels());
    }

    /**
     * Disable focusing for GameLabels.
     */
    private void doDisableFocusing() {
        mainFrame.setFocusing(false);
        GameLabel.resetAllGameLabelFocus(mainFrame);
    }

    /**
     * Set MainFrame's frame scale.
     *
     * @param frameScale Double containing the MainFrame's new frame scale
     */
    private void doSetFrameScale(double frameScale) {
        mainFrame.setFrameScale(frameScale);
        mainFrame.redrawGameGridPanel(mainFrame.getActiveGameLabels());
        mainFrame.fixWindowPosition();
    }

    /**
     * Changes the main window's appearance according to each key press.
     *
     * @param e KeyEvent that was triggered
     */
    private void doKeyAction(KeyEvent e) {

        switch (e.getKeyCode()) {
            // If F5 is pressed, center the main window
            case KeyEvent.VK_F5:
                mainFrame.fadeOutJFrame();
                setLocationRelativeTo(null);
                mainFrame.setLocationRelativeTo(null);
                mainFrame.fadeInJFrame();
                break;
            // If + is pressed, increase the window's scale
            case KeyEvent.VK_ADD:
                if (mainFrame.getFrameScale() < 1.5) {
                    mainFrame.setFrameScale(mainFrame.getFrameScale() + 0.1);
                    frameScaleSlider.setValue((int) (mainFrame.getFrameScale() * 10));
                }
                break;
            // If - is pressed, decrease the window's scale
            case KeyEvent.VK_SUBTRACT:
                if (mainFrame.getFrameScale() > 0.5) {
                    mainFrame.setFrameScale(mainFrame.getFrameScale() - 0.1);
                    frameScaleSlider.setValue((int) (mainFrame.getFrameScale() * 10));
                }
                break;
            default:
                break;
        }
    }

    /**
     * Create a new thread for resizing and repainting the MainFrame.
     */
    private void createRepaintThread() {
        Thread repaintingThread = new Thread(() -> {
            doSetFrameScale((double) frameScaleSlider.getValue() / 10);
        });
        repaintingThread.start();
        try {
            synchronized (repaintingThread) {
                repaintingThread.wait();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(SettingsDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Fields
    private JPanel bottomPanel, steamPanel, titleSettingsPanel, gameAutoExitPanel, shadowPanel, shadowButtonsPanel, middlePanel, focusingPanel, borderPanel, borderButtonsPanel, sliderPanel, spareColorButtonsPanel, menuPanel;
    private JColorChooser barColorChooser, buttonColorChooser, borderColorChooser, backgroundColorChooser, shadowColorChooser;
    private JSlider frameScaleSlider;
    private JButton revertDefaultsButton, changeSpacingOption, changeTitleButton, borderColorButton, shadowColorButton, buttonsColorButton, barColorButton, backgroundColorButton;
    private JCheckBox autoExitCheckbox, hasShadowCheckbox, hasFocusingCheckbox, hasBorderCheckBox, usingSteamCheckBox, showMenuCheckbox;
    private JTextField titleField;
    private final MainFrame mainFrame;
}
