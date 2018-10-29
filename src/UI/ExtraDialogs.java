/*
 * mpaterakis, 2018
 */
package UI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Includes functions for smaller and less important dialogs that are used by MainFrame
 *
 * @author mpaterakis
 */
public class ExtraDialogs {

    // Creates a dialog for inserting a game name
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

    // Creates a dialog for picking a game icon
    public static String createGameIconPicker() {
        
        // Set up JFileChooser
        String userhome = System.getProperty("user.home");
        JFileChooser chooser = new JFileChooser(userhome);
        chooser.setDialogTitle("Choose Game Tile Image");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "tif", "bmp"));

        // Change buttons' color
        changeFileChooserButtonColor(chooser);

        chooser.showOpenDialog(null);
        return chooser.getSelectedFile().getAbsolutePath();
    }

    // Creates a dialog for picking a game icon
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

    // Change the "Open" and "Cancel" buttons' color in JFileChooser to "Color(209,209,209"
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
    
    // Creates a dialog for picking a color
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
//        return picker.getColor();
    }
}
