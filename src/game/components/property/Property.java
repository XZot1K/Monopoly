/*
    Jeremiah Osborne
    Date: 4/1/2022
 */

package game.components.property;

import game.Game;
import game.components.entity.Token;
import game.components.gui.board.Board;

import java.awt.*;

public class Property { // NOTE: a property is just a location on the road (game board) so it doesn't have to be purchasable

    private final String name; // name of the property
    private final Group group; // the general group the property is a part of
    private final Position position; // the position where this property is located (since locations like "chance" have several)
    private final int baseValue, // its money base worth/value/cost
            baseRent, // the base the money rent
            mortgage; // the mortgage cost
    private int houses, hotels; // the house and hotel counts

    private boolean mortgaged; // whether the property is in mortgaged state
    private Token owner; // the current owner, if any

    private PropertyCard propertyCard; // property card

    /**
     * @param name      The name of the property.
     * @param group     The group the property belongs to.
     * @param baseValue The base cost/value of the property.
     */
    public Property(String name, Group group, int baseValue, int baseRent, int mortgage, Position position) {
        // initialize variables
        this.name = name;
        this.group = group;
        this.baseValue = baseValue;
        this.baseRent = baseRent;
        this.mortgage = mortgage;
        this.position = position;
        setOwner(null);
        setHouses(0);
        setHotels(0);

        // determine which quadrant the property belongs to in sets of 10
        Board.Quadrant quadrant;
        if (getPosition().getSimplePosition() >= 0 && getPosition().getSimplePosition() < 10)
            quadrant = Board.Quadrant.LEFT;
        else if (getPosition().getSimplePosition() >= 10 && getPosition().getSimplePosition() < 20)
            quadrant = Board.Quadrant.TOP;
        else if (getPosition().getSimplePosition() >= 20 && getPosition().getSimplePosition() < 30)
            quadrant = Board.Quadrant.RIGHT;
        else quadrant = Board.Quadrant.BOTTOM;

        // create and setup property card for the property
        setPropertyCard(new PropertyCard(this, quadrant));
    }

    /**
     * @return Checks if the property is owned.
     */
    public boolean isOwned() {return (getOwner() != null);}

    /**
     * @param coords The coordinates to check for, can be either 0-39 or the layout coordinates x: <0-10>, y: <0-10>.
     * @return If the property's position matches the provided coordinates.
     */
    public boolean isSame(int... coords) {
        if (coords.length == 1) return position.getSimplePosition() == coords[0];
        else if (coords.length == 2)
            return position.getX() == coords[0] && position.getY() == coords[1];
        return false;
    }

    /**
     * @return checks if a corner's coordinates are contained in the property positions & returns the result.
     */
    public boolean isCorner() {return (isSame(0) || isSame(10) || isSame(20) || isSame(30));}

    /**
     * @return Checks if the location is a purchasable property.
     */
    public boolean isPurchasable() {return (!isCorner() && getBaseValue() > 0 && !isOwned());}

    /**
     * @param token The player/token for ownership checks.
     * @return If the property can have a house or hotel built.
     */
    public boolean canBuild(Token token) {
        return (getGroup() != Group.UTILITIES && getGroup() != Group.STATIONS && getGroup() != Group.NONE
                && Game.INSTANCE.ownsAll(getGroup(), token.getName())
                && getHotels() <= 0 && getHouses() < 4 && token.getMoney() >= getPerHouseCost());
    }

    /**
     * @return The cost to build a house/hotel on the property.
     */
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

    /**
     * @return The sell price of the property based on buildings.
     */
    public int getSellValue() {
        return ((getHotelRent() > 0) ? ((getPerHouseCost() * 5) / 2) // return half of hotel purchase price
                : ((getHouses() > 0) ? ((getPerHouseCost() * getHouses()) / 2) : getBaseValue())); // return half of houses purchase price
    }

    /**
     * @return The rent based on rules for specific properties, buildings, etc.
     */
    public int getRent() {
        // handle $25 * the number of stations/railroads owned
        if (getGroup() == Group.STATIONS) return (25 * (getOwner() != null ? Game.INSTANCE.getOwnedProperties(Group.STATIONS, getOwner().getName()).size() : 1));

            // handle cost based on buildings as long as the player also owns all in the same group
        else if (getHouses() == 0 && getHotels() == 0) return ((getOwner() != null && Game.INSTANCE.ownsAll(getGroup(),
                getOwner().getName())) ? (getBaseRent() * 2) : getBaseRent());
        else return ((getHotels() * getHotelRent()) + (getHouses() * 3) * getHouseRent());
    }

    /**
     * @return Gets the rent house increase based on the base rent value.
     */
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

    /**
     * @return Gets the hotel rent value based on the base rent value.
     */
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

    /**
     * @return Gets the cost to un-mortgage the property.
     */
    public int getUnMortgagedCost() {return (int) (getMortgage() + (getMortgage() * 0.1));}

    @Override
    public String toString() {
        // create a string based on property data
        return (getPosition().getSimplePosition() + "," + ((getOwner() != null) ? getOwner().getName() : "null")
                + "," + getHotels() + "," + getHouses() + "," + isMortgaged());
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
     * @return The position of the property.
     */
    public Position getPosition() {return position;}

    /**
     * @return Amount of houses the property has.
     */
    public int getHouses() {return houses;}

    /**
     * @param houses The amount of houses to set.
     */
    public void setHouses(int houses) {this.houses = houses;}

    /**
     * @return Amount of hotels the property has.
     */
    public int getHotels() {return hotels;}

    /**
     * @param hotels The amount of hotels to set.
     */
    public void setHotels(int hotels) {this.hotels = hotels;}

    /**
     * @return The mortgage value.
     */
    public int getMortgage() {return mortgage;}

    /**
     * @return If the property is mortgaged.
     */
    public boolean isMortgaged() {return mortgaged;}

    /**
     * @param mortgaged Whether to mortgage.
     */
    public void setMortgaged(boolean mortgaged) {this.mortgaged = mortgaged;}

    /**
     * @return The property card.
     */
    public PropertyCard getPropertyCard() {return propertyCard;}

    /**
     * @param propertyCard The new property card.
     */
    public void setPropertyCard(PropertyCard propertyCard) {this.propertyCard = propertyCard;}

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