/*
    Jeremiah Osborne
    Date: 4/1/2022
 */

package game.components.entity;

import game.Game;
import game.components.property.Property;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Token implements Comparable<Token> {

    private String name; // the name of the token
    private int money; // the money currently held by the token

    private Icon icon;

    private boolean inJail, gojCard, sellinggojCard;

    private Property location; // the property the token is located at (position)

    public Token(String name) {
        setName(name); // initialize the token name, given the parameter
        setMoney(1500); // initialize the money held by the token (rules state 1500)
        setLocation(Game.INSTANCE.getByPosition(0, 10)); // set default position to GO
        setInJail(false);
        setGOJCard(false);
        setSellingGOJCard(false);
    }

    /**
     * @param distance the distance on the board to move the token
     */
    public void move(int distance) {

    }

    // getters & setters

    /**
     * @return The token name.
     */
    public String getName() {return name;}

    /**
     * @param name The name to set the token name to.
     */
    public void setName(String name) {this.name = name;}

    /**
     * @return The money held by the token.
     */
    public int getMoney() {return money;}

    /**
     * @param money The amount to set as the token's balance.
     */
    public void setMoney(int money) {this.money = money;}

    /**
     * @return The token's current position on the board.
     */
    public Property getLocation() {return location;}

    /**
     * @param location The location to set as the current position of the token.
     */
    public void setLocation(Property location) {this.location = location;}

    public Icon getIcon() {return icon;}

    public void setIcon(Icon icon) {this.icon = icon;}

    // compares tokens by name
    @Override
    public int compareTo(Token token) {return getName().compareToIgnoreCase(token.getName());}

    public boolean isInJail() {return inJail;}

    public void setInJail(boolean inJail) {this.inJail = inJail;}

    public boolean hasGOJCard() {return gojCard;}

    public void setGOJCard(boolean gojCard) {this.gojCard = gojCard;}

    public boolean isSellingGOJCard() {return sellinggojCard;}

    public void setSellingGOJCard(boolean sellinggojCard) {this.sellinggojCard = sellinggojCard;}

    public enum Icon {
        THIMBLE, CAR, TOP_HAT, WHEELBARROW, SCOTTIE, FLAT_IRON, BOOT, BATTLESHIP;

        /**
         * @param text The text to check through.
         * @return The icon whose name is mentioned in the text.
         */
        public static Icon getFromString(String text) {
            final String modifiedString = text.toUpperCase().replace(" ", "_");
            for (Icon icon : values()) if (modifiedString.contains(icon.name())) return icon;
            return null;
        }

        /**
         * @return Gets the icon from file.
         */
        public BufferedImage get() {
            final URL url = Game.class.getResource("/resources/tokens/" + name().toLowerCase().replace("_", "-") + ".png"); // load image
            if (url != null) {
                try {
                    return ImageIO.read(url); // attempt to read in and create the buffered image
                } catch (IOException e) {e.printStackTrace();} // print stack trace since it's an IO issue that should NOT occur
            }
            return null;
        }

    }

}