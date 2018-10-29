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
import javax.swing.colorchooser.AbstractColorChooserPanel;

/**
 * Creates the Settings Panel which can manipulate the program's look
 *
 * @author mpaterakis
 */
public class SettingsDialog extends JDialog {

    public SettingsDialog(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
    }

    private void initComponents() {

        // Color Choosers
        barColorChooser = new JColorChooser();
        barColorChooser.setBorder(BorderFactory.createTitledBorder("Bar Color"));
        barColorChooser.setPreviewPanel(new JPanel());
        removeExcessChooserTabs(barColorChooser);
        barColorChooser.getSelectionModel().addChangeListener(e -> doSetBarColor());

        buttonColorChooser = new JColorChooser();
        buttonColorChooser.setBorder(BorderFactory.createTitledBorder("Bar Buttons Color"));
        buttonColorChooser.setPreviewPanel(new JPanel());
        removeExcessChooserTabs(buttonColorChooser);
        buttonColorChooser.getSelectionModel().addChangeListener(e -> doSetButtonColor());

        borderColorChooser = new JColorChooser();
        borderColorChooser.setColor(mainFrame.getBorderColor());
        System.out.println(mainFrame.getBorderColor());
        borderColorChooser.setBorder(BorderFactory.createTitledBorder("Window Border Color"));
        borderColorChooser.setPreviewPanel(new JPanel());
        removeExcessChooserTabs(borderColorChooser);
        borderColorChooser.getSelectionModel().addChangeListener(e -> doSetBorderColor());

        backgroundColorChooser = new JColorChooser();
        backgroundColorChooser.setBorder(BorderFactory.createTitledBorder("Background Color"));
        backgroundColorChooser.setPreviewPanel(new JPanel());
        removeExcessChooserTabs(backgroundColorChooser);
        backgroundColorChooser.getSelectionModel().addChangeListener(e -> doSetBackgroundColor());

        shadowColorChooser = new JColorChooser();
        shadowColorChooser.setBorder(BorderFactory.createTitledBorder("Shadow Color"));
        shadowColorChooser.setPreviewPanel(new JPanel());
        removeExcessChooserTabs(shadowColorChooser);
        shadowColorChooser.getSelectionModel().addChangeListener(e -> doSetShadowColor());

        // JSliders
        frameScaleSlider = new JSlider(5, 15, (int) (mainFrame.getFrameScale() * 10));
        Hashtable sliderTable = new Hashtable();
        for (int i = 5; i < 16; i++) {
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
                        if (mainFrame.getFrameScale() < 1.5) {
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

        // JTextFields
        titleField = new JTextField(mainFrame.getTitleText());

        // JPanels
        titleSettingsPanel = new JPanel(new BorderLayout());
        titleSettingsPanel.add(new JLabel(" Window Title: "), BorderLayout.WEST);
        titleSettingsPanel.add(titleField, BorderLayout.CENTER);
        titleSettingsPanel.add(changeTitleButton, BorderLayout.EAST);

        shadowPanel = new JPanel(new BorderLayout());
        shadowPanel.add(new JLabel(" Use drop shadow on main window: "), BorderLayout.WEST);
        shadowPanel.add(hasShadowCheckbox, BorderLayout.EAST);
        
        borderPanel = new JPanel(new BorderLayout());
        borderPanel.add(new JLabel(" Use border on main window: "), BorderLayout.WEST);
        borderPanel.add(hasBorderCheckBox, BorderLayout.EAST);

        focusingPanel = new JPanel(new BorderLayout());
        focusingPanel.add(new JLabel(" Dim the games that are not focused: "), BorderLayout.WEST);
        focusingPanel.add(hasFocusingCheckbox, BorderLayout.EAST);

        gameAutoExitPanel = new JPanel(new BorderLayout());
        gameAutoExitPanel.add(new JLabel(" Exit GameOrganizer after a game is launched: "), BorderLayout.WEST);
        gameAutoExitPanel.add(autoExitCheckbox, BorderLayout.EAST);

        sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.add(new JLabel(" Window Scale:     "), BorderLayout.WEST);
        sliderPanel.add(frameScaleSlider, BorderLayout.CENTER);

        middlePanel = new JPanel(new GridLayout(6, 1));
        middlePanel.add(shadowPanel);
        middlePanel.add(borderPanel);
        middlePanel.add(focusingPanel);
        middlePanel.add(gameAutoExitPanel);
        middlePanel.add(titleSettingsPanel);
        middlePanel.add(sliderPanel);

        centerPanel = new JPanel(new GridLayout(5, 1));
        centerPanel.add(barColorChooser);
        centerPanel.add(buttonColorChooser);
        centerPanel.add(borderColorChooser);
        centerPanel.add(backgroundColorChooser);
        centerPanel.add(shadowColorChooser);

        completeSettingsPanel = new JPanel(new BorderLayout());
        completeSettingsPanel.add(centerPanel, BorderLayout.CENTER);
        completeSettingsPanel.add(middlePanel, BorderLayout.SOUTH);

        bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(changeSpacingOption);
        bottomPanel.add(revertDefaultsButton);

        // Add JPanel
        add(completeSettingsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add KeyListener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Change the main window's appearance according to each keypress
                doKeyAction(e);
            }
        });

        // Set JDialog parameters
        int height = 900;
        if (mainFrame.getFrameScale() < 1.0 && Toolkit.getDefaultToolkit().getScreenSize().getHeight() < 900) {
            height *= mainFrame.getFrameScale();
        }
        setFocusable(true);
        setSize(440, height);
        setTitle("Program Settings");
        setResizable(false);
        setLocationRelativeTo(null);
        setModal(true);
        setVisible(true);
    }

