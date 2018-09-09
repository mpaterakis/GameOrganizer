/*
 * mpaterakis, 2018
 */
package UI;

import GameOrganizer.Game;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * This class creates the settings dialog that is created for each game on right click
 *
 * @author mpaterakis
 */
public class GameSettingsDialog extends JDialog {

    // Constructor
    public GameSettingsDialog(Game game, MainFrame mainFrame) {
        this.game = originalGame = game;
        this.mainFrame = mainFrame;
        this.originalGameLabels = new ArrayList<>(mainFrame.getGameLabels());
        initComponents();
    }

    // Initialize Components
    private void initComponents() {

        // JLabels
        nameLabel = new JLabel(" Name: ");
        pathLabel = new JLabel(" Path: ");
        iconLabel = new JLabel(" Icon: ");
        orderLabel = new JLabel(" Order: ");

        // JTextFields
        nameField = new JTextField(game.getGameName());
        pathField = new JTextField(game.getGamePath());
        iconField = new JTextField(game.getGameIcon().toString());
        orderField = new JTextField(Integer.toString(getGameOrderNumber()));
        orderField.setHorizontalAlignment((int) JTextField.CENTER_ALIGNMENT);
        orderField.setEditable(false);

        // JButtons
        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(209, 209, 209));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doCancel();
            }
        });

        okButton = new JButton("OK");
        okButton.setBackground(new Color(209, 209, 209));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doOk();
            }
        });

        choosePathButton = new JButton("Change Exe");
        choosePathButton.setBackground(new Color(209, 209, 209));
        choosePathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doChoosePath();
            }
        });

        chooseIconButton = new JButton("Change Icon");
        chooseIconButton.setBackground(new Color(209, 209, 209));
        chooseIconButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doChooseIcon();
            }
        });

        openDirButton = new JButton("Open Folder");
        openDirButton.setBackground(new Color(209, 209, 209));
        openDirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doOpenDir();
            }
        });

        removeGameButton = new JButton("Remove Game");
        removeGameButton.setBackground(new Color(209, 209, 209));
        removeGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doRemoveGame();
            }
        });

        orderMinusButton = new JButton("-");
        orderMinusButton.setBackground(new Color(209, 209, 209));
        orderMinusButton.setFocusPainted(false);
        if (Integer.valueOf(orderField.getText()) == 1) {
            orderMinusButton.setEnabled(false);
        }
        orderMinusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doOrderMinus();
            }
        });

        orderPlusButton = new JButton("+");
        orderPlusButton.setBackground(new Color(209, 209, 209));
        orderPlusButton.setFocusPainted(false);
        if (Integer.valueOf(orderField.getText()) == mainFrame.getGameLabels().size()) {
            orderPlusButton.setEnabled(false);
        }
        orderPlusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doOrderPlus();
            }
        });

        // JPanels
        mainPanel = new JPanel(new BorderLayout());
        centerPanel = new JPanel(new GridLayout(2, 1));
        labelsPanel = new JPanel(new GridLayout(4, 1));
        chooserButtonsPanel = new JPanel(new GridLayout(2, 1));
        chooserFieldsPanel = new JPanel(new GridLayout(2, 1));
        filechoosersPanel = new JPanel(new BorderLayout());
        buttonsPanel = new JPanel(new FlowLayout());
        orderPanel = new JPanel(new BorderLayout());
        orderAndNamePanel = new JPanel(new GridLayout(2, 1));

        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(openDirButton);
        buttonsPanel.add(removeGameButton);

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

    // Custom functions
    // Return game's order number in gameGridPanel
    private int getGameOrderNumber() {
        for (int i = 0; i < mainFrame.getGameLabels().size(); i++) {
            if (mainFrame.getGameLabels().get(i).getGame() == game) {
                return i + 1;
            }
        }
        return -1;
    }

    // Reorder gameLabels
    private void reorderGameLabels(int oldPosition, int newPosition) {
        // Swap positions
        Collections.swap(mainFrame.getGameLabels(), oldPosition, newPosition);

        // Redraw gameGridPanel
        mainFrame.redrawGameGridPanel(mainFrame.getGameLabels());
    }

    // Close this JDialog
    private void doCancel() {
        mainFrame.redrawGameGridPanel(originalGameLabels);
        dispose();
    }

    // Apply the data from the textfields to the game instance
    private void doOk() {
        game.setGameName(nameField.getText());
        game.setGameIcon(new ImageIcon(iconField.getText().toString()));
        game.setGamePath(pathField.getText());
        dispose();
    }

    // Choose game's path
    private void doChoosePath() {
        pathField.setText(ExtraDialogs.createGameExePicker());
    }

    // Choose icon's path
    private void doChooseIcon() {
        iconField.setText(ExtraDialogs.createGameIconPicker());
    }

    // Open parent folder
    private void doOpenDir() {
        try {
            Desktop.getDesktop().open(new File(game.getGamePath()).getParentFile());
        } catch (IOException ex) {
            Logger.getLogger(GameSettingsDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Remove this game
    private void doRemoveGame() {
        for (int i = 0; i < mainFrame.getGameLabels().size(); i++) {
            // Make sure we got the correct game
            if (mainFrame.getGameLabels().get(i).getGame().getGamePath() == originalGame.getGamePath()
                    && mainFrame.getGameLabels().get(i).getGame().getGameName() == originalGame.getGameName()) {

                // JOptionPane prompt before deletion
                // Creating buttons for JOptionPane
                // Creating "YES" JButton
                JButton yesButton = new JButton("YES");
                yesButton.setBackground(new Color(209, 209, 209));
                yesButton.setFocusPainted(false);
                final int j = i;
                yesButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {

                        // Get gameLabels, remove this instance and set it as the new gameLabels
                        ArrayList<GameLabel> gameLabels = mainFrame.getGameLabels();
                        gameLabels.remove(j);
                        mainFrame.redrawGameGridPanel(gameLabels);

                        // Close the dialogs
                        SwingUtilities.getWindowAncestor(yesButton).dispose();
                        dispose();
                    }
                });

                // Creating "NO" JButton
                JButton noButton = new JButton("NO");
                noButton.setBackground(new Color(209, 209, 209));
                noButton.setFocusPainted(false);
                noButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        SwingUtilities.getWindowAncestor(yesButton).dispose();
                    }
                });
                JButton[] customButtons = {yesButton, noButton};

                // Show JOptionPane with custom buttons
                JOptionPane.showOptionDialog(null, "Are you sure you want to remove " + game.getGameName() + "?",
                        "Removal Confirmation", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
                        customButtons, customButtons[0]);

            }
        }
    }

    // Move game down in games' order
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

    // Move game up in games' order
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
        if (Integer.valueOf(orderField.getText()) == mainFrame.getGameLabels().size()) {
            orderPlusButton.setEnabled(false);
            orderPlusButton.setBackground(Color.LIGHT_GRAY);
        }
    }

    // Fields
    private Game game, originalGame;
    private JButton okButton, choosePathButton, chooseIconButton, cancelButton, removeGameButton, openDirButton, orderPlusButton, orderMinusButton;
    private JLabel nameLabel, pathLabel, iconLabel, orderLabel;
    private JTextField nameField, pathField, iconField, orderField;
    private JPanel mainPanel, centerPanel, buttonsPanel, labelsPanel, chooserFieldsPanel, chooserButtonsPanel, filechoosersPanel, orderPanel, orderAndNamePanel;
    private MainFrame mainFrame;
    private ArrayList<GameLabel> originalGameLabels;
}
