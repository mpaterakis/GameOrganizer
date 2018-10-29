/*
 * mpaterakis, 2018
 */ 
package DataManagement;

import GameOrganizer.Game;
import UI.*;
import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * This class includes all the functions required to save and load from the XML file
 *
 * @author mpaterakis
 */
public class ProcessXML {

    // Writes the current data to the xml
    public static void WriteXML(MainFrame mainFrame) {
        try {
            // Build XML
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // Root element
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("GameOrganizerData");
            doc.appendChild(rootElement);

            // Game list
            Element subElement = doc.createElement("GameList");
            rootElement.appendChild(subElement);

            // Games list
            for (int i = 0; i < mainFrame.getGameLabels().size(); i++) {
                // Game elements
                Element game = doc.createElement("Game");
                subElement.appendChild(game);

                // Set attribute to staff element
                Attr attr = doc.createAttribute("id");
                attr.setValue(Integer.toString(i + 1));
                game.setAttributeNode(attr);

                // Name elements
                Element gamename = doc.createElement("Name");
                gamename.appendChild(doc.createTextNode(mainFrame.getGameLabels().get(i).getGame().getGameName()));
                game.appendChild(gamename);

                // Icon elements
                Element gameicon = doc.createElement("Icon");
                gameicon.appendChild(doc.createTextNode(mainFrame.getGameLabels().get(i).getGame().getGameIconPath()));
                game.appendChild(gameicon);

                // Exe locaiton elements
                Element gamepath = doc.createElement("Path");
                gamepath.appendChild(doc.createTextNode(mainFrame.getGameLabels().get(i).getGame().getGamePath()));
                game.appendChild(gamepath);
            }

            // Window properties
            Element subElement2 = doc.createElement("WindowProperties");
            rootElement.appendChild(subElement2);

            // Bar color
            Element barClr = doc.createElement("BarColor");
            barClr.appendChild(doc.createTextNode(Integer.toString(mainFrame.getBarColor().getRGB())));
            subElement2.appendChild(barClr);

            // Buttons Color
            Element btnsClr = doc.createElement("ButtonsColor");
            btnsClr.appendChild(doc.createTextNode(Integer.toString(mainFrame.getButtonColor().getRGB())));
            subElement2.appendChild(btnsClr);

            // Border Color
            Element brdrClr = doc.createElement("BorderColor");
            brdrClr.appendChild(doc.createTextNode(Integer.toString(mainFrame.getBorderColor().getRGB())));
            subElement2.appendChild(brdrClr);

            // Background Color
            Element bgClr = doc.createElement("BackgroundColor");
            bgClr.appendChild(doc.createTextNode(Integer.toString(mainFrame.getBackgroundColor().getRGB())));
            subElement2.appendChild(bgClr);

            // Shadow Color
            Element shdClr = doc.createElement("ShadowColor");
            shdClr.appendChild(doc.createTextNode(Integer.toString(mainFrame.getShadowColor().getRGB())));
            subElement2.appendChild(shdClr);

            // HasBorder bool
            Element hasBrdr = doc.createElement("HasBorder");
            if (mainFrame.hasBorder()) {
                hasBrdr.appendChild(doc.createTextNode("true"));
            } else {
                hasBrdr.appendChild(doc.createTextNode("false"));
            }
            subElement2.appendChild(hasBrdr);

            // HasSpace bool
            Element hasSpc = doc.createElement("HasSpace");
            if (mainFrame.hasSpace()) {
                hasSpc.appendChild(doc.createTextNode("true"));
            } else {
                hasSpc.appendChild(doc.createTextNode("false"));
            }
            subElement2.appendChild(hasSpc);
            
            // AutoExit bool
            Element autoExit = doc.createElement("AutoExit");
            if (mainFrame.getAutoExit()) {
                autoExit.appendChild(doc.createTextNode("true"));
            } else {
                autoExit.appendChild(doc.createTextNode("false"));
            }
            subElement2.appendChild(autoExit);
            
            // HasShadow bool
            Element hasShadow = doc.createElement("HasShadow");
            if (mainFrame.hasShadow()) {
                hasShadow.appendChild(doc.createTextNode("true"));
            } else {
                hasShadow.appendChild(doc.createTextNode("false"));
            }
            subElement2.appendChild(hasShadow);
            
            // Focusing bool
            Element focusing = doc.createElement("Focusing");
            if (mainFrame.hasFocusing()) {
                focusing.appendChild(doc.createTextNode("true"));
            } else {
                focusing.appendChild(doc.createTextNode("false"));
            }
            subElement2.appendChild(focusing);
            
            // Window Title text
            Element windowTitle = doc.createElement("WindowTitle");
            windowTitle.appendChild(doc.createTextNode(mainFrame.getTitleText()));
            subElement2.appendChild(windowTitle);
            
            // Window scale double
            Element frameScale = doc.createElement("FrameScale");
            frameScale.appendChild(doc.createTextNode(String.valueOf(mainFrame.getFrameScale())));
            subElement2.appendChild(frameScale);
            
            // Window position X
            Element windowPosX = doc.createElement("WindowPositionX");
            windowPosX.appendChild(doc.createTextNode(String.valueOf(mainFrame.getLocation().getX())));
            subElement2.appendChild(windowPosX);
            
            // Window position Y
            Element windowPosY = doc.createElement("WindowPositionY");
            windowPosY.appendChild(doc.createTextNode(String.valueOf(mainFrame.getLocation().getY())));
            subElement2.appendChild(windowPosY);

            // Write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(System.getProperty("user.home") + "\\GameOrganizerData.xml"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);

            System.out.println("XML saved successfully");

            // Catch exceptions
        } catch (ParserConfigurationException pce) {
            JOptionPane.showMessageDialog(null, "XML Error: " + pce.getMessage(), "XML Error", JOptionPane.ERROR_MESSAGE);
        } catch (TransformerException tfe) {
            JOptionPane.showMessageDialog(null, "XML Error: " + tfe.getMessage(), "XML Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "XML Error: " + ex.getMessage(), "XML Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Loads the data from the saved xml
    public static void LoadXML(MainFrame mainFrame) {
        try {
            // Load file
            File file = new File(System.getProperty("user.home") + "\\GameOrganizerData.xml");
            if (file.exists()) {
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);

                // Get data from file
                String barClr = document.getElementsByTagName("BarColor").item(0).getTextContent();
                String btnsClr = document.getElementsByTagName("ButtonsColor").item(0).getTextContent();
                String brdrClr = document.getElementsByTagName("BorderColor").item(0).getTextContent();
                String bgClr = document.getElementsByTagName("BackgroundColor").item(0).getTextContent();
                String shdClr = document.getElementsByTagName("ShadowColor").item(0).getTextContent();
                String hasBrdr = document.getElementsByTagName("HasBorder").item(0).getTextContent();
                String hasSpc = document.getElementsByTagName("HasSpace").item(0).getTextContent();
                String hasShadow = document.getElementsByTagName("HasShadow").item(0).getTextContent();
                String autoExit = document.getElementsByTagName("AutoExit").item(0).getTextContent();
                String focusing = document.getElementsByTagName("Focusing").item(0).getTextContent();
                String windowTitle = document.getElementsByTagName("WindowTitle").item(0).getTextContent();
                double windowPosX = Double.valueOf(document.getElementsByTagName("WindowPositionX").item(0).getTextContent());
                double windowPosY = Double.valueOf(document.getElementsByTagName("WindowPositionY").item(0).getTextContent());
                double frameScale = Double.valueOf(document.getElementsByTagName("FrameScale").item(0).getTextContent());
                NodeList gamesList = document.getElementsByTagName("Game");

                // Load game data
                ArrayList<GameLabel> gameLabels = new ArrayList<>();
                for (int i = 0; i < gamesList.getLength(); i++) {
                    // Set data to Strings and create GameLabels
                    String name = ((Element) gamesList.item(i)).getElementsByTagName("Name").item(0).getTextContent();
                    String icon = ((Element) gamesList.item(i)).getElementsByTagName("Icon").item(0).getTextContent();
                    String path = ((Element) gamesList.item(i)).getElementsByTagName("Path").item(0).getTextContent();
                    GameLabel gameLabel = new GameLabel(new Game(icon, path, name, frameScale), mainFrame);
                    gameLabels.add(gameLabel);
                }
                
                // Draw the gameGridPanel with the new GameLabels
                mainFrame.redrawGameGridPanel(gameLabels);

                if (hasSpc.equalsIgnoreCase("true")) {
                    mainFrame.setHasSpace(true);
                } else if (hasBrdr.equalsIgnoreCase("false")) {
                    mainFrame.setHasSpace(false);
                }

                if (hasBrdr.equalsIgnoreCase("true")) {
                    mainFrame.setHasBorder(true);
                } else if (hasBrdr.equalsIgnoreCase("false")) {
                    mainFrame.setHasBorder(false);
                }
                
                if (hasShadow.equalsIgnoreCase("true")) {
                    mainFrame.setHasShadow(true);
                } else if (hasShadow.equalsIgnoreCase("false")) {
                    mainFrame.setHasShadow(false);
                }
                
                if (autoExit.equalsIgnoreCase("true")) {
                    mainFrame.setAutoExit(true);
                } else if (autoExit.equalsIgnoreCase("false")) {
                    mainFrame.setAutoExit(false);
                }
                
                if (focusing.equalsIgnoreCase("true")) {
                    mainFrame.setFocusing(true);
                } else if (focusing.equalsIgnoreCase("false")) {
                    mainFrame.setFocusing(false);
                }

                // Apply data to program
                mainFrame.setBarColor(new Color(Integer.parseInt(barClr)));
                mainFrame.setButtonColor(new Color(Integer.parseInt(btnsClr)));
                mainFrame.setBorderColor(new Color(Integer.parseInt(brdrClr)));
                mainFrame.setBackgroundColor(new Color(Integer.parseInt(bgClr)));
                mainFrame.setShadowColor(new Color(Integer.parseInt(shdClr)));
                mainFrame.setTitleText(windowTitle);                
                mainFrame.setFrameScale(frameScale);
                mainFrame.setLocation((int) windowPosX, (int) windowPosY);
                
            } else {
                // If the XML file does not exist, simply draw the gameGridPanel
                mainFrame.redrawGameGridPanel(new ArrayList<>());
                mainFrame.setBorderAndSize(true, mainFrame.getBorderColor());
                mainFrame.setLocationRelativeTo(null);
            }

            // Catch exceptions
        } catch (SAXException ex) {
            JOptionPane.showMessageDialog(null, "XML Error: " + ex.getMessage(), "XML Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "XML Error: " + ex.getMessage(), "XML Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } catch (ParserConfigurationException ex) {
            JOptionPane.showMessageDialog(null, "XML Error: " + ex.getMessage(), "XML Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } catch (NumberFormatException | DOMException ex) {
            JOptionPane.showMessageDialog(null, "XML Error: " + ex.getMessage(), "XML Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "XML Error, program settings will reset" , "XML Error", JOptionPane.ERROR_MESSAGE);
            File file = new File(System.getProperty("user.home") + "\\GameOrganizerData.xml");
            file.delete();
            mainFrame.redrawGameGridPanel(mainFrame.getGameLabels());
        }
    }
}
