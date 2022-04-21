/*
    Jeremiah Osborne
    Date: 4/1/2022
 */

package game.components.entity;

import game.Game;
import game.components.property.Property;
import game.components.special.Chance;
import game.components.special.CommunityChest;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Token {

    private String name; // the name of the token
    private int money, // the money currently held by the token
            simplePosition, // simple coordinate between 0 and 39 for their current location
            lastPosition, // simple coordinate between 0 and 39 of their last location
            jailCounter; // turn counter for jail

    private Icon icon; // the icon

    private boolean inJail, // whether player is in jail
            gojCard, // if the player has a "Get Out of Jail Free" card
            sellinggojCard; // whether the player is selling their "Get Out of Jail Free" card

    private Property location; // the property the token is located at (position)

    public Token(String name) {
        setName(name); // initialize the token name, given the parameter
        setMoney(1500); // initialize the money held by the token (rules state 1500)
        setLocation(Game.INSTANCE.getByPosition(0, 10)); // set default position to GO

        // initialize variables
        setInJail(false);
        setGOJCard(false);
        setSellingGOJCard(false);
        setSimplePosition(0);
        setLastPosition(-1); // -1 represents no last location
    }

    /**
     * @param distance the distance on the board to move the token.
     * @return Whether to end the turn.
     */
    public boolean move(int distance) {
        Property property = null;

        //for the -3 card
        if (distance < 0) {
            final int newPos = (getSimplePosition() - 3);
            property = Game.INSTANCE.getByPosition((newPos < 0) ? (39 - newPos) : newPos); // update property
        }

        //otherwise
        else {
            for (int i = -1; ++i < distance; ) {
                //set last location
                setLastPosition(getSimplePosition());

                //set new location
                setSimplePosition(getSimplePosition() + 1);
                if (getSimplePosition() > 39) setSimplePosition(0);


                //check for passing GO & update property:
                if ((property = Game.INSTANCE.getByPosition(getSimplePosition())).isSame(0) && getLastPosition() == 39) {
                    Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + getName() + "\" passed go, and collected $200.");
                    setMoney(getMoney() + 200);
                }
            }
        }

        if (property != null) {
            setLocation(property);

            if (property.getPropertyCard() != null) Game.INSTANCE.setSelectedProperty(property);

            switch (property.getName()) {
                case "FREE PARKING": {
                    //free parking, remember to make bank player to be getting all the money
                    final int gainedMoney = Math.max(100, Game.INSTANCE.getJackpot());
                    setMoney(getMoney() + gainedMoney);
                    Game.INSTANCE.setJackpot(0);

                    // dialog for gaining money
                    JOptionPane.showMessageDialog(null, "You have gained $" + gainedMoney
                            + " from the jackpot!", "FREE PARKING", JOptionPane.INFORMATION_MESSAGE);

                    // log action
                    Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + getName() + " has won $" + gainedMoney + ".");
                    break;
                }

                case "LUXURY TAX": {
                    //luxury tax, pay $75
                    setMoney(getMoney() - 75);
                    //bank receives money
                    Game.INSTANCE.setJackpot(Game.INSTANCE.getJackpot() + 75);

                    // log action
                    Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + getName() + " payed the bank $75.");
                    break;
                }

                case "INCOME TAX": {
                    Object[] objects = {"10%", "$200"}; // selectable options for the option dialog

                    // new option dialog for multi-option selection
                    int result = JOptionPane.showOptionDialog(null, "Please choose a payment type:", "INCOME TAX - Payment Choice",
                            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, objects, objects[0]);

                    // if 10% was selected
                    if (result == JOptionPane.YES_OPTION) {
                        //income tax, pay 10% of their balance
                        final int lostMoney = (int) (getMoney() * 0.1);
                        setMoney(getMoney() - lostMoney);
                        //bank receives money
                        Game.INSTANCE.setJackpot(Game.INSTANCE.getJackpot() + lostMoney);
                        Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + getName() + " payed the bank $" + lostMoney + ".");
                    } else {
                        //income tax, pay $200
                        setMoney(getMoney() - 200);
                        //bank receives money
                        Game.INSTANCE.setJackpot(Game.INSTANCE.getJackpot() + 200);
                        Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + getName() + " payed the bank $200.");
                    }
                    break;
                }

                case "GO TO JAIL": {
                    // send to jail
                    setInJail(true);
                    setJailCounter(0);
                    setLocation(Game.INSTANCE.getByPosition(10));

                    // dialog for going to jail
                    JOptionPane.showMessageDialog(null, "Looks like you messed up... Good news is "
                            + "it only gets worse from here!", "GO TO JAIL", JOptionPane.WARNING_MESSAGE);

                    // log action
                    Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + getName() + " has been sent to jail.");
                    return true;
                }

                case "CHANCE": {
                    // log action
                    Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + getName() + " has drawn a \"CHANCE\" card.");

                    // draw and apply new chance card
                    final Chance chance = Chance.draw();
                    chance.apply(this);
                    break;
                }

                case "COMMUNITY CHEST": {
                    // log action
                    Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + getName() + " has drawn a \"COMMUNITY CHEST\" card.");

                    // draw and apply new community chest card
                    final CommunityChest communityChest = CommunityChest.draw();
                    communityChest.apply(this);
                    break;
                }

                default: {

                    // check if property has an owner
                    if (property.getOwner() != null && property.getOwner() != this) {

                        if (!property.isMortgaged()) { // check if the property is mortgaged

                            // set current player's money
                            final int amountToRemove = Math.min(getMoney(), property.getRent());
                            Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + getName() + "\" has paid \""
                                    + property.getOwner().getName() + "\" $" + amountToRemove + " for rent.");

                            setMoney(getMoney() - amountToRemove); // remove money

                            // give money to property owner
                            property.getOwner().setMoney(property.getOwner().getMoney() + amountToRemove);

                            // notify about rent payment
                            JOptionPane.showMessageDialog(null, "You have landed on \"" + property.getName()
                                    + "\" which is owned by \"" + property.getOwner().getName() + "\". You pay $" + property.getRent()
                                    + " in rent.", "Pay Up!", JOptionPane.INFORMATION_MESSAGE);

                        } else

                            // notify that there was no rent to pay
                            JOptionPane.showMessageDialog(null, "", "Lucky Day!", JOptionPane.INFORMATION_MESSAGE);
                    }

                    break;
                }
            }
        }

        Game.INSTANCE.updateBoard(); // update game board
        return false;
    }

    /**
     * finds nearest util, re
     */
    public Property findNearest(Property.Group group) {
        Property property;
        do {
            //set last location
            setLastPosition(getSimplePosition());

            //set new location
            setSimplePosition(getSimplePosition() + 1);
            if (getSimplePosition() > 39) setSimplePosition(0);

            property = Game.INSTANCE.getByPosition(getSimplePosition());

            //check for passing GO:
            if (property.isSame(0) && getLastPosition() == 39) {
                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + getName() + " passed go, and collected $200.");
                setMoney(getMoney() + 200); // gain $200
            }

        } while (property.getGroup() != group); //check group

        //set new location and repaint
        setLocation(property);
        Game.INSTANCE.updateBoard();
        return property;
    }

    /**
     * @param propertyName property to move to by name.
     * @param passGoCheck  Whether to collect $200 when passing go.
     */
    public void travel(String propertyName, boolean passGoCheck) {
        Property property;
        do {
            //set last location
            setLastPosition(getSimplePosition());

            //set new location
            setSimplePosition(getSimplePosition() + 1);
            if (getSimplePosition() > 39) setSimplePosition(0);

            property = Game.INSTANCE.getByPosition(getSimplePosition());

            //check for passing GO:
            if (passGoCheck && property.isSame(0) && getLastPosition() == 39) {
                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + getName() + " passed go, and collected $200.");
                setMoney(getMoney() + 200); // gain $200
            }

        } while (!property.getName().equalsIgnoreCase(propertyName)); // loop until proper is found

        //set new location and repaint
        setLocation(Game.INSTANCE.getByPosition(getSimplePosition()));

        // log action
        Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + getName() + " has advanced to \"" + property.getName() + "\".");
        Game.INSTANCE.updateBoard(); // update board
    }

    @Override
    public String toString() {
        return (getName() + "," + getMoney() + "," + getSimplePosition() + "," + getIcon().name()
                + "," + isInJail() + "," + getJailCounter() + "," + hasGOJCard() + "," + isSellingGOJCard());
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
    public void setMoney(int money) {
        this.money = money;

        // check for bankruptcy
        if (this.money <= 0) {
            Game.INSTANCE.nextTurn(); // next turn
            Game.INSTANCE.getPlayers().remove(this); // unregister the player

            // log action
            Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + getName() + "\" has gone bankrupt.");
        }
    }

    /**
     * @return The token's current position on the board.
     */
    public Property getLocation() {return location;}

    /**
     * @param location The location to set as the current position of the token.
     */
    public void setLocation(Property location) {
        // update last location to current and set new location alongside the simple position associated to it
        if (getLocation() != null) setLastPosition(getLocation().getPosition().getSimplePosition());
        setSimplePosition((this.location = location).getPosition().getSimplePosition());

        // only repaint board if it's been created
        if (Game.INSTANCE.getBoard() != null)
            Game.INSTANCE.updateBoard();
    }

    /**
     * @return The player's icon.
     */
    public Icon getIcon() {return icon;}

    /**
     * @param icon The icon to set as the player's icon
     */
    public void setIcon(Icon icon) {this.icon = icon;}

    /**
     * @return Whether the player is in jail
     */
    public boolean isInJail() {return inJail;}

    /**
     * @param inJail Whether the player should be in jail or not
     */
    public void setInJail(boolean inJail) {this.inJail = inJail;}

    /**
     * @return If the player has a "Get Out of Jail Free" card.
     */
    public boolean hasGOJCard() {return gojCard;}

    /**
     * @param gojCard Whether the player should have a "Get Out of Jail Free" card.
     */
    public void setGOJCard(boolean gojCard) {this.gojCard = gojCard;}

    /**
     * @return If the player is selling a "Get Out of Jail Free" card.
     */
    public boolean isSellingGOJCard() {return sellinggojCard;}


    /**
     * @param sellinggojCard If the player should be selling a "Get Out of Jail Free" card.
     */
    public void setSellingGOJCard(boolean sellinggojCard) {this.sellinggojCard = sellinggojCard;}

    /**
     * @return The simple property position (can be 0-39).
     */
    public int getSimplePosition() {return simplePosition;}

    /**
     * @param simplePosition A simple property position between 0 and 39.
     */
    public void setSimplePosition(int simplePosition) {this.simplePosition = simplePosition;}

    /**
     * @return The last simple property position (can be 0-39).
     */
    public int getLastPosition() {return lastPosition;}

    /**
     * @param lastPosition A last simple property position between 0 and 39.
     */
    public void setLastPosition(int lastPosition) {this.lastPosition = lastPosition;}

    /**
     * @return The counter for how many turns the player has been in jail.
     */
    public int getJailCounter() {return jailCounter;}

    /**
     * @param jailCounter The new counter for how long the player has been in jail.
     */
    public void setJailCounter(int jailCounter) {this.jailCounter = jailCounter;}

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