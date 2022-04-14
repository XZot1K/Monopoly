/*
    Jeremiah Osborne
    Date: 4/1/2022
 */

package game.components.property;

import game.Game;
import game.components.gui.board.Board;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class PropertyCard extends JPanel {

    public final PropertyCard CARD_INSTANCE; // instance of the card
    private final Property property; // property associated with
    private final Board.Quadrant quadrant; // quadrant apart of

    public PropertyCard(Property property, Board.Quadrant quadrant) {

        // initialize variables
        this.CARD_INSTANCE = this;
        this.property = property;
        this.quadrant = quadrant;

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (getProperty().isPurchasable()) {
                    // check if the selected property is null or not the same as this card
                    if (Game.INSTANCE.getBoard().getSelectedProperty() == null) {
                        Game.INSTANCE.getBoard().setSelectedProperty(CARD_INSTANCE);
                        Game.INSTANCE.getBoard().repaint(); // repaint the board
                    } else if (Game.INSTANCE.getBoard().getSelectedProperty() == CARD_INSTANCE) {
                        Game.INSTANCE.getBoard().setSelectedProperty(null); // reset selected property
                        Game.INSTANCE.getBoard().repaint(); // repaint the board
                    } else if (Game.INSTANCE.getBoard().getSelectedProperty() != CARD_INSTANCE) {
                        Game.INSTANCE.getBoard().setSelectedProperty(CARD_INSTANCE); // set new selected property
                        Game.INSTANCE.getBoard().repaint(); // repaint the board
                    }
                }

            }

        });

        setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2 = (Graphics2D) g; // cast to 2d graphics
        final AffineTransform original = (AffineTransform) g2.getTransform().clone(); // original transform

        // color the panel size to white
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (getProperty().isCorner()) { // if the property is a corner piece
            try {
                switch (getProperty().getName()) { // switch by property name

                    case "GO": { // pass go (bottom left)
                        final URL url = Game.class.getResource("/resources/corners/go.png"); // load the image
                        if (url != null) {
                            // scale and draw it to the panel /w observer as the panel
                            g2.scale(0.9, 0.9);
                            g2.drawImage(ImageIO.read(url), 13, 7, getWidth(), getHeight(), this);
                            g2.setTransform(original);
                        }

                        break;
                    }

                    case "IN JAIL/JUST VISITING": { // jail/just visiting (top left)
                        final URL url = Game.class.getResource("/resources/corners/jail.png");
                        if (url != null) {
                            // draw it to the panel /w observer as the panel
                            g2.drawImage(ImageIO.read(url), 0, 0, getWidth(), getHeight(), this);
                        }
                        break;
                    }

                    case "FREE PARKING": { // free parking (top right)
                        final URL url = Game.class.getResource("/resources/corners/parking.png");
                        if (url != null) {
                            // draw it to the panel /w observer as the panel
                            g2.drawImage(ImageIO.read(url), 0, 0, getWidth(), getHeight(), this);
                        }
                        break;
                    }

                    case "GO TO JAIL": { // go to jail (bottom right)
                        final URL url = Game.class.getResource("/resources/corners/go_to_jail.png");
                        if (url != null) {
                            // draw it to the panel /w observer as the panel
                            g2.drawImage(ImageIO.read(url), 0, 0, getWidth(), getHeight(), this);
                        }
                        break;
                    }
                }
            } catch (IOException e) {e.printStackTrace();} // print stacktrace since the files will all exist no error should occur
        } else {
            if (quadrant == Board.Quadrant.LEFT) { // left side board
                final int portion = (int) (getWidth() * 0.25), x = (getWidth() - portion);

                // new transform to rotate 90 degrees
                final AffineTransform affineTransform = new AffineTransform();
                affineTransform.rotate(Math.toRadians(90), 0, 0);

                // new font
                final Font font = new Font("Arial Black", Font.BOLD, 14);
                g2.setFont(font.deriveFont(affineTransform));
                g2.setColor(Color.BLACK);

                if (getProperty().getGroup() == Property.Group.STATIONS) { // is railroad station
                    try {
                        final URL url = Game.class.getResource("/resources/stations/station_left.png"); // load left-side image
                        if (url != null) {
                            // scale it down some and draw it to the panel
                            g2.scale(0.85, 1);
                            g2.drawImage(ImageIO.read(url), 0, 0, getWidth(), getHeight(), this);
                            g2.setTransform(original);
                        }
                    } catch (IOException e) {e.printStackTrace();} // print stack trace

                    if (property.getName().contains(" ")) { // has spaces
                        final String[] args = property.getName().split(" "); // separate by spaces
                        StringBuilder sb = new StringBuilder();
                        for (String word : args) sb.append(word.charAt(0)); // first letter of each word
                        g2.drawString(sb.toString(), (int) ((getWidth() / 2) + (getWidth() * 0.35)), (int) (getHeight() - (getHeight() * 0.6))); // draw string to panel
                    }
                } else if (getProperty().getName().equals("CHANCE")) { // is chance
                    try {
                        final URL url = Game.class.getResource("/resources/chance/q-purple.png"); // load left-side image
                        if (url != null) {
                            // scale it down some and draw it to the panel
                            g2.scale(0.65, 0.45);
                            g2.drawImage(ImageIO.read(url), (int) (getWidth() * 0.1), (int) (getHeight() * 0.6), getWidth(), getHeight(), this);
                            g2.setTransform(original);
                        }
                    } catch (IOException e) {e.printStackTrace();}

                    // draw the property name "chance" on the panel
                    g2.drawString(getProperty().getName(), (int) ((getWidth() / 2) + (getWidth() * 0.33)), (int) (getHeight() * 0.08));

                } else if (getProperty().getName().equals("COMMUNITY CHEST")) { // is community chest
                    try {
                        final URL url = Game.class.getResource("/resources/chest/chest_left.png"); // load left-side image
                        if (url != null) {
                            // scale it down some and draw it to the panel
                            g2.scale(0.65, 0.8);
                            g2.drawImage(ImageIO.read(url), 0, (int) (getHeight() * 0.1), getWidth(), getHeight(), this);
                            g2.setTransform(original);
                        }
                    } catch (IOException e) {e.printStackTrace();}

                    final Font newFont = new Font("Arial Black", Font.BOLD, 10);
                    g2.setFont(newFont.deriveFont(affineTransform));

                    // draw the property name "Community Chest" on the panel
                    int tempX = (int) ((getWidth() / 2) + (getWidth() * 0.35)),
                            tempY = (int) ((getHeight() / 2) * 0.1);

                    for (String word : getProperty().getName().split(" ")) {
                        g2.drawString(word, tempX, tempY);
                        tempX -= (getWidth() * 0.1);
                        tempY += (getHeight() * 0.2);
                    }

                    g2.setFont(font.deriveFont(affineTransform));

                } else if (getProperty().getName().equals("INCOME TAX")) { // is community chest
                    try {
                        final URL url = Game.class.getResource("/resources/tax/income_tax.png"); // load left-side image
                        if (url != null) {
                            // scale it down some and draw it to the panel
                            g2.scale(1.4, 1.4);
                            g2.drawImage(ImageIO.read(url), (int) (-getWidth() * 0.12), (int) (-getHeight() * 0.18), getWidth(), getHeight(), this);
                            g2.setTransform(original);
                        }
                    } catch (IOException e) {e.printStackTrace();}
                } else { // normal card
                    // draw group color rectangle at the top of the card
                    g2.setColor(getProperty().getGroup().getColor());
                    g2.fillRect(x, 0, portion, getHeight());

                    // draw a black border around the colored rectangle
                    g2.setColor(Color.BLACK);
                    g2.drawRect(x, 0, portion, getHeight());

                    if (property.getName().contains(" ")) { // name has spaces
                        final String[] args = property.getName().split(" "); // separate the name by spaces
                        StringBuilder sb = new StringBuilder();
                        for (String word : args) sb.append(word.charAt(0)); // add the first letter of each word to the string builder
                        g2.drawString(sb.toString(), getWidth() / 2, (int) ((getHeight() / 2) - (getHeight() * 0.13))); // draw the string
                    }
                }


            } else if (quadrant == Board.Quadrant.RIGHT) { // right side board

                // new transform to rotate 270 degrees
                final AffineTransform affineTransform = new AffineTransform();
                affineTransform.rotate(Math.toRadians(270), 0, 0);

                // new font
                final Font font = new Font("Arial Black", Font.BOLD, 14);
                g2.setFont(font.deriveFont(affineTransform));
                g2.setColor(Color.BLACK);

                if (getProperty().getGroup() == Property.Group.STATIONS) { // is railroad station
                    try {
                        final URL url = Game.class.getResource("/resources/stations/station_right.png"); // load the right-side image
                        if (url != null) {
                            // scale it down some and draw it to the panel
                            g2.scale(0.85, 1);
                            g2.drawImage(ImageIO.read(url), (int) (getWidth() * 0.18), 0, getWidth(), getHeight(), this);
                            g2.setTransform(original); // reset transform back to original
                        }
                    } catch (IOException e) {e.printStackTrace();}

                    if (property.getName().contains(" ")) { // name has spaces
                        final String[] args = property.getName().split(" "); // separate the name by spaces
                        StringBuilder sb = new StringBuilder();
                        for (String word : args) sb.append(word.charAt(0)); // add the first letter of each word to the string builder
                        g2.drawString(sb.toString(), (int) ((getWidth() * 0.17)), (int) (getHeight() - (getHeight() * 0.2))); // draw the string
                    }
                } else if (getProperty().getName().equals("WATER WORKS")) { // is railroad station
                    try {
                        final URL url = Game.class.getResource("/resources/utilities/ww.png"); // load bottom image
                        if (url != null) {
                            // scale it down some and draw it to the panel
                            // g2.scale(1, 0.78);
                            g2.drawImage(ImageIO.read(url), 0, 0, getWidth(), getHeight(), this);
                            g2.setTransform(original);
                        }
                    } catch (IOException e) {e.printStackTrace();} // print stack trace
                } else if (getProperty().getName().equals("CHANCE")) { // is chance
                    try {
                        final URL url = Game.class.getResource("/resources/chance/q-blue.png"); // load left-side image
                        if (url != null) {
                            // scale it down some and draw it to the panel
                            g2.scale(0.65, 0.45);
                            g2.drawImage(ImageIO.read(url), (int) (getWidth() * 0.43), (int) (getHeight() * 0.6), getWidth(), getHeight(), this);
                            g2.setTransform(original);
                        }
                    } catch (IOException e) {e.printStackTrace();}

                    // draw the property name "chance" on the panel
                    g2.drawString(getProperty().getName(), (int) (getWidth() * 0.2), (int) (getHeight() * 0.93));

                } else {
                    final int portion = (int) (getWidth() * 0.25), x = 0;

                    // draw group color rectangle at the top of the card
                    g2.setColor(getProperty().getGroup().getColor());
                    g2.fillRect(x, 0, portion, getHeight());

                    // draw a black border around the colored rectangle
                    g2.setColor(Color.BLACK);
                    g2.drawRect(x, 0, portion, getHeight());

                    if (property.getName().contains(" ")) { // name has spaces
                        final String[] args = property.getName().split(" "); // separate the name by spaces
                        StringBuilder sb = new StringBuilder();
                        for (String word : args) sb.append(word.charAt(0)); // add the first letter of each word to the string builder
                        g2.drawString(sb.toString(), (int) (getWidth() * 0.45), (int) (getHeight() * 0.63)); // draw the string
                    }

                    g2.setFont(font.deriveFont(affineTransform));
                }
            } else if (quadrant == Board.Quadrant.TOP) { // top of the board

                // new transform to rotate 90 degrees
                final AffineTransform affineTransform = new AffineTransform();
                affineTransform.rotate(Math.toRadians(180), 0, 0);

                // new font
                final Font font = new Font("Arial Black", Font.BOLD, 14);
                g2.setFont(font.deriveFont(affineTransform));
                g2.setColor(Color.BLACK);

                if (getProperty().getGroup() == Property.Group.STATIONS) { // is railroad station
                    try {
                        final URL url = Game.class.getResource("/resources/stations/station_top.png"); // load bottom image
                        if (url != null) {
                            // scale it down some and draw it to the panel
                            g2.scale(0.9, 0.78);
                            g2.drawImage(ImageIO.read(url), (int) (getWidth() * 0.05), (int) (getHeight() * 0.05), getWidth(), getHeight(), this);
                            g2.setTransform(original);
                        }
                    } catch (IOException e) {e.printStackTrace();} // print stack trace

                    if (property.getName().contains(" ")) { // has spaces
                        final String[] args = property.getName().split(" "); // separate by spaces
                        StringBuilder sb = new StringBuilder();
                        for (String word : args) sb.append(word.charAt(0)); // first letter of each word
                        g2.drawString(sb.toString(), (int) (getWidth() * 0.65), (int) (getHeight() - (getHeight() * 0.17))); // draw string to panel
                    }
                } else if (getProperty().getName().equals("ELECTRIC COMPANY")) { // is railroad station
                    try {
                        final URL url = Game.class.getResource("/resources/utilities/ec.png"); // load bottom image
                        if (url != null) {
                            // scale it down some and draw it to the panel
                            // g2.scale(1, 0.78);
                            g2.drawImage(ImageIO.read(url), 0, 0, getWidth(), getHeight(), this);
                            g2.setTransform(original);
                        }
                    } catch (IOException e) {e.printStackTrace();} // print stack trace
                } else if (getProperty().getName().equals("COMMUNITY CHEST")) { // is community chest
                    try {
                        final URL url = Game.class.getResource("/resources/chest/chest_top.png"); // load left-side image
                        if (url != null) {
                            // scale it down some and draw it to the panel
                            g2.scale(0.8, 0.65);
                            g2.drawImage(ImageIO.read(url), (int) (getWidth() * 0.15), (int) (getHeight() * 0.05), getWidth(), getHeight(), this);
                            g2.setTransform(original);
                        }
                    } catch (IOException e) {e.printStackTrace();}

                    final Font newFont = new Font("Arial Black", Font.BOLD, 10);
                    g2.setFont(newFont.deriveFont(affineTransform));

                    // draw the property name "Community Chest" on the panel
                    int tempX = (int) (getWidth() * 0.92),
                            tempY = (int) (getHeight() - (getHeight() * 0.14));

                    for (String word : getProperty().getName().split(" ")) {
                        g2.drawString(word, tempX, tempY);
                        tempX -= (getWidth() * 0.2);
                        tempY -= (getHeight() * 0.12);
                    }

                    g2.setFont(font.deriveFont(affineTransform));

                } else {
                    final int portion = (int) (getHeight() * 0.25),
                            y = (getHeight() - portion);

                    // draw group color rectangle at the bottom of the card
                    g2.setColor(getProperty().getGroup().getColor());
                    g2.fillRect(0, y, getWidth(), portion);

                    // draw a black border around the colored rectangle
                    g2.setColor(Color.BLACK);
                    g2.drawRect(0, y, getWidth(), portion);

                    if (property.getName().contains(" ")) { // name has spaces
                        final String[] args = property.getName().split(" "); // separate the name by spaces
                        StringBuilder sb = new StringBuilder();
                        for (String word : args) sb.append(word.charAt(0)); // add the first letter of each word to the string builder
                        g2.drawString(sb.toString(), (int) (getWidth() * ((sb.length() >= 3) ? 0.7 : 0.65)), (int) (getHeight() * 0.55)); // draw the string
                    }

                    g2.setFont(font.deriveFont(affineTransform));
                }

                // TODO add utilities, railroads, etc.
            } else if (quadrant == Board.Quadrant.BOTTOM) { // bottom of the board

                g2.setColor(Color.BLACK);

                // new font
                final Font font = new Font("Arial Black", Font.BOLD, 14);
                g2.setFont(font);

                if (getProperty().getGroup() == Property.Group.STATIONS) { // is railroad station
                    try {
                        final URL url = Game.class.getResource("/resources/stations/station_bottom.png"); // load bottom image
                        if (url != null) {
                            // scale it down some and draw it to the panel
                            g2.scale(0.9, 0.78);
                            g2.drawImage(ImageIO.read(url), (int) (getWidth() * 0.05), (int) (getHeight() * 0.27), getWidth(), getHeight(), this);
                            g2.setTransform(original);
                        }
                    } catch (IOException e) {e.printStackTrace();} // print stack trace

                    if (property.getName().contains(" ")) { // has spaces
                        final String[] args = property.getName().split(" "); // separate by spaces
                        StringBuilder sb = new StringBuilder();
                        for (String word : args) sb.append(word.charAt(0)); // first letter of each word
                        g2.drawString(sb.toString(), (int) (getWidth() * 0.43), (int) (getHeight() * 0.2)); // draw string to panel
                    }
                } else if (getProperty().getName().equals("CHANCE")) { // is chance
                    try {
                        final URL url = Game.class.getResource("/resources/chance/q-orange.png"); // load left-side image
                        if (url != null) {
                            // scale it down some and draw it to the panel
                            g2.scale(0.45, 0.65);
                            g2.drawImage(ImageIO.read(url), (int) (getWidth() * 0.63), (int) (getHeight() * 0.42), getWidth(), getHeight(), this);
                            g2.setTransform(original);
                        }
                    } catch (IOException e) {e.printStackTrace();}

                    // draw the property name "chance" on the panel
                    g2.drawString(getProperty().getName(), (int) (getWidth() * 0.1), (int) (getHeight() * 0.2));

                } else if (getProperty().getName().equals("COMMUNITY CHEST")) { // is community chest
                    try {
                        final URL url = Game.class.getResource("/resources/chest/chest_bottom.png"); // load left-side image
                        if (url != null) {
                            // scale it down some and draw it to the panel
                            g2.scale(0.8, 0.65);
                            g2.drawImage(ImageIO.read(url), (int) (getWidth() * 0.15), (int) (getHeight() * 0.5), getWidth(), getHeight(), this);
                            g2.setTransform(original);
                        }
                    } catch (IOException e) {e.printStackTrace();}

                    final Font newFont = new Font("Arial Black", Font.BOLD, 10);
                    g2.setFont(newFont);

                    // draw the property name "Community Chest" on the panel
                    int tempX = (int) (getWidth() * 0.08),
                            tempY = (int) (getHeight() * 0.14);

                    for (String word : getProperty().getName().split(" ")) {
                        g2.drawString(word, tempX, tempY);
                        tempX += (getWidth() * 0.2);
                        tempY += (getHeight() * 0.12);
                    }

                    g2.setFont(font);

                } else if (getProperty().getName().equals("LUXURY TAX")) { // is community chest
                    try {
                        final URL url = Game.class.getResource("/resources/tax/lux_tax.png"); // load left-side image
                        if (url != null) {
                            // scale it down some and draw it to the panel
                            g2.scale(0.95, 0.95);
                            g2.drawImage(ImageIO.read(url), 0, 0, getWidth(), getHeight(), this);
                            g2.setTransform(original);
                        }
                    } catch (IOException e) {e.printStackTrace();}
                } else {
                    final int portion = (int) (getHeight() * 0.25), y = 0;

                    // draw group color rectangle at the top of the card
                    g2.setColor(getProperty().getGroup().getColor());
                    g2.fillRect(0, y, getWidth(), portion);

                    // draw a black border around the colored rectangle
                    g2.setColor(Color.BLACK);
                    g2.drawRect(0, y, getWidth(), portion);

                    if (property.getName().contains(" ")) { // name has spaces
                        final String[] args = property.getName().split(" "); // separate the name by spaces
                        StringBuilder sb = new StringBuilder();
                        for (String word : args) sb.append(word.charAt(0)); // add the first letter of each word to the string builder
                        g2.drawString(sb.toString(), (int) (getWidth() * ((sb.length() >= 3) ? 0.3 : 0.4)), (int) (getHeight() * 0.45)); // draw the string
                    } else if (property.getName().equals("BOARDWALK"))
                        g2.drawString("BW", (int) (getWidth() * 0.33), (int) (getHeight() * 0.45)); // draw the string
                }
            }

            g2.setTransform(original);  // reset the transform
        }

        if (Game.INSTANCE.getBoard().getSelectedProperty() != null && Game.INSTANCE.getBoard().getSelectedProperty() == this) {
            // color the panel size to white
            g2.setColor(Color.PINK);
            g2.setStroke(new BasicStroke(5));
            g2.drawRect(0, 0, (int) (getWidth() - (getWidth() * 0.008)), (int) (getHeight() - (getHeight() * 0.008)));
        }
    }

    /**
     * @return Converts the property card JPanel into a BufferedImage.
     */
    public BufferedImage toImage() {
        BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB); // RGB buffered image instance
        Graphics2D g2d = img.createGraphics(); // create new 2s graphics
        print(g2d); // print the image
        g2d.dispose(); // dispose changes
        return img; // return the image
    }

    /**
     * @return The property the card is associated with.
     */
    public Property getProperty() {return property;}

    /**
     * @return The quadrant the property is a part of.
     */
    public Board.Quadrant getQuadrant() {return quadrant;}


}