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

public abstract class Token implements Comparable<Token> {

    private String name; // the name of the token
    private int money; // the money currently held by the token

    private Icon icon;

    private Property location; // the property the token is located at (position)

    public Token(String name) {
        setName(name); // initialize the token name, given the parameter
        setMoney(1500); // initialize the money held by the token (rules state 1500)
    }

    /**
     * @param distance the distance on the board to move the token
     */
    public abstract void move(int distance);

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

    public enum Icon {
        THIMBLE, CAR, TOP_HAT, WHEELBARROW, SCOTTIE, FLAT_IRON, BOOT, BATTLESHIP;

        /**
         * @return Gets the icon from file.
         */
        public BufferedImage getIcon() {
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