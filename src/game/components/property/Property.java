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
    private final ArrayList<Position> positions; // the positions where this property is located (since locations like "chance" have several)
    private final int baseValue, // its money base worth/value/cost
            baseRent, // the base the money rent
            mortgage; // the mortgage cost
    private int houses, hotels; // the house and hotel counts

    private boolean mortgaged; // whether the property is in mortgaged state
    private Token owner; // the current owner, if any

    /**
     * @param name      The name of the property.
     * @param group     The group the property belongs to.
     * @param baseValue The base cost/value of the property.
     * @param positions The positions associated with the property.
     * @throws InsufficientResourcesException Insufficient positions were provided
     */
    public Property(String name, Group group, int baseValue, int baseRent, int mortgage, Position... positions) throws InsufficientResourcesException {

        // initialize variables
        this.name = name;
        this.group = group;
        this.baseValue = baseValue;
        this.baseRent = baseRent;
        this.mortgage = mortgage;
        this.positions = new ArrayList<>();
        setOwner(null);
        setHouses(0);
        setHotels(0);

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
    public boolean isPurchasable() {return (!isCorner() && getBaseValue() > 0 && !isOwned());}

    /**
     * @param token The player/token for ownership checks.
     * @return If the property can have a house or hotel built.
     */
    public boolean canBuild(Token token) {
        return (Game.INSTANCE.ownsAll(getGroup(), token.getName())
                && getHotels() <= 0 && getHouses() < 4 && token.getMoney() >= getPerHouseCost());
    }

    public int getPerHouseCost() {
        switch (getGroup()) {
            case BROWN:
            case LIGHT_BLUE:
                return 50;

            case PINK:
            case ORANGE:
                return 100;

            case RED:
            case YELLOW:
                return 150;

            case DARK_BLUE:
            case GREEN:
                return 200;

            default:
                return 0;
        }
    }

    public int getSellValue() {
        return ((getHotelRent() > 0) ? ((getPerHouseCost() * 5) / 2) // return half of hotel purchase price
                : ((getHouses() > 0) ? ((getPerHouseCost() * getHouses()) / 2) : 0)); // return half of houses purchase price
    }

    public int getRent() {
        if (getGroup() == Group.STATIONS) return (25 * (getOwner() != null ? Game.INSTANCE.getOwnedProperties(Group.STATIONS, getOwner().getName()).size() : 1));
        else if (getHouses() == 0 && getHotels() == 0) return ((getOwner() != null && Game.INSTANCE.ownsAll(getGroup(),
                getOwner().getName())) ? (getBaseRent() * 2) : getBaseRent());
        else return ((getHotels() * getHotelRent()) + (getHouses() * 3) * getHouseRent());
    }

    private int getHouseRent() {
        switch (getBaseRent()) {
            case 2:
                return 10;
            case 4:
                return 20;
            case 6:
                return 30;
            case 8:
                return 40;
            case 10:
                return 50;
            case 12:
                return 60;
            case 14:
                return 70;
            case 16:
                return 80;
            case 18:
                return 90;
            case 20:
                return 100;
            case 22:
                return 110;
            case 24:
                return 120;
            case 26:
                return 130;
            case 28:
                return 150;
            case 35:
                return 175;
            case 50:
                return 200;
            default:
                return 0;
        }
    }

    private int getHotelRent() {
        switch (getBaseRent()) {
            case 2:
                return 250;
            case 4:
                return 450;
            case 6:
                return 550;
            case 8:
                return 650;
            case 10:
                return 750;
            case 12:
                return 900;
            case 14:
                return 950;
            case 16:
                return 1000;
            case 18:
                return 1050;
            case 20:
                return 1100;
            case 22:
                return 1150;
            case 24:
                return 1200;
            case 26:
                return 1275;
            case 28:
                return 1400;
            case 35:
                return 1500;
            case 50:
                return 2000;
            default:
                return 0;
        }
    }

    public int getDeMortgagedCost() {return (int) (getMortgage() + (getMortgage() * 0.1));}

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
     * @return The base value/cost of the property.
     */
    public int getBaseValue() {return baseValue;}

    /**
     * @return The base rent of the property.
     */
    public int getBaseRent() {return baseRent;}

    /**
     * @return The name of the property.
     */
    public String getName() {return name;}

    /**
     * @return The list of positions the property contains.
     */
    public ArrayList<Position> getPositions() {return positions;}

    public int getHouses() {return houses;}

    public void setHouses(int houses) {this.houses = houses;}

    public int getHotels() {return hotels;}

    public void setHotels(int hotels) {this.hotels = hotels;}

    public int getMortgage() {return mortgage;}

    public boolean isMortgaged() {return mortgaged;}

    public void setMortgaged(boolean mortgaged) {this.mortgaged = mortgaged;}

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