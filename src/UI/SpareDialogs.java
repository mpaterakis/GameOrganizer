/*
 * mpaterakis, 2018
 */
package UI;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Includes functions for smaller and less important dialogs that are used by MainFrame.
 *
 * @author mpaterakis
 */
public class SpareDialogs {

    /**
     * Creates a dialog for inserting a game name.
     *
     * @param mainFrame MainFrame object where this method was called from
     * @param fileName A Game's filename
     */
    public static void createGameNameDialog(MainFrame mainFrame, String fileName) {

        // JPanels
        JDialog gameNameDialog = new JDialog(mainFrame, "Game Title");
        JPanel inputPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JPanel dialogPanel = new JPanel(new BorderLayout());

        // JTextField
        JTextField tf = new JTextField(25);
        tf.setText(fileName);

        // JButtons
        JButton btn = new JButton("OK");
        btn.setBackground(new Color(209, 209, 209));

        // JLabel
        JLabel label = new JLabel("Enter Game Title: ");

        // ActionListener for the JButton and JTextField
        ActionListener action = (ActionEvent e) -> {
            mainFrame.setAddedGameName(tf.getText());
            gameNameDialog.dispose();
        };

        // Add ActionListener
        tf.addActionListener(action);
        btn.addActionListener(action);

        // Set up panels
        inputPanel.add(label);
        inputPanel.add(tf);
        buttonPanel.add(btn);
        dialogPanel.add(inputPanel, BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
        gameNameDialog.add(dialogPanel);
        gameNameDialog.pack();
        gameNameDialog.setResizable(false);
        gameNameDialog.setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - gameNameDialog.getBounds().width / 2, (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2);
        gameNameDialog.setModal(true);
        gameNameDialog.setVisible(true);
    }

    /**
     * Creates a dialog for picking a game icon.
     *
     * @return String containing the picked icon's path
     */
    public static String createGameIconPicker(String gameLocation, String gameName) {

        // JButtons
        JButton sgdbButton = new JButton("Search for image on SteamGridDB");
        sgdbButton.addActionListener(e -> {
            try {
                java.awt.Desktop.getDesktop().browse(java.net.URI.create("https://www.steamgriddb.com/search/" + java.net.URLEncoder.encode(gameName, "UTF-8").replace("+", "%20")));
            } catch (IOException ex) {
                Logger.getLogger(SpareDialogs.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        // JLabels
        JLabel sgdbLabel = new JLabel("", SwingConstants.CENTER);
        try {
            InputStream stream = SpareDialogs.class.getResourceAsStream("/Files/sgdb-logo.png");
            sgdbLabel.setIcon(new ImageIcon(ImageIO.read(stream)));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "IconImage Error: Cannot load statusbar icon", "Icon Error", JOptionPane.ERROR_MESSAGE);
        }

        // JPanels
        JPanel sgdbPanel = new JPanel(new BorderLayout());
        sgdbPanel.add(sgdbButton, BorderLayout.SOUTH);
        sgdbPanel.add(sgdbLabel, BorderLayout.CENTER);

        // Set up JFileChooser
        JFileChooser chooser = new JFileChooser(gameLocation);
        chooser.setAccessory(sgdbPanel);
        chooser.setDialogTitle("Choose Game Tile Image");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "tif", "bmp"));

        // Change buttons' color
        changeFileChooserButtonColor(chooser);

        chooser.showOpenDialog(null);
        return chooser.getSelectedFile().getAbsolutePath();
    }

    /**
     * Creates a dialog for picking a game's path.
     *
     * @return String containing the picked game's path
     */
    public static String createGameExePicker() {

        // Set up JFileChooser
        String userhome = System.getProperty("user.home");
        JFileChooser chooser = new JFileChooser(userhome);
        chooser.setDialogTitle("Choose Game Exe");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Change buttons' color
        changeFileChooserButtonColor(chooser);

        chooser.showOpenDialog(null);
        return chooser.getSelectedFile().getAbsolutePath();
    }

    /**
     * Changes a JFileChooser's buttons to a custom color.
     *
     * @param chooser JFileChooser object to be edited
     */
    private static void changeFileChooserButtonColor(JFileChooser chooser) {

        // This spaghetti is setting the buttons colors to "Color(209, 209, 209)" instead
        // of Java's default puke-blue
        //
        // Explanation: It gets all the JButtons -> of the JPanels -> of the JPanels -> of chooser
        for (Component child : chooser.getComponents()) {
            if (child instanceof JPanel) {
                JPanel jp = (JPanel) child;
                for (Component child2 : jp.getComponents()) {
                    if (child2 instanceof JPanel) {
                        JPanel jp2 = (JPanel) child2;
                        for (Component child3 : jp2.getComponents()) {
                            if (child3 instanceof JButton) {
                                JButton b = (JButton) child3;
                                if (b.getText() == "Open" || b.getText() == "Cancel") {
                                    b.setBackground(new Color(209, 209, 209));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates a dialog for picking a color.
     *
     * @param picker JColorChooser to be shown
     */
    public static void createColorPicker(JColorChooser picker) {

        // Set up JPanel
        JDialog colorPickerDialog = new JDialog();
        JPanel mainPanel = new JPanel();
        mainPanel.add(picker);
        colorPickerDialog.setTitle(picker.getName());

        colorPickerDialog.add(picker);
        colorPickerDialog.setModal(true);
        colorPickerDialog.pack();
        colorPickerDialog.setLocationRelativeTo(null);
        colorPickerDialog.setVisible(true);
    }
}
