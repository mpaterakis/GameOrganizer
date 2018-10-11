/*
 * mpaterakis, 2018
 */
package UI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.*;

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

        // JButtons
        disableBorderButton = new JButton("Remove Border");
        disableBorderButton.setBackground(new Color(209, 209, 209));
        disableBorderButton.setFocusPainted(false);
        disableBorderButton.addActionListener(e -> doDisableBorder());

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

        // JRadioButtons
        radioAutoExitTrue = new JRadioButton("Yes");
        radioAutoExitTrue.addActionListener(e -> doEnableAutoExit());

        radioAutoExitFalse = new JRadioButton("No");
        radioAutoExitFalse.addActionListener(e -> doDisableAutoExit());

        if (mainFrame.getAutoExit()) {
            radioAutoExitTrue.setSelected(true);
        } else {
            radioAutoExitFalse.setSelected(true);
        }

        radioHasShadowTrue = new JRadioButton("Yes");
        radioHasShadowTrue.addActionListener(e -> doEnableShadow());

        radioHasShadowFalse = new JRadioButton("No");
        radioHasShadowFalse.addActionListener(e -> doDisableShadow());

        if (mainFrame.getHasShadow()) {
            radioHasShadowTrue.setSelected(true);
        } else {
            radioHasShadowFalse.setSelected(true);
        }
        
        radioFocusingTrue = new JRadioButton("Yes");
        radioFocusingTrue.addActionListener(e -> doEnableFocusing());

        radioFocusingFalse = new JRadioButton("No");
        radioFocusingFalse.addActionListener(e -> doDisableFocusing());

        if (mainFrame.usesFocusing()) {
            radioFocusingTrue.setSelected(true);
        } else {
            radioFocusingFalse.setSelected(true);
        }

        // Add the JRadioButtons in ButtonGroups
        ButtonGroup radioButtons1 = new ButtonGroup();
        radioButtons1.add(radioAutoExitTrue);
        radioButtons1.add(radioAutoExitFalse);

        ButtonGroup radioButtons2 = new ButtonGroup();
        radioButtons2.add(radioHasShadowTrue);
        radioButtons2.add(radioHasShadowFalse);
        
        ButtonGroup radioButtons3 = new ButtonGroup();
        radioButtons3.add(radioFocusingTrue);
        radioButtons3.add(radioFocusingFalse);

        // JTextFields
        titleField = new JTextField(mainFrame.getTitleText());

        // JPanels
        titleSettingsPanel = new JPanel(new BorderLayout());
        titleSettingsPanel.add(new JLabel(" Window Title: "), BorderLayout.WEST);
        titleSettingsPanel.add(titleField, BorderLayout.CENTER);
        titleSettingsPanel.add(changeTitleButton, BorderLayout.EAST);

        radioAutoExitPanel = new JPanel(new FlowLayout());
        radioAutoExitPanel.add(radioAutoExitTrue);
        radioAutoExitPanel.add(radioAutoExitFalse);

        radioShadowPanel = new JPanel(new FlowLayout());
        radioShadowPanel.add(radioHasShadowTrue);
        radioShadowPanel.add(radioHasShadowFalse);
        
        radioFocusingPanel = new JPanel(new FlowLayout());
        radioFocusingPanel.add(radioFocusingTrue);
        radioFocusingPanel.add(radioFocusingFalse);
        
        shadowPanel = new JPanel(new BorderLayout());
        shadowPanel.add(new JLabel(" Use drop shadow on main window: "), BorderLayout.WEST);
        shadowPanel.add(radioShadowPanel, BorderLayout.EAST);
        
        focusingPanel = new JPanel(new BorderLayout());
        focusingPanel.add(new JLabel(" Dim the games that are not focused: "), BorderLayout.WEST);
        focusingPanel.add(radioFocusingPanel, BorderLayout.EAST);

        gameAutoExitPanel = new JPanel(new BorderLayout());
        gameAutoExitPanel.add(new JLabel(" Exit GameOrganizer after a game is launched: "), BorderLayout.WEST);
        gameAutoExitPanel.add(radioAutoExitPanel, BorderLayout.EAST);

        middlePanel = new JPanel(new GridLayout(4, 1));
        middlePanel.add(shadowPanel);
        middlePanel.add(focusingPanel);
        middlePanel.add(gameAutoExitPanel);
        middlePanel.add(titleSettingsPanel);

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
        bottomPanel.add(disableBorderButton);
        bottomPanel.add(changeSpacingOption);
        bottomPanel.add(revertDefaultsButton);

        // Add JPanel
        add(completeSettingsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Set JDialog parameters
        setSize(440, 850);
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
        borderColorChooser.setColor(Color.TRANSLUCENT);
        mainFrame.setBorderAndSize(false, Color.WHITE);
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
        radioHasShadowTrue.setSelected(true);
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
    private void doEnableFocusing () {
        mainFrame.setFocusing(true);
        mainFrame.redrawGameGridPanel(mainFrame.getGameLabels());
    }
    
    // Disable focusing
    private void doDisableFocusing () {
        mainFrame.setFocusing(false);
        mainFrame.redrawGameGridPanel(mainFrame.getGameLabels());
    }
            
    // Fields
    private JPanel centerPanel, bottomPanel, titleSettingsPanel, completeSettingsPanel, gameAutoExitPanel, shadowPanel, middlePanel, focusingPanel, radioAutoExitPanel, radioShadowPanel, radioFocusingPanel;
    private JColorChooser barColorChooser, buttonColorChooser, borderColorChooser, backgroundColorChooser, shadowColorChooser;
    private JButton disableBorderButton, revertDefaultsButton, changeSpacingOption, changeTitleButton;
    private JRadioButton radioAutoExitTrue, radioAutoExitFalse, radioHasShadowTrue, radioHasShadowFalse, radioFocusingTrue, radioFocusingFalse;
    private JTextField titleField;
    private MainFrame mainFrame;
}
