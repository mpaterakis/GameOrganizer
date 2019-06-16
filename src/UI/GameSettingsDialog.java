/*
 * mpaterakis, 2018
 */
package UI;

import GameOrganizer.Game;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * Creates the GameSettings dialog from which the user can manipulate a Game's values.
 *
 * @author mpaterakis
 */
public class GameSettingsDialog extends JDialog {

    /**
     * Create a new GameSettingsDialog object.
     *
     * @param game Game object whose values will be shown and edited
     * @param mainFrame MainFrame object containing the GameLabel that holds the game
     */
    public GameSettingsDialog(GameLabel gameLabel, MainFrame mainFrame) {
        this.gameLabel = gameLabel;
        this.game = gameLabel.getGame();
        this.mainFrame = mainFrame;
        this.originalGameLabels = new ArrayList<>(mainFrame.getActiveGameLabels());
        initComponents();
    }

    /**
     * Initialize the GameSettingsDialog's components.
     */
    private void initComponents() {

        // JLabels
        nameLabel = new JLabel(" Name: ");
        pathLabel = new JLabel(" Path: ");
        iconLabel = new JLabel(" Icon: ");
        orderLabel = new JLabel(" Order: ");

        // JTextFields
        nameField = new JTextField(game.getGameName());
        pathField = new JTextField(game.getGamePath());
        iconField = new JTextField(game.getGameIconPath());
        orderField = new JTextField(Integer.toString(getGameOrderNumber()));
        orderField.setHorizontalAlignment((int) JTextField.CENTER_ALIGNMENT);
        orderField.setEditable(false);

        // JButtons
        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(190, 190, 190));
        cancelButton.addActionListener(e -> doCancel());

        okButton = new JButton("OK");
        okButton.setBackground(new Color(190, 190, 190));
        okButton.addActionListener(e -> doOk());

        choosePathButton = new JButton("Change Exe");
        choosePathButton.setBackground(new Color(209, 209, 209));
        choosePathButton.addActionListener(e -> doChoosePath());

        chooseIconButton = new JButton("Change Icon");
        chooseIconButton.setBackground(new Color(209, 209, 209));
        chooseIconButton.addActionListener(e -> doChooseIcon());

        openDirButton = new JButton("Open Folder");
        openDirButton.setBackground(new Color(209, 209, 209));
        openDirButton.addActionListener(e -> doOpenDir());

        removeGameButton = new JButton("Remove Game");
        removeGameButton.setBackground(new Color(209, 209, 209));
        removeGameButton.addActionListener(e -> doRemoveGame());

        moveToGameMenuButton = new JButton("Move to Games Menu: ");
        moveToGameMenuButton.setBackground(new Color(209, 209, 209));
        moveToGameMenuButton.addActionListener(e -> doMoveToGameMenu(Integer.valueOf(gameMenuComboBox.getSelectedItem().toString()) - 1));
        if (mainFrame.getActiveGameLabels().size() == 9) {
            moveToGameMenuButton.setEnabled(false);
        }

        orderMinusButton = new JButton("-");
        orderMinusButton.setBackground(new Color(209, 209, 209));
        orderMinusButton.setFocusPainted(false);
        if (Integer.valueOf(orderField.getText()) == 1) {
            orderMinusButton.setEnabled(false);
        }
        orderMinusButton.addActionListener(e -> doOrderMinus());

        orderPlusButton = new JButton("+");
        orderPlusButton.setBackground(new Color(209, 209, 209));
        orderPlusButton.setFocusPainted(false);
        if (Integer.valueOf(orderField.getText()) == mainFrame.getActiveGameLabels().size()) {
            orderPlusButton.setEnabled(false);
        }
        orderPlusButton.addActionListener(e -> doOrderPlus());

        // JComboBox
        gameMenuComboBox = new JComboBox();
        gameMenuComboBox.setBackground(new Color(219, 219, 219));
        for (int i = 0; i < mainFrame.getGameLabelLists().size(); i++) {
            gameMenuComboBox.addItem(i + 1);
            gameMenuComboBox.addItemListener((ie) -> {
                if (mainFrame.getGameLabelLists().get(Integer.valueOf(ie.getItem().toString()) - 1) == mainFrame.getActiveGameLabels()) {
                    moveToGameMenuButton.setEnabled(false);
                } else if (mainFrame.getGameLabelLists().get(Integer.valueOf(ie.getItem().toString()) - 1).size() == 9) {
                    moveToGameMenuButton.setEnabled(false);
                } else {
                    moveToGameMenuButton.setEnabled(true);
                }
            });
            if (mainFrame.getGameLabelLists().get(i) == mainFrame.getActiveGameLabels()) {
                gameMenuComboBox.setSelectedIndex(i);
                moveToGameMenuButton.setEnabled(false);
            }
        }

