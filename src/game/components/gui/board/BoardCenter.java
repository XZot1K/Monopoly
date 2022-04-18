package game.components.gui.board;

import game.Game;
import game.components.gui.Controller;
import game.components.gui.LogBox;
import game.components.property.Property;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.net.URL;

public class BoardCenter extends JPanel {

    private final LogBox logBox;
    private final Controller controller;
    public int width, height;

    public int[] roll;


    public BoardCenter(int width, int height) {
        setBackground(new Color(144, 238, 144)); // a nice green

        this.width = width;
        this.height = height;
        this.roll = new int[]{0, 0};

        setLayout(new GridLayout(2, 2, (int) (width * 0.15), (int) (height * 0.3)));

        add(new JLabel(), 0);
        add((controller = new Controller(this)), 1);
        add((logBox = new LogBox()).getScrollPane(), 2);


        update();
        setVisible(true);
    }

    public void update() {
        GridLayout layout = (GridLayout) getLayout();
        layout.setHgap((int) (width * 0.15));
        layout.setVgap((int) (height * 0.3));
        setBorder(BorderFactory.createEmptyBorder((int) (height * 0.12), (int) (width * 0.12),
                (int) (height * 0.12), (int) (width * 0.12)));
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
            final Property property = Game.INSTANCE.getSelectedProperty().getProperty();

            if (property.getName().equals("GO")) {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.21), (int) (getHeight() * 0.06), 20, 20);

