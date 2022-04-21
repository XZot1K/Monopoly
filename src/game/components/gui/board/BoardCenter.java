package game.components.gui.board;

import game.Game;
import game.components.gui.Controller;
import game.components.gui.LogBox;
import game.components.property.Property;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class BoardCenter extends JPanel {

    private final LogBox logBox; // the log box for logging actions
    private final Controller controller; // the player turn controller (handles button actions and info display)
    public int width, height; // the stored width and height for internal panels
    public int[] roll; // an array of 2 integers for the left & right dice
    private BufferedImage[] diceFaces; // an array of each cell image from the dice image sprite


    public BoardCenter(int width, int height) {
        setBackground(new Color(144, 238, 144)); // a nice green

        // initialize variables
        this.width = width;
        this.height = height;
        this.roll = new int[]{0, 0};

        // new grid layout
        setLayout(new GridLayout(2, 2, (int) (width * 0.15), (int) (height * 0.3)));

        // initialize the border
        setBorder(BorderFactory.createEmptyBorder((int) (height * 0.12), (int) (width * 0.12),
                (int) (height * 0.12), (int) (width * 0.12)));

        add(new JLabel(), 0); // blank label as a spacer

        // initialize the controller and log box
        add((controller = new Controller(this)), 1);
        add((logBox = new LogBox()).getScrollPane(), 2);

        setupDiceFaces(); // split the dice sprite into cells and store snapshots of each side

        setVisible(true); // set visible
    }

    /**
     * Updates the center panel border.
     */
    public void updateBorder() {
        setBorder(BorderFactory.createEmptyBorder((int) (height * 0.12), (int) (width * 0.12),
                (int) (height * 0.12), (int) (width * 0.12)));
    }

    @Override
    public void repaint() {
        // while the layout is that of a grid layout adjust the gaps based on dimensions
        if (getLayout() instanceof GridLayout) {
            GridLayout layout = (GridLayout) getLayout();
            layout.setHgap((int) (width * 0.15));
            layout.setVgap((int) (height * 0.3));
        }

        // repaint the center panel and controller, if it exists
        super.repaint();
        if (getController() != null) getController().repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2 = (Graphics2D) g.create(); // 2d graphics
        final AffineTransform original = (AffineTransform) g2.getTransform().clone(); // original transform

        try {
            final URL url = Game.class.getResource("/resources/logo.png"); // url to the logo image file (inside the JAR)
            if (url != null) { // not null
                /*
                 * scale the image down by half its original size, draw it based on the panel's size and position /w the observer as the frame,
                 * and then reset the transform to the original state.
                 */
                g2.scale(0.5, 0.5);
                g2.drawImage(ImageIO.read(url), getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), this);
                g2.setTransform(original);
            }
        } catch (IOException e) {e.printStackTrace();} // the image wasn't found and an error occurred, shouldn't happen so print stack trace

        final Font font = new Font("Arial Black", Font.BOLD, 14);
        if (Game.INSTANCE.getSelectedProperty() != null) { // check if a property card is currently selected
            final Property property = Game.INSTANCE.getSelectedProperty();

            // switches through each property and paints the tooltip alongside its information
            switch (property.getName()) {
                case "GO": {
                    // paint white box
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.21), (int) (getHeight() * 0.06), 20, 20);

                    // paint group color border around box
                    g2.setColor(property.getGroup().getColor());
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.21), (int) (getHeight() * 0.06), 20, 20);

                    // update color and font
                    g2.setColor(Color.BLACK);
                    g2.setFont(font.deriveFont(12f));

                    // draw property name
                    g2.drawString(property.getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.15));

                    // draw some description content
                    g2.setFont(font.deriveFont(Font.PLAIN, 11));
                    g2.drawString("Pass go and collect $200.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.17));
                    g2.setFont(font);
                    break;
                }

                case "IN JAIL/JUST VISITING": {
                    // paint white box
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.265), (int) (getHeight() * 0.165), 20, 20);

                    // paint group color border around box
                    g2.setColor(property.getGroup().getColor());
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.265), (int) (getHeight() * 0.165), 20, 20);

                    // update color and font
                    g2.setColor(Color.BLACK);
                    g2.setFont(font.deriveFont(12f));

                    // draw property name
                    g2.drawString(property.getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.15));

                    // draw some description content
                    g2.setFont(font.deriveFont(Font.PLAIN, 11));
                    g2.drawString(" If in jail, the player can wait 3 turns,", (int) (getWidth() * 0.14), (int) (getHeight() * 0.17));
                    g2.drawString("pay $50 and attempt to roll doubles on", (int) (getWidth() * 0.14), (int) (getHeight() * 0.19));
                    g2.drawString("any of their next 3 turns, or use a get", (int) (getWidth() * 0.14), (int) (getHeight() * 0.21));
                    g2.drawString("out of jail free card.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.23));

                    g2.drawString(" Upon being released from jail, no other", (int) (getWidth() * 0.14), (int) (getHeight() * 0.255));
                    g2.drawString("actions may be performed that turn.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.275));
                    g2.setFont(font);
                    break;
                }

                case "FREE PARKING": {
                    // paint white box
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.277), (int) (getHeight() * 0.12), 20, 20);

                    // paint group color border around box
                    g2.setColor(property.getGroup().getColor());
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.277), (int) (getHeight() * 0.12), 20, 20);

                    // update color and font
                    g2.setColor(Color.BLACK);
                    g2.setFont(font.deriveFont(12f));

                    // draw property name
                    g2.drawString(property.getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.15));

                    // draw some description content
                    g2.setFont(font.deriveFont(Font.PLAIN, 11));
                    g2.drawString("Collect all fee earnings from the jackpot.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.18));
                    g2.drawString("If the jackpot is empty, collect $100.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.2));

                    g2.drawString("Current Jackpot: $" + Game.INSTANCE.getJackpot(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.23));

                    g2.setFont(font);
                    break;
                }

                case "GO TO JAIL": {
                    // paint white box
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.24), (int) (getHeight() * 0.06), 20, 20);

                    // paint group color border around box
                    g2.setColor(property.getGroup().getColor());
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.24), (int) (getHeight() * 0.06), 20, 20);

                    // update color and font
                    g2.setColor(Color.BLACK);
                    g2.setFont(font.deriveFont(12f));

                    // draw property name
                    g2.drawString(property.getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.15));

                    // draw some description content
                    g2.setFont(font.deriveFont(Font.PLAIN, 11));
                    g2.drawString("Go to jail and do NOT collect $200.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.17));
                    g2.setFont(font);
                    break;
                }

                case "COMMUNITY CHEST": {
                    // paint white box
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.24), (int) (getHeight() * 0.06), 20, 20);

                    // paint group color border around box
                    g2.setColor(property.getGroup().getColor());
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.24), (int) (getHeight() * 0.06), 20, 20);

                    // update color and font
                    g2.setColor(Color.BLACK);
                    g2.setFont(font.deriveFont(12f));

                    // draw property name
                    g2.drawString(property.getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.15));

                    // draw some description content
                    g2.setFont(font.deriveFont(Font.PLAIN, 11));
                    g2.drawString("Draw a community chest card.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.17));
                    g2.setFont(font);
                    break;
                }

                case "CHANCE": {
                    // paint white box
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.15), (int) (getHeight() * 0.06), 20, 20);

                    // paint group color border around box
                    g2.setColor(property.getGroup().getColor());
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.15), (int) (getHeight() * 0.06), 20, 20);

                    // update color and font
                    g2.setColor(Color.BLACK);
                    g2.setFont(font.deriveFont(12f));

                    // draw property name
                    g2.drawString(property.getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.15));

                    // draw some description content
                    g2.setFont(font.deriveFont(Font.PLAIN, 11));
                    g2.drawString("Draw a chance card.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.17));
                    g2.setFont(font);
                    break;
                }

                case "INCOME TAX": {
                    // paint white box
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.23), (int) (getHeight() * 0.06), 20, 20);

                    // paint group color border around box
                    g2.setColor(property.getGroup().getColor());
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.23), (int) (getHeight() * 0.06), 20, 20);

                    // update color and font
                    g2.setColor(Color.BLACK);
                    g2.setFont(font.deriveFont(12f));

                    // draw property name
                    g2.drawString(property.getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.15));

                    // draw some description content
                    g2.setFont(font.deriveFont(Font.PLAIN, 11));
                    g2.drawString("Pay 10% OR $200 to the jackpot.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.17));
                    g2.setFont(font);
                    break;
                }

                case "LUXURY TAX": {
                    // paint white box
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.17), (int) (getHeight() * 0.06), 20, 20);

                    // paint group color border around box
                    g2.setColor(property.getGroup().getColor());
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.17), (int) (getHeight() * 0.06), 20, 20);

                    // update color and font
                    g2.setColor(Color.BLACK);
                    g2.setFont(font.deriveFont(12f));

                    // draw property name
                    g2.drawString(property.getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.15));

                    // draw some description content
                    g2.setFont(font.deriveFont(Font.PLAIN, 11));
                    g2.drawString("Pay $75 to the jackpot.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.17));
                    g2.setFont(font);
                    break;
                }

                default: {
                    // paint white (gray if mortgaged) box
                    g2.setColor(property.isMortgaged() ? Color.LIGHT_GRAY : Color.WHITE);
                    g2.fillRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.32), (int) (getHeight() * 0.2), 20, 20);

                    // paint group color border around box
                    g2.setColor(property.getGroup().getColor());
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.32), (int) (getHeight() * 0.2), 20, 20);

                    // update color and font
                    g2.setColor(Color.BLACK);
                    g2.setFont(font.deriveFont(12f));

                    // draw property name
                    g2.drawString(property.getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.15));

                    // draw some description content

                    if (property.isPurchasable()) { // if purchasable

                        // draw cost and whether it can be bought
                        g2.drawString("Cost: " + property.getBaseValue(), (int) (getWidth() * 0.35), (int) (getHeight() * 0.15));
                        g2.drawString("Available For Purchase", (int) (getWidth() * 0.14), (int) (getHeight() * 0.31));

                    } else if (property.getOwner() != null) { // has owner

                        if (!property.isMortgaged()) { // is mortgaged

                            // draw rent and sell price
                            g2.drawString("Rent: " + property.getRent(), (int) (getWidth() * 0.34), (int) (getHeight() * 0.15));
                            g2.drawString("Sell: " + property.getRent(), (int) (getWidth() * 0.34), (int) (getHeight() * 0.17));

                        } else
                            g2.drawString("Mortgaged", (int) (getWidth() * 0.34), (int) (getHeight() * 0.15)); // draw if its mortgaged

                        // draw who owns it
                        g2.drawString("Owned By " + property.getOwner().getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.31));
                    }

                    if (property.getMortgage() > 0) { // check if there is a mortgage value

                        if (!property.isMortgaged()) // is not mortgaged

                            g2.drawString("Mortgage: " + property.getMortgage(), (int) (getWidth() * 0.33), (int) (getHeight() * 0.31)); // draw mortgage cost
                        else
                            g2.drawString("Un-Mortgage: " + property.getUnMortgagedCost(), (int) (getWidth() * 0.31), (int) (getHeight() * 0.31)); // draw un-mortgage cost

                    }

                    try {
                        // check if property group is not a normal property
                        if (property.getGroup() != Property.Group.STATIONS && property.getGroup() != Property.Group.UTILITIES && property.getGroup() != Property.Group.NONE) {

                            if (property.getHotels() <= 0) { // check if it has no hotels

                                final URL url = Game.class.getResource("/resources/upgrades/house.png"); // load house image
                                if (url != null) {
                                    // scale it down some and draw it to the panel
                                    g2.scale(0.03, 0.03);
                                    g2.drawImage(ImageIO.read(url), (int) (getWidth() * 4.5), (int) (getHeight() * 5.5), getWidth(), getHeight(), this);
                                    g2.setTransform(original);
                                }

                                // update font and draw house count
                                g2.setFont(font.deriveFont(14f));
                                g2.drawString(String.valueOf(property.getHouses()), (int) (getWidth() * 0.17), (int) (getHeight() * 0.19));

                            } else { // has hotels

                                final URL url2 = Game.class.getResource("/resources/upgrades/hotel.png"); // load hotel image
                                if (url2 != null) {
                                    // scale it down some and draw it to the panel
                                    g2.scale(0.03, 0.03);
                                    g2.drawImage(ImageIO.read(url2), (int) (getWidth() * 4.5), (int) (getHeight() * 5.5), getWidth(), getHeight(), this);
                                    g2.setTransform(original);
                                }

                                // draw hotel count
                                g2.drawString(String.valueOf(property.getHouses()), (int) (getWidth() * 0.17), (int) (getHeight() * 0.19));
                            }
                        }
                    } catch (IOException e) {e.printStackTrace();}

                    if (property.getName().equals("ELECTRIC COMPANY")) { // electric company

                        // draw description
                        g2.setFont(font.deriveFont(Font.PLAIN, 11));
                        g2.drawString(" If \"Water Works\" is NOT owned by the owner,", (int) (getWidth() * 0.14), (int) (getHeight() * 0.19));
                        g2.drawString("roll the dice and pay 4x the rolled amount.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.21));

                        g2.drawString(" If \"Water Works\" IS owned by the owner, ", (int) (getWidth() * 0.14), (int) (getHeight() * 0.235));
                        g2.drawString("roll the dice and pay 10x the rolled amount.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.255));

                        g2.setFont(font);

                    } else if (property.getName().equals("WATER WORKS")) { // water works

                        // draw description
                        g2.setFont(font.deriveFont(Font.PLAIN, 11));
                        g2.drawString(" If \"Electric Company\" is NOT owned by the ", (int) (getWidth() * 0.14), (int) (getHeight() * 0.19));
                        g2.drawString("owner, roll the dice and pay 4x the rolled", (int) (getWidth() * 0.14), (int) (getHeight() * 0.21));
                        g2.drawString("amount.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.23));

                        g2.drawString(" If \"Electric Company\" IS owned by the owner, ", (int) (getWidth() * 0.14), (int) (getHeight() * 0.255));
                        g2.drawString("roll the dice and pay 10x the rolled amount.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.275));

                        g2.setFont(font);

                    } else if (property.getGroup() == Property.Group.STATIONS) { // rail road

                        // draw description
                        g2.setFont(font.deriveFont(Font.PLAIN, 11));
                        g2.drawString("Rent increases by $25 per owned railroad.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.2));
                        g2.setFont(font);

                    }

                    break;
                }
            }

        }

        if (getRoll()[0] > 0 && getRoll()[1] > 0) { // if the left & right die are > 0 then draw their associated face image

            // draw left die with its specific face
            drawDiceFace(g2, (int) (getWidth() * 1.97), (int) (getHeight() * 2.6), (int) (getWidth() * 0.21),
                    (int) (getHeight() * 0.21), getRoll()[0]);
            g2.setTransform(original);

            // draw right die with its specific face
            drawDiceFace(g2, (int) (getWidth() * 2.25), (int) (getHeight() * 2.6), (int) (getWidth() * 0.21),
                    (int) (getHeight() * 0.21), getRoll()[1]);
            g2.setTransform(original);

        }

        if (Game.INSTANCE.getCurrentPlayerTurn() != null) { // check if there is a player as the current turn

            // scale down and drawn their icon
            g2.scale(0.04, 0.04);
            g2.drawImage(Game.INSTANCE.getCurrentPlayerTurn().getIcon().get(),
                    (int) (getWidth() * 13.3), (int) (getHeight() * 3.5), width, height, this);
            g2.setTransform(original);

        }
    }

    /**
     * @param g2       The 2D Graphics object.
     * @param x        The x coordinate.
     * @param y        The y coordinate.
     * @param width    The width of the die face.
     * @param height   The height of the die face.
     * @param dotCount The amount of dots from 1 to 6.
     */
    public void drawDiceFace(Graphics2D g2, int x, int y, int width, int height, int dotCount) {
        g2.scale(0.3, 0.3); // scale down

        // draw the image relative to the passed specifications onto the center board frame
        g2.drawImage(diceFaces[dotCount - 1], x, y, width, height, this);
    }

    /**
     * splits up the dice sprite and takes snapshots of each face, storing each face to its respectful dot count index.
     */
    private void setupDiceFaces() {
        final int width = 203, height = 205; // width and height of each face in the sprite
        diceFaces = new BufferedImage[6]; // new array for all 6 sides

        final URL url = Game.class.getResource("/resources/dice.png"); // load dice sprite image
        if (url != null) {
            try {
                final BufferedImage diceSprite = ImageIO.read(url); // read the sprite in
                for (int row = -1; ++row < 2; ) // row loop
                    for (int column = -1; ++column < 3; ) // column loop
                        diceFaces[(row * 3) + column] = diceSprite.getSubimage((column * width), (row * height), width, height); // set the face in the array
            } catch (IOException e) {e.printStackTrace();} // print stacktrace since this shouldn't happen
        }
    }

    /**
     * @return The log box.
     */
    public LogBox getLogBox() {return logBox;}

    /**
     * @return The controller for the player.
     */
    public Controller getController() {return controller;}

    /**
     * NOTE: each die will range from 1-6 (0 is to hide the dice).
     *
     * @return The dice roll storage (index 0 is the left die roll and 1 is the right die roll).
     */
    public int[] getRoll() {return roll;}

    /**
     * @param leftRoll  The left die roll value.
     * @param rightRoll The right die roll value.
     */
    public void setRoll(int leftRoll, int rightRoll) {
        getRoll()[0] = leftRoll;
        getRoll()[1] = rightRoll;
        Game.INSTANCE.updateBoard();
    }

}