        // JPanels
        mainPanel = new JPanel(new BorderLayout());
        centerPanel = new JPanel(new GridLayout(2, 1));
        labelsPanel = new JPanel(new GridLayout(4, 1));
        chooserButtonsPanel = new JPanel(new GridLayout(2, 1));
        chooserFieldsPanel = new JPanel(new GridLayout(2, 1));
        filechoosersPanel = new JPanel(new BorderLayout());
        buttonsPanel = new JPanel(new GridLayout(2, 1));
        JPanel buttonsPanelTop = new JPanel(new FlowLayout());
        JPanel buttonsPanelBottom = new JPanel(new FlowLayout());
        orderPanel = new JPanel(new BorderLayout());
        orderAndNamePanel = new JPanel(new GridLayout(2, 1));

        buttonsPanelBottom.add(okButton);
        buttonsPanelBottom.add(cancelButton);

        buttonsPanelTop.add(openDirButton);
        buttonsPanelTop.add(removeGameButton);
        buttonsPanelTop.add(moveToGameMenuButton);
        buttonsPanelTop.add(gameMenuComboBox);

        buttonsPanel.add(buttonsPanelTop);
        buttonsPanel.add(buttonsPanelBottom);

        labelsPanel.add(orderLabel);
        labelsPanel.add(nameLabel);
        labelsPanel.add(pathLabel);
        labelsPanel.add(iconLabel);

        orderPanel.add(orderMinusButton, BorderLayout.WEST);
        orderPanel.add(orderField, BorderLayout.CENTER);
        orderPanel.add(orderPlusButton, BorderLayout.EAST);

        chooserFieldsPanel.add(pathField);
        chooserFieldsPanel.add(iconField);

        chooserButtonsPanel.add(choosePathButton);
        chooserButtonsPanel.add(chooseIconButton);

        filechoosersPanel.add(chooserFieldsPanel, BorderLayout.CENTER);
        filechoosersPanel.add(chooserButtonsPanel, BorderLayout.EAST);

        orderAndNamePanel.add(orderPanel);
        orderAndNamePanel.add(nameField);

