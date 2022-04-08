package game.components.property;

import game.components.entity.Token;

import javax.naming.InsufficientResourcesException;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class Property {

    private final String name;
    private final Group group;
    private final int value;
    private final ArrayList<Position> positions;

    private Token owner;


    public Property(String name, Group group, int value, Position... positions) throws InsufficientResourcesException {
        this.name = name;
        this.group = group;
        this.value = value;
        this.positions = new ArrayList<>();
        setOwner(null);

        if (positions.length <= 0)
            throw new InsufficientResourcesException("No positions were provided.");

        if (!getPositions().isEmpty()) throw new InsufficientResourcesException("The property has no defined positions.");
        Collections.addAll(getPositions(), positions);
    }

    public boolean isOwned() {return (getOwner() != null);}

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

    public boolean isCorner() {return (contains(0, 0) || contains(0, 10) || contains(10, 0) || contains(10, 10));}

    // getters & setters

    public Group getGroup() {return group;}

    public Token getOwner() {return owner;}

    public void setOwner(Token owner) {this.owner = owner;}

    public int getValue() {return value;}

    public String getName() {return name;}

    public ArrayList<Position> getPositions() {return positions;}

    public enum Group {
        BROWN(new Color(101, 67, 33)), LIGHT_BLUE(new Color(173, 216, 230)),
        PINK(Color.MAGENTA), ORANGE(new Color(255, 140, 0)),
        RED(Color.RED), YELLOW(Color.YELLOW), GREEN(new Color(0, 100, 0)),
        DARK_BLUE(Color.BLUE), STATIONS(Color.BLACK),
        UTILITIES(Color.WHITE), NONE(Color.GRAY);

        private final Color color;

        Group(Color color) {
            this.color = color;
        }

        public Color getColor() {return color;}
    }

}