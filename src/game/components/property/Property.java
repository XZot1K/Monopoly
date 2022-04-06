package game.components.property;

import game.components.entity.Player;

import javax.naming.InsufficientResourcesException;
import java.awt.*;
import java.util.Optional;
import java.util.stream.Stream;

public class Property {

    private final String name;
    private final Group group;
    private final int value;
    private final int[][] positions;

    private Player owner;


    public Property(String name, Group group, int value, int... positions) throws InsufficientResourcesException {
        this.name = name;
        this.group = group;
        this.value = value;
        setOwner(null);

        if (positions.length <= 0)
            throw new InsufficientResourcesException("No positions were provided.");

        if ((positions.length % 2) != 0)
            throw new InsufficientResourcesException("Positions must be provided in pairs of 2 for the X & Y axis.");


        this.positions = new int[positions.length / 2][2]; // new 2D array for positions in pairs of 2

        int lastCoordinate = 0;
        for (int i = -1; ++i < positions.length; ) {
            final int coordinate = positions[i];

            if ((i % 2) != 0) { // each odd index represents the associate 'y' axis value for the lastCoordinate ('x' axis)
                getPositions()[i / 2] = new int[]{lastCoordinate, coordinate}; // add new coordinate pair to the array
                continue;
            }

            lastCoordinate = coordinate; // update the lastCoordinate variable to the found 'x' axis
        }

        final Optional<int[]> position = Stream.of(getPositions()).findAny();
        if(!position.isPresent()) throw new InsufficientResourcesException("The property has no defined positions.");
    }

    public boolean isOwned() {return (getOwner() != null);}

    //public JPanel buildPropertyCard() {
    //    return new ;
    //}

    public boolean contains(int x, int y) {
        return Stream.of(getPositions()).anyMatch(position -> (position[0] == x && position[1] == y));
    }

    public boolean isCorner() {return (contains(0, 0) || contains(0, 10) || contains(10, 0) || contains(10, 10));}

    // getters & setters

    public Group getGroup() {return group;}

    public Player getOwner() {return owner;}

    public void setOwner(Player owner) {this.owner = owner;}

    public int getValue() {return value;}

    public String getName() {return name;}

    public int[][] getPositions() {return positions;}

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