    // Custom Functions
    // Switch spacing
    private void doChangeSpacing() {
        mainFrame.switchHasSpace();
    }

    // Set "default" values
    private void doRevertDefaults() {
        mainFrame.setHasSpace(false);
        mainFrame.setBarColor(new Color(204, 204, 204));
        mainFrame.setButtonColor(Color.BLACK);
        mainFrame.setBorderAndSize(true, new Color(204, 204, 204));
        mainFrame.setBackgroundColor(Color.WHITE);
        mainFrame.setTitleText("Game Organizer");
    }

    // Set the new Window Title text
    private void doChangeTitle() {
        mainFrame.setTitleText(titleField.getText());
    }

    // Set the new border as no border (remove the border)
    private void doDisableBorder() {
        // Change color to an unavailable swatch, needed for stateChanged
        mainFrame.setBorderAndSize(false, mainFrame.getBorderColor());
    }

    // This loop removes all tabs except "Swatches" from the colorChooser
    private void removeExcessChooserTabs(JColorChooser colorChooser) {
        AbstractColorChooserPanel[] panels2 = colorChooser.getChooserPanels();
        for (AbstractColorChooserPanel accp : panels2) {
            if (!accp.getDisplayName().equals("Swatches")) {
                colorChooser.removeChooserPanel(accp);
            }
        }
    }

    // Set the new bar color
    private void doSetBarColor() {
        mainFrame.setBarColor(barColorChooser.getColor());
    }

    // Set the new border color
    private void doSetBorderColor() {
        hasBorderCheckBox.setSelected(true);
        mainFrame.setBorderAndSize(true, borderColorChooser.getColor());
    }

    // Set the new background color
    private void doSetBackgroundColor() {
        mainFrame.setBackgroundColor(backgroundColorChooser.getColor());
    }

    // Set the new shadow color
    private void doSetShadowColor() {
        mainFrame.setHasShadow(true);
        mainFrame.setShadowColor(shadowColorChooser.getColor());
        mainFrame.paintShadow();
        hasShadowCheckbox.setSelected(true);
    }

    // Set the new button color
    private void doSetButtonColor() {
        mainFrame.setButtonColor(buttonColorChooser.getColor());
    }

    // Enable auto-exit after game launch
    private void doEnableAutoExit() {
        mainFrame.setAutoExit(true);
    }

    // Enable auto-exit after game launch
    private void doDisableAutoExit() {
        mainFrame.setAutoExit(false);
    }

    // Enable shadow
    private void doEnableShadow() {
        mainFrame.setHasShadow(true);
        mainFrame.paintShadow();
    }

    // Disable shadow
    private void doDisableShadow() {
        mainFrame.setHasShadow(false);
        mainFrame.paintShadow();
    }

    // Enable focusing
    private void doEnableFocusing() {
        mainFrame.setFocusing(true);
        mainFrame.redrawGameGridPanel(mainFrame.getGameLabels());
    }

    // Disable focusing
    private void doDisableFocusing() {
        mainFrame.setFocusing(false);
    }

    // Set main window's frame scale
    private void doSetFrameScale(double frameScale) {
        int height = 900;
        if (mainFrame.getFrameScale() < 1.0 && Toolkit.getDefaultToolkit().getScreenSize().getHeight() < 900) {
            height *= mainFrame.getFrameScale();
        }
        setSize(440, height);
        mainFrame.setFrameScale(frameScale);
        mainFrame.redrawGameGridPanel(mainFrame.getGameLabels());
    }

    // Changes the main window's appearance according to each keypress
    private void doKeyAction(KeyEvent e) {

        // If F5 is pressed, center the main window
        if (e.getKeyCode() == KeyEvent.VK_F5) {
            mainFrame.fadeOutJFrame();
            setLocationRelativeTo(null);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.fadeInJFrame();
        } // If + is pressed, increase the window's scale
        else if (e.getKeyCode() == KeyEvent.VK_ADD) {
            if (mainFrame.getFrameScale() < 1.5) {
                mainFrame.setFrameScale(mainFrame.getFrameScale() + 0.1);
                frameScaleSlider.setValue((int) (mainFrame.getFrameScale() * 10));
            }
        } // If - is pressed, decrease the window's scale
        else if (e.getKeyCode() == KeyEvent.VK_SUBTRACT) {
            if (mainFrame.getFrameScale() > 0.5) {
                mainFrame.setFrameScale(mainFrame.getFrameScale() - 0.1);
                frameScaleSlider.setValue((int) (mainFrame.getFrameScale() * 10));
            }
        }
    }

    // Make a new thread for resizing and repainting the window
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
    private JPanel centerPanel, bottomPanel, titleSettingsPanel, completeSettingsPanel, gameAutoExitPanel, shadowPanel, middlePanel, focusingPanel, borderPanel, sliderPanel;
    private JColorChooser barColorChooser, buttonColorChooser, borderColorChooser, backgroundColorChooser, shadowColorChooser;
    private JSlider frameScaleSlider;
    private JButton revertDefaultsButton, changeSpacingOption, changeTitleButton;
    private JCheckBox autoExitCheckbox, hasShadowCheckbox, hasFocusingCheckbox, hasBorderCheckBox;
    private JTextField titleField;
    private MainFrame mainFrame;
}