        centerPanel.add(orderAndNamePanel);
        centerPanel.add(filechoosersPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        mainPanel.add(labelsPanel, BorderLayout.WEST);
        mainPanel.setBorder(new TitledBorder("Game Settings"));

        add(mainPanel);

        // WindowListener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Cancel all changes on close
                mainFrame.redrawGameGridPanel(originalGameLabels);
            }
        });

        // Set JDialog properties
        pack();
        setTitle(game.getGameName() + " Settings");
        setResizable(false);
        setModal(true);
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - getBounds().width / 2, (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2);
        setVisible(true);
    }

    /**
     * Get the Game's order number (on the MainFrame).
     *
     * @return Integer containing the Game's order number
     */
    private int getGameOrderNumber() {
        for (int i = 0; i < mainFrame.getActiveGameLabels().size(); i++) {
            if (mainFrame.getActiveGameLabels().get(i).getGame() == game) {
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * Reorder the GameLabels on the MainFrame.
     *
     * @param oldPosition Integer containing the old position of a GameLabel
     * @param newPosition Integer containing the new position of a GameLabel
     */
    private void reorderGameLabels(int oldPosition, int newPosition) {
        // Swap positions
        Collections.swap(mainFrame.getActiveGameLabels(), oldPosition, newPosition);

        // Redraw gameGridPanel
        mainFrame.redrawGameGridPanel(mainFrame.getActiveGameLabels());
    }

    /**
     * Close this JDialog.
     */
    private void doCancel() {
        mainFrame.redrawGameGridPanel(originalGameLabels);
        dispose();
    }

    /**
     * Apply the data from the textfields to the game instance
     */
    private void doOk() {
        game.setGameName(nameField.getText());
        game.setGameIconPath(iconField.getText());
        game.setGamePath(pathField.getText());
        dispose();
    }

    /**
     * Choose the Game's path.
     */
    private void doChoosePath() {
        pathField.setText(SpareDialogs.createGameExePicker());
    }

    /**
     * Choose the Game's icon.
     */
    private void doChooseIcon() {
        iconField.setText(SpareDialogs.createGameIconPicker(new File(game.getGamePath()).getParentFile().getAbsolutePath(), game.getGameName()));
    }

    // Open parent folder
    private void doOpenDir() {
        try {
            Desktop.getDesktop().open(new File(game.getGamePath()).getParentFile());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "This file has no parent directory", "No Parent Directory", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Remove this Game.
     */
    private void doRemoveGame() {
        // JOptionPane prompt before deletion
        // Creating buttons for JOptionPane
        // Creating "YES" JButton
        JButton yesButton = new JButton("YES");
        yesButton.setBackground(new Color(209, 209, 209));
        yesButton.setFocusPainted(false);
        yesButton.addActionListener((ActionEvent actionEvent) -> {
            // Get gameLabels, remove this instance and set it as the new gameLabels
            ArrayList<GameLabel> gameLabels = mainFrame.getActiveGameLabels();
            mainFrame.getActiveGameLabels().remove(gameLabel);
            mainFrame.redrawGameGridPanel(gameLabels);

            // If the gameLabels list is empty, remove it
            if (gameLabels.isEmpty()) {
                mainFrame.getGameLabelLists().remove(gameLabels);
            }

            // Close the dialogs
            SwingUtilities.getWindowAncestor(yesButton).dispose();
            dispose();
        });

        // Creating "NO" JButton
        JButton noButton = new JButton("NO");
        noButton.setBackground(new Color(209, 209, 209));
        noButton.setFocusPainted(false);
        noButton.addActionListener(e -> SwingUtilities.getWindowAncestor(yesButton).dispose());
        JButton[] customButtons = {yesButton, noButton};

        // Show JOptionPane with custom buttons
        JOptionPane.showOptionDialog(null, "Are you sure you want to remove " + game.getGameName() + "?",
                "Removal Confirmation", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
                customButtons, customButtons[0]);
    }

    /**
     * Move game down in games' order.
     */
    private void doOrderMinus() {
        // Calculate old and new positions and set the textfield text
        int oldPosition = Integer.valueOf(orderField.getText());
        int newPosition = Integer.valueOf(orderField.getText()) - 1;
        orderField.setText(Integer.toString(newPosition));

        // Reorder gameLabels and redraw gameGridPanel
        reorderGameLabels(oldPosition - 1, newPosition - 1);

        // Enable the plus button
        orderPlusButton.setEnabled(true);
        orderPlusButton.setBackground(new Color(209, 209, 209));

        // Prevent from going out of bounds
        if (Integer.valueOf(orderField.getText()) == 1) {
            orderMinusButton.setEnabled(false);
            orderMinusButton.setBackground(Color.LIGHT_GRAY);
        }
    }

    /**
     * Move game up in games' order.
     */
    private void doOrderPlus() {
        // Calculate old and new positions and set the textfield text
        int oldPosition = Integer.valueOf(orderField.getText());
        int newPosition = Integer.valueOf(orderField.getText()) + 1;
        orderField.setText(Integer.toString(newPosition));

        // Reorder gameLabels and redraw gameGridPanel
        reorderGameLabels(oldPosition - 1, newPosition - 1);

        // Enable the plus button
        orderMinusButton.setEnabled(true);
        orderMinusButton.setBackground(new Color(209, 209, 209));

        // Prevent from going out of bounds
        if (Integer.valueOf(orderField.getText()) == mainFrame.getActiveGameLabels().size()) {
            orderPlusButton.setEnabled(false);
            orderPlusButton.setBackground(Color.LIGHT_GRAY);
        }
    }

    /**
     * Move game to different game menu.
     */
    private void doMoveToGameMenu(int newMenuIndex) {
        if (mainFrame.getGameLabelLists().get(newMenuIndex).size() < 9) {
            // JOptionPane prompt before deletion
            // Creating buttons for JOptionPane
            // Creating "YES" JButton
            JButton yesButton = new JButton("YES");
            yesButton.setBackground(new Color(209, 209, 209));
            yesButton.setFocusPainted(false);
            yesButton.addActionListener((ActionEvent actionEvent) -> {
                // Get gameLabels, remove this instance and move it to the new gameLabels list
                mainFrame.getActiveGameLabels().remove(gameLabel);
                mainFrame.getGameLabelLists().get(newMenuIndex).add(gameLabel);

                // If the gameLabels list is empty, remove it
                if (mainFrame.getActiveGameLabels().isEmpty()) {
                    mainFrame.getGameLabelLists().remove(mainFrame.getActiveGameLabels());
                    mainFrame.goToGameMenu(newMenuIndex);
                }
                
                // Reload GameLabels
                mainFrame.redrawGameGridPanel(mainFrame.getActiveGameLabels());
                
                // Close the dialogs
                SwingUtilities.getWindowAncestor(yesButton).dispose();
                dispose();
            });

            // Creating "NO" JButton
            JButton noButton = new JButton("NO");
            noButton.setBackground(new Color(209, 209, 209));
            noButton.setFocusPainted(false);
            noButton.addActionListener(e -> SwingUtilities.getWindowAncestor(yesButton).dispose());
            JButton[] customButtons = {yesButton, noButton};

            // Show JOptionPane with custom buttons
            JOptionPane.showOptionDialog(null, "Are you sure you want to move " + game.getGameName() + " to Secret Games menu?",
                    "Moving Confirmation", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
                    customButtons, customButtons[0]);
        }
    }

    // Fields
    private Game game;
    private GameLabel gameLabel;
    private JButton okButton, choosePathButton, chooseIconButton, cancelButton, removeGameButton, openDirButton, orderPlusButton, orderMinusButton, moveToGameMenuButton;
    private JLabel nameLabel, pathLabel, iconLabel, orderLabel;
    private JTextField nameField, pathField, iconField, orderField;
    private JPanel mainPanel, centerPanel, buttonsPanel, labelsPanel, chooserFieldsPanel, chooserButtonsPanel, filechoosersPanel, orderPanel, orderAndNamePanel;
    private JComboBox gameMenuComboBox;
    private MainFrame mainFrame;
    private ArrayList<GameLabel> originalGameLabels;
}