                g2.setColor(property.getGroup().getColor());
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.21), (int) (getHeight() * 0.06), 20, 20);

                g2.setColor(Color.BLACK);
                g2.setFont(font.deriveFont(12f));

                g2.drawString(property.getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.15));

                g2.setFont(font.deriveFont(Font.PLAIN, 11));
                g2.drawString("Pass go and collect $200.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.17));
                g2.setFont(font);
            } else if (property.getName().equals("IN JAIL/JUST VISITING")) {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.265), (int) (getHeight() * 0.165), 20, 20);

                g2.setColor(property.getGroup().getColor());
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.265), (int) (getHeight() * 0.165), 20, 20);

                g2.setColor(Color.BLACK);
                g2.setFont(font.deriveFont(12f));

                g2.drawString(property.getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.15));

                g2.setFont(font.deriveFont(Font.PLAIN, 11));
                g2.drawString(" If in jail, the player can wait 3 turns,", (int) (getWidth() * 0.14), (int) (getHeight() * 0.17));
                g2.drawString("pay $50 and attempt to roll doubles on", (int) (getWidth() * 0.14), (int) (getHeight() * 0.19));
                g2.drawString("any of their next 3 turns, or use a get", (int) (getWidth() * 0.14), (int) (getHeight() * 0.21));
                g2.drawString("out of jail free card.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.23));

                g2.drawString(" Upon being released from jail, no other", (int) (getWidth() * 0.14), (int) (getHeight() * 0.255));
                g2.drawString("actions may be performed that turn.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.275));
                g2.setFont(font);
            } else if (property.getName().equals("FREE PARKING")) {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.277), (int) (getHeight() * 0.12), 20, 20);

                g2.setColor(property.getGroup().getColor());
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.277), (int) (getHeight() * 0.12), 20, 20);

                g2.setColor(Color.BLACK);
                g2.setFont(font.deriveFont(12f));

                g2.drawString(property.getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.15));

                g2.setFont(font.deriveFont(Font.PLAIN, 11));
                g2.drawString("Collect all fee earnings from the jackpot.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.18));
                g2.drawString("If the jackpot is empty, collect $100.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.2));

                g2.drawString("Current Jackpot: $" + Game.INSTANCE.getJackpot(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.23));

                g2.setFont(font);
            } else if (property.getName().equals("GO TO JAIL")) {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.24), (int) (getHeight() * 0.06), 20, 20);

                g2.setColor(property.getGroup().getColor());
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.24), (int) (getHeight() * 0.06), 20, 20);

                g2.setColor(Color.BLACK);
                g2.setFont(font.deriveFont(12f));

                g2.drawString(property.getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.15));

                g2.setFont(font.deriveFont(Font.PLAIN, 11));
                g2.drawString("Go to jail and do NOT collect $200.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.17));
                g2.setFont(font);
            } else if (property.getName().equals("COMMUNITY CHEST")) {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.24), (int) (getHeight() * 0.06), 20, 20);

                g2.setColor(property.getGroup().getColor());
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.24), (int) (getHeight() * 0.06), 20, 20);

                g2.setColor(Color.BLACK);
                g2.setFont(font.deriveFont(12f));

                g2.drawString(property.getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.15));

                g2.setFont(font.deriveFont(Font.PLAIN, 11));
                g2.drawString("Draw a community chest card.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.17));
                g2.setFont(font);
            } else if (property.getName().equals("CHANCE")) {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.15), (int) (getHeight() * 0.06), 20, 20);

                g2.setColor(property.getGroup().getColor());
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.15), (int) (getHeight() * 0.06), 20, 20);

                g2.setColor(Color.BLACK);
                g2.setFont(font.deriveFont(12f));

                g2.drawString(property.getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.15));

                g2.setFont(font.deriveFont(Font.PLAIN, 11));
                g2.drawString("Draw a chance card.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.17));
                g2.setFont(font);
            } else if (property.getName().equals("INCOME TAX")) {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.23), (int) (getHeight() * 0.06), 20, 20);

                g2.setColor(property.getGroup().getColor());
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.23), (int) (getHeight() * 0.06), 20, 20);

                g2.setColor(Color.BLACK);
                g2.setFont(font.deriveFont(12f));

                g2.drawString(property.getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.15));

                g2.setFont(font.deriveFont(Font.PLAIN, 11));
                g2.drawString("Pay 10% OR $200 to the jackpot.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.17));
                g2.setFont(font);
            } else if (property.getName().equals("LUXURY TAX")) {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.17), (int) (getHeight() * 0.06), 20, 20);

                g2.setColor(property.getGroup().getColor());
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.17), (int) (getHeight() * 0.06), 20, 20);

                g2.setColor(Color.BLACK);
                g2.setFont(font.deriveFont(12f));

                g2.drawString(property.getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.15));

                g2.setFont(font.deriveFont(Font.PLAIN, 11));
                g2.drawString("Pay $75 to the jackpot.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.17));
                g2.setFont(font);
            } else {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.32), (int) (getHeight() * 0.2), 20, 20);

                g2.setColor(property.getGroup().getColor());
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect((int) (getWidth() * 0.125), (int) (getHeight() * 0.125), (int) (getWidth() * 0.32), (int) (getHeight() * 0.2), 20, 20);

                g2.setColor(Color.BLACK);
                g2.setFont(font.deriveFont(12f));

                g2.drawString(property.getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.15));

                if (property.isPurchasable()) {
                    g2.drawString("Value: " + property.getBaseValue(), (int) (getWidth() * 0.35), (int) (getHeight() * 0.15));
                    g2.drawString("Available For Purchase", (int) (getWidth() * 0.14), (int) (getHeight() * 0.31));
                } else if (property.getOwner() != null) {
                    g2.drawString("Rent: " + property.getRent(), (int) (getWidth() * 0.35), (int) (getHeight() * 0.15));
                    g2.drawString("Sell: " + property.getRent(), (int) (getWidth() * 0.35), (int) (getHeight() * 0.17));
                    g2.drawString("Mortgage: " + property.getMortgage(), (int) (getWidth() * 0.35), (int) (getHeight() * 0.19));
                    g2.drawString("Owned By " + property.getOwner().getName(), (int) (getWidth() * 0.14), (int) (getHeight() * 0.31));
                }

                try {
                    if (property.getGroup() != Property.Group.STATIONS && property.getGroup() != Property.Group.UTILITIES && property.getGroup() != Property.Group.NONE) {
                        if (property.getHotels() <= 0) {
                            final URL url = Game.class.getResource("/resources/upgrades/house.png"); // load left-side image
                            if (url != null) {
                                // scale it down some and draw it to the panel
                                g2.scale(0.03, 0.03);
                                g2.drawImage(ImageIO.read(url), (int) (getWidth() * 4.5), (int) (getHeight() * 5.5), getWidth(), getHeight(), this);
                                g2.setTransform(original);
                            }

                            g2.setFont(font.deriveFont(14f));
                            g2.drawString(String.valueOf(property.getHouses()), (int) (getWidth() * 0.17), (int) (getHeight() * 0.19));
                        } else {
                            final URL url2 = Game.class.getResource("/resources/upgrades/hotel.png"); // load left-side image
                            if (url2 != null) {
                                // scale it down some and draw it to the panel
                                g2.scale(0.03, 0.03);
                                g2.drawImage(ImageIO.read(url2), (int) (getWidth() * 4.5), (int) (getHeight() * 5.5), getWidth(), getHeight(), this);
                                g2.setTransform(original);
                            }
                            g2.drawString(String.valueOf(property.getHouses()), (int) (getWidth() * 0.17), (int) (getHeight() * 0.19));
                        }
                    }
                } catch (IOException e) {e.printStackTrace();}

                if (property.getName().equals("ELECTRIC COMPANY")) {
                    g2.setFont(font.deriveFont(Font.PLAIN, 11));
                    g2.drawString(" If \"Water Works\" is NOT owned by the owner,", (int) (getWidth() * 0.14), (int) (getHeight() * 0.19));
                    g2.drawString("roll the dice and pay 4x the rolled amount.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.21));

                    g2.drawString(" If \"Water Works\" IS owned by the owner, ", (int) (getWidth() * 0.14), (int) (getHeight() * 0.235));
                    g2.drawString("roll the dice and pay 10x the rolled amount.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.255));

                    g2.setFont(font);
                } else if (property.getName().equals("WATER WORKS")) {
                    g2.setFont(font.deriveFont(Font.PLAIN, 11));
                    g2.drawString(" If \"Electric Company\" is NOT owned by the ", (int) (getWidth() * 0.14), (int) (getHeight() * 0.19));
                    g2.drawString("owner, roll the dice and pay 4x the rolled", (int) (getWidth() * 0.14), (int) (getHeight() * 0.21));
                    g2.drawString("amount.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.23));

                    g2.drawString(" If \"Electric Company\" IS owned by the owner, ", (int) (getWidth() * 0.14), (int) (getHeight() * 0.255));
                    g2.drawString("roll the dice and pay 10x the rolled amount.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.275));

                    g2.setFont(font);
                } else if (property.getGroup() == Property.Group.STATIONS) {
                    g2.setFont(font.deriveFont(Font.PLAIN, 11));
                    g2.drawString("Rent increases by $25 per owned railroad.", (int) (getWidth() * 0.14), (int) (getHeight() * 0.19));
                    g2.setFont(font);
                }
            }

        }

        if (getRoll()[0] > 0 && getRoll()[1] > 0) {
            drawDiceFace(g2, (int) (getWidth() * 1.97), (int) (getHeight() * 2.6), (int) (getWidth() * 0.21),
                    (int) (getHeight() * 0.21), getRoll()[0]);
            g2.setTransform(original);

            drawDiceFace(g2, (int) (getWidth() * 2.25), (int) (getHeight() * 2.6), (int) (getWidth() * 0.21),
                    (int) (getHeight() * 0.21), getRoll()[1]);
            g2.setTransform(original);
        }
    }

    public void drawDiceFace(Graphics2D g2, int x, int y, int width, int height, int dotCount) {
        try {
            final URL url = Game.class.getResource("/resources/dice.png"); // load dice sprite image
            if (url != null) {

                if (dotCount == 2 || dotCount == 5) width -= 0.05; // remove a bit off the side since these two in the sprite are slightly off center

                g2.scale(0.3, 0.3); // scale down
                final int cellX = ((dotCount - 1) % 3) * width, cellY = ((dotCount - 1) / 3) * height; // divide the image into cells

                // draw the image relative to the passed specifications onto the center board frame
                g2.drawImage(ImageIO.read(url), x, y, x + width, y + height,
                        cellX, cellY, cellX + width, cellY + height, this);
            }
        } catch (IOException e) {e.printStackTrace();} // will not throw
    }

    public LogBox getLogBox() {return logBox;}

    public Controller getController() {return controller;}

    public int[] getRoll() {return roll;}

    public void setRoll(int leftRoll, int rightRoll) {
        getRoll()[0] = leftRoll;
        getRoll()[1] = rightRoll;
        Game.INSTANCE.getBoard().repaint();
    }

}