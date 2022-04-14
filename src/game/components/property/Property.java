/*
    Jeremiah Osborne
    Date: 4/1/2022
 */

package game.components.property;

import game.Game;
import game.components.entity.Token;

import javax.naming.InsufficientResourcesException;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class Property { // NOTE: a property is just a location on the road (game board) so it doesn't have to be purchasable

    private final String name; // name of the property
    private final Group group; // the general group the property is a part of
    private final int value; // its money worth/value/cost
    private final ArrayList<Position> positions; // the positions where this property is located (since locations like "chance" have several)

    private Token owner; // the current owner, if any

    /**
     * @param name      The name of the property.
     * @param group     The group the property belongs to.
     * @param value     The cost/value of the property.
     * @param positions The positions associated with the property.
     * @throws InsufficientResourcesException Insufficient positions were provided
     */
    public Property(String name, Group group, int value, Position... positions) throws InsufficientResourcesException {

        // initialize variables
        this.name = name;
        this.group = group;
        this.value = value;
        this.positions = new ArrayList<>();
        setOwner(null);

        if (positions.length <= 0) // no positions
            throw new InsufficientResourcesException("No positions were provided.");

        Collections.addAll(getPositions(), positions); // add all positions to the positions list
    }

    /**
     * @return Checks if the property is owned.
     */
    public boolean isOwned() {return (getOwner() != null);}

    /**
     * @param coords The coordinates to check for, can be either 0-39 or the layout coordinates x: <0-10>, y: <0-10>.
     * @return if the coordinate was found in the property positions.
     */
    public boolean contains(int... coords) {
        if (coords.length == 1) {
            for (Position position : getPositions())
                if (position.getPosition() == coords[0]) return true;
        } else if (coords.length == 2) {
            for (Position position : getPositions())
                if (position.getX() == coords[0] && position.getY() == coords[1])
                    return true;
        }

        return false;
    }

    /**
     * @return checks if a corner's coordinates are contained in the property positions & returns the result.
     */
    public boolean isCorner() {return (contains(0, 0) || contains(0, 10) || contains(10, 0) || contains(10, 10));}

    /**
     * @return Checks if the location is a purchasable property.
     */
    public boolean isPurchasable() {return (!isCorner() && getValue() > 0 && !isOwned());}

    /**
     * @return Gets properties in the same group & returns if the token owns
     */
    public boolean canBuildHouses() {
        return Game.INSTANCE.getBoard().getProperties(getGroup()).stream().anyMatch(property ->
                property.getOwner() != null && getOwner() != null && property.getOwner() == getOwner());
    }

    // getters & setters

    /**
     * @return The group the property belongs to.
     */
    public Group getGroup() {return group;}

    /**
     * @return The owner of the property
     */
    public Token getOwner() {return owner;}

    /**
     * @param owner The owner to set.
     */
    public void setOwner(Token owner) {this.owner = owner;}

    /**
     * @return The value/cost of the property.
     */
    public int getValue() {return value;}

    /**
     * @return The name of the property.
     */
    public String getName() {return name;}

    /**
     * @return The list of positions the property contains.
     */
    public ArrayList<Position> getPositions() {return positions;}

    public enum Group {

        // groups and their colors
        BROWN(new Color(101, 67, 33)), LIGHT_BLUE(new Color(173, 216, 230)),
        PINK(Color.MAGENTA), ORANGE(new Color(255, 140, 0)),
        RED(Color.RED), YELLOW(Color.YELLOW), GREEN(new Color(0, 100, 0)),
        DARK_BLUE(Color.BLUE), STATIONS(Color.BLACK),
        UTILITIES(Color.WHITE), NONE(Color.GRAY);

        private final Color color; // color

        Group(Color color) {
            this.color = color;
        }

        /**
         * @return The color of the group.
         */
        public Color getColor() {return color;}
    }

}