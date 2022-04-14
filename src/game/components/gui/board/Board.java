/*
    Jeremiah Osborne
    Date: 4/1/2022
 */

package game.components.gui.board;

import game.Game;
import game.components.property.Position;
import game.components.property.Property;
import game.components.property.PropertyCard;

import javax.naming.InsufficientResourcesException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class Board extends JFrame {

    private final Game INSTANCE; // game instance
    private final BoardCenter center; // the panel in the center of the board

    private final ArrayList<Property> properties; // the properties list

    // these are used to handle the custom drawn tooltips (BOTH will be NULL when a property card is not hovered on)
    private PropertyCard selectedProperty; // the selected property card (can be NULL)

    public Board(Game gameInstance) throws InsufficientResourcesException {
        INSTANCE = gameInstance; // game instance pass-through
        properties = new ArrayList<>(); // initialize the property list
        setupProperties(); // initialize and add all the properties to a list in-memory

        setSize(gameInstance.getEnteredSize(), gameInstance.getEnteredSize());
        setPreferredSize(new Dimension(gameInstance.getEnteredSize(), gameInstance.getEnteredSize()));
        setMinimumSize(new Dimension(getContentPane().getWidth(), getContentPane().getHeight()));

        setTitle("Monopoly - Turn 1"); // update title of frame
        setDefaultCloseOperation(EXIT_ON_CLOSE); // exit on close
        setLayout(null); // set the JFrame's layout to null as an initializer (will be updated later anyway)

        URL logo = Game.class.getResource("/resources/tokens/thimble.png");
        if (logo != null) setIconImage(new ImageIcon(logo).getImage());

        final Color greenBackground = new Color(144, 238, 144); // a nice green
        getContentPane().setBackground(greenBackground); // update the content pane's background color

        final GridBagLayout layout = new GridBagLayout(); // new grid bag layout
        final double cornerWeight = 0.25, sideWeight = 0.18; // weights

        // row weights to make corners squares vs being the same as the side pieces
        layout.rowWeights = new double[]{cornerWeight, sideWeight, sideWeight, sideWeight, sideWeight,
                sideWeight, sideWeight, sideWeight, sideWeight, sideWeight, cornerWeight};

        // column weights to make corners squares vs being the same as the side pieces
        layout.columnWeights = new double[]{cornerWeight, sideWeight, sideWeight, sideWeight, sideWeight,
                sideWeight, sideWeight, sideWeight, sideWeight, sideWeight, cornerWeight};

        getContentPane().setLayout(layout); // update the content pane layout

        int x, y; // default grid coords

        // creating panels on each side

        final Insets insets = new Insets(0, 0, 0, 0); // insets for the grid layout (spacing)
        for (int i = -1; ++i < 4; ) { // loop 4 times since there are 4 sides to the board
            for (int j = -1; ++j < 11; ) { // loop 11 times since there are 11 properties per side
                PropertyCard propertyCard = null; // new property card
                switch (i) { // switch 'i' since this helps determine which quadrant the position is in

                    case 0: { // top
                        x = j;
                        y = 0; // top will have a y-axis of 0

                        final int finalX = x, finalY = y; // finalize to use in optional stream

                        // filter the properties by which one contains the coordinates
                        Optional<Property> optional = getProperties().stream()
                                .filter(property -> property.contains(finalX, finalY)).findFirst();

                        // if present, create a property card (another JPanel which handles its individual visual)
                        if (optional.isPresent()) propertyCard = new PropertyCard(optional.get(), Quadrant.TOP); // takes a quadrant enum
                        break;
                    }

                    case 1: { // left
                        x = 0; // left will have an x-axis of 0
                        y = j;

                        final int finalX = x, finalY = y; // finalize to use in optional stream

                        // filter the properties by which one contains the coordinates
                        Optional<Property> optional = getProperties().stream()
                                .filter(property -> property.contains(finalX, finalY)).findFirst();

                        // if present, create a property card (another JPanel which handles its individual visual)
                        if (optional.isPresent()) propertyCard = new PropertyCard(optional.get(), Quadrant.LEFT); // takes a quadrant enum
                        break;
                    }

                    case 2: { // right
                        x = 10; // right will have an x-axis of 10
                        y = j;

                        final int finalX = x, finalY = y; // finalize to use in optional stream

                        // filter the properties by which one contains the coordinates
                        Optional<Property> optional = getProperties().stream()
                                .filter(property -> property.contains(finalX, finalY)).findFirst();

                        // if present, create a property card (another JPanel which handles its individual visual)
                        if (optional.isPresent()) propertyCard = new PropertyCard(optional.get(), Quadrant.RIGHT); // takes a quadrant enum
                        break;
                    }

                    default: { // bottom
                        x = j;
                        y = 10; // bottom will have a y-axis of 10

                        final int finalX = x, finalY = y; // finalize to use in optional stream

                        // filter the properties by which one contains the coordinates
                        Optional<Property> optional = getProperties().stream()
                                .filter(property -> property.contains(finalX, finalY)).findFirst();

                        // if present, create a property card (another JPanel which handles its individual visual)
                        if (optional.isPresent()) propertyCard = new PropertyCard(optional.get(), Quadrant.BOTTOM); // takes a quadrant enum
                        break;
                    }
                }

                if (propertyCard != null) { // if a property card exists through this iteration

                    // create a new constraint so spacing and weight is correct
                    final GridBagConstraints constraints = new GridBagConstraints(x, y, 1, 1, 0, 0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0);
                    getContentPane().add(propertyCard, constraints); // add to the content pane
                    propertyCard.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // outlines the JPanel with a black border
                }
            }
        }

        this.center = new BoardCenter(this, getContentPane().getWidth(), getContentPane().getHeight());
        getContentPane().add(center, new GridBagConstraints(0, 0, 0, 0,
                0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));


        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                getCenter().height = e.getComponent().getSize().height;
                getCenter().width = e.getComponent().getSize().width;
                getCenter().update();
            }
        });


        pack(); // pack the frame

        // set the frame size & set its minimum dimensions/size to that same value to keep things clean
        setSize(gameInstance.getEnteredSize(), gameInstance.getEnteredSize());
        setPreferredSize(new Dimension(gameInstance.getEnteredSize(), gameInstance.getEnteredSize()));
        setMinimumSize(new Dimension(getWidth(), getHeight()));
        setVisible(true); // make the frame visible
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);

        final Graphics2D g2 = (Graphics2D) g.create(); // 2d graphics

        if (getSelectedProperty() != null) { // check if a property card is currently selected

        }

        /*if (getCursorPosition() != null && getPropertyMouseOver() != null) { // check if property card is being hovered on
            g2.setColor(Color.WHITE); // set color to white

            if (getPropertyMouseOver().getQuadrant() == Quadrant.LEFT) { // if left side of board
                final int x = (int) Math.max(getCursorPosition().x, getPropertyMouseOver().getWidth() + (getPropertyMouseOver().getWidth() * 0.15)),
                        y = (int) Math.min(Math.max(getPropertyMouseOver().getY(), (getPropertyMouseOver().getWidth() + (getPropertyMouseOver().getWidth() * 0.33))),
                                getContentPane().getHeight() - ((getPropertyMouseOver().getWidth() * 3) - (getPropertyMouseOver().getWidth() * 0.94))),
                        width = (getWidth() / 4), height = (getHeight() / 7);

                tooltipHelper(g2, x, y, width, height);

                // draws the name of the property at the top of the tooltip
                g2.drawString(getPropertyMouseOver().getProperty().getName(), (int) (x + (x * 0.08)), (int) (y + (x * 0.15)));

                if (getPropertyMouseOver().getProperty().getValue() != 0) // if the property cost is not 0 then the cost will be drawn
                    g2.drawString("Cost: " + getPropertyMouseOver().getProperty().getValue(), (int) (x + (x * 0.08)), (int) (y + (x * 0.3)));
            } else if (getPropertyMouseOver().getQuadrant() == Quadrant.RIGHT) { // if right side of board
                final int x = (int) Math.max(getCursorPosition().x, (getContentPane().getWidth() - (getPropertyMouseOver().getWidth() * 3) - (getWidth() * 0.028))),
                        y = (int) Math.min(Math.max(getPropertyMouseOver().getY(), (getPropertyMouseOver().getWidth() + (getPropertyMouseOver().getWidth() * 0.33))),
                                getContentPane().getHeight() - ((getPropertyMouseOver().getWidth() * 3) - (getPropertyMouseOver().getWidth() * 0.94))),
                        width = (getWidth() / 4), height = (getHeight() / 7);

                tooltipHelper(g2, x, y, width, height);

                // draws the name of the property at the top of the tooltip
                g2.drawString(getPropertyMouseOver().getProperty().getName(), (int) (x + (x * 0.018)), (int) (y + (x * 0.035)));

                if (getPropertyMouseOver().getProperty().getValue() != 0)
                    g2.drawString("Cost: " + getPropertyMouseOver().getProperty().getValue(), (int) (x + (x * 0.018)), (int) (y + (x * 0.07)));
            } else if (getPropertyMouseOver().getQuadrant() == Quadrant.TOP) { // if top of board
                final int x = (int) Math.min(Math.max(getPropertyMouseOver().getX(), (getPropertyMouseOver().getHeight() + (getPropertyMouseOver().getHeight() * 0.2))),
                        (getContentPane().getWidth() - ((getPropertyMouseOver().getHeight() * 3) + (getPropertyMouseOver().getHeight() * 0.35)))),
                        y = (int) (getPropertyMouseOver().getHeight() + (getPropertyMouseOver().getHeight() * 0.35)),
                        width = (getWidth() / 4), height = (getHeight() / 7);

                tooltipHelper(g2, x, y, width, height);

                // draws the name of the property at the top of the tooltip
                g2.drawString(getPropertyMouseOver().getProperty().getName(), (int) (x + (y * 0.08)), (int) (y + (y * 0.18)));

                if (getPropertyMouseOver().getProperty().getValue() != 0) // if the property cost is not 0 then the cost will be drawn
                    g2.drawString("Cost: " + getPropertyMouseOver().getProperty().getValue(), (int) (x + (y * 0.08)), (int) (y + (y * 0.3)));
            } else if (getPropertyMouseOver().getQuadrant() == Quadrant.BOTTOM) { // if bottom of board
                final int x = (int) Math.min(Math.max(getPropertyMouseOver().getX(), (getPropertyMouseOver().getHeight() + (getPropertyMouseOver().getHeight() * 0.2))),
                        (getContentPane().getWidth() - ((getPropertyMouseOver().getHeight() * 3) + (getPropertyMouseOver().getHeight() * 0.35)))),
                        y = (int) (getContentPane().getHeight() - ((getPropertyMouseOver().getHeight() * 2) + (getPropertyMouseOver().getHeight() * 0.1))),
                        width = (getWidth() / 4), height = (getHeight() / 7);

                tooltipHelper(g2, x, y, width, height); // draw the tooltip box and setup font, etc.

                // draws the name of the property at the top of the tooltip
                g2.drawString(getPropertyMouseOver().getProperty().getName(), (int) (x + (y * 0.015)), (int) (y + (y * 0.03)));

                if (getPropertyMouseOver().getProperty().getValue() != 0) // if the property cost is not 0 then the cost will be drawn
                    g2.drawString("Cost: " + getPropertyMouseOver().getProperty().getValue(), (int) (x + (y * 0.015)), (int) (y + (y * 0.06)));
            }

            g2.dispose(); // dispose changes to graphics
        }*/

        g2.dispose(); // dispose changes to graphics
    }

    private void tooltipHelper(Graphics2D g2, int x, int y, int width, int height) {
        g2.fillRoundRect(x, y, width, height, 20, 20); // draw a nice white box

        // set the color to that of the group the property belongs to
        // g2.setColor(getPropertyMouseOver().getProperty().getGroup().getColor());
        g2.drawRect(x, y, width, height); // draw a nice hollow box with the colored border

        // set color to black & create a new font then set it
        g2.setColor(Color.BLACK);
        final Font font = new Font("Arial Black", Font.BOLD, 14);
        g2.setFont(font);
    }

    private void setupProperties() throws InsufficientResourcesException {

        /*
         * For each property it is first created given a name, group, value/cost, and an array of positions
         * given a simple position (0-39) and its actual layout coordinates.
         */

        // corners
        getProperties().add(new Property("GO", Property.Group.NONE, 0, new Position(0, 0, 10)));
        getProperties().add(new Property("IN JAIL/JUST VISITING", Property.Group.NONE, 0, new Position(10, 0, 0)));
        getProperties().add(new Property("FREE PARKING", Property.Group.NONE, 0, new Position(20, 10, 0)));
        getProperties().add(new Property("GO TO JAIL", Property.Group.NONE, 0, new Position(30, 10, 10)));

        // chance & community chest
        getProperties().add(new Property("COMMUNITY CHEST", Property.Group.NONE, 0, new Position(2, 0, 8),
                new Position(17, 7, 0), new Position(33, 7, 10)));
        getProperties().add(new Property("CHANCE", Property.Group.NONE, 0, new Position(7, 0, 3),
                new Position(22, 10, 2), new Position(36, 4, 10)));

        // tax properties
        getProperties().add(new Property("INCOME TAX", Property.Group.NONE, 200, new Position(4, 0, 6)));
        getProperties().add(new Property("LUXURY TAX", Property.Group.NONE, 100, new Position(38, 2, 10)));

        // utility properties
        getProperties().add(new Property("ELECTRIC COMPANY", Property.Group.UTILITIES, 150, new Position(12, 2, 0)));
        getProperties().add(new Property("WATER WORKS", Property.Group.UTILITIES, 150, new Position(28, 10, 8)));

        // railroad-station properties
        getProperties().add(new Property("READING RAILROAD", Property.Group.STATIONS, 200, new Position(5, 0, 5)));
        getProperties().add(new Property("PENNSYLVANIA RAILROAD", Property.Group.STATIONS, 200, new Position(15, 5, 0)));
        getProperties().add(new Property("B & O. RAILROAD", Property.Group.STATIONS, 200, new Position(25, 10, 5)));
        getProperties().add(new Property("SHORT LINE", Property.Group.STATIONS, 200, new Position(35, 5, 10)));

        // brown properties
        getProperties().add(new Property("MEDITERRANEAN AVENUE", Property.Group.BROWN, 60, new Position(1, 0, 9)));
        getProperties().add(new Property("BALTIC AVENUE", Property.Group.BROWN, 60, new Position(3, 0, 7)));

        // light-blue properties
        getProperties().add(new Property("ORIENTAL AVENUE", Property.Group.LIGHT_BLUE, 100, new Position(6, 0, 4)));
        getProperties().add(new Property("VERMONT AVENUE", Property.Group.LIGHT_BLUE, 100, new Position(8, 0, 2)));
        getProperties().add(new Property("CONNECTICUT AVENUE", Property.Group.LIGHT_BLUE, 120, new Position(9, 0, 1)));

        // magenta/pink properties
        getProperties().add(new Property("ST. CHARLES PLACE", Property.Group.PINK, 140, new Position(11, 1, 0)));
        getProperties().add(new Property("STATES AVENUE", Property.Group.PINK, 140, new Position(13, 3, 0)));
        getProperties().add(new Property("VIRGINIA AVENUE", Property.Group.PINK, 160, new Position(14, 4, 0)));

        // orange properties
        getProperties().add(new Property("ST. JAMES PLACE", Property.Group.ORANGE, 180, new Position(16, 6, 0)));
        getProperties().add(new Property("TENNESSEE AVENUE", Property.Group.ORANGE, 180, new Position(18, 8, 0)));
        getProperties().add(new Property("NEW YORK AVENUE", Property.Group.ORANGE, 200, new Position(19, 9, 0)));

        // red properties
        getProperties().add(new Property("KENTUCKY AVENUE", Property.Group.RED, 220, new Position(21, 10, 1)));
        getProperties().add(new Property("INDIANA AVENUE", Property.Group.RED, 220, new Position(23, 10, 3)));
        getProperties().add(new Property("ILLINOIS AVENUE", Property.Group.RED, 240, new Position(24, 10, 4)));

        // yellow properties
        getProperties().add(new Property("ATLANTIC AVENUE", Property.Group.YELLOW, 260, new Position(26, 10, 6)));
        getProperties().add(new Property("VENTNOR AVENUE", Property.Group.YELLOW, 260, new Position(27, 10, 7)));
        getProperties().add(new Property("MARVIN GARDENS", Property.Group.YELLOW, 280, new Position(29, 10, 9)));

        // green properties
        getProperties().add(new Property("PACIFIC AVENUE", Property.Group.GREEN, 300, new Position(31, 9, 10)));
        getProperties().add(new Property("NORTH CAROLINA AVENUE", Property.Group.GREEN, 300, new Position(32, 8, 10)));
        getProperties().add(new Property("PENNSYLVANIA AVENUE", Property.Group.GREEN, 320, new Position(34, 6, 10)));

        // dark-blue properties
        getProperties().add(new Property("PARK PLACE", Property.Group.DARK_BLUE, 350, new Position(37, 3, 10)));
        getProperties().add(new Property("BOARDWALK", Property.Group.DARK_BLUE, 400, new Position(39, 1, 10)));
    }

    /**
     * @param position The position to filter by, can be either 0-39 or the layout coordinates x: <0-10>, y: <0-10>.
     * @return The property at the defined position, CAN RETURN NULL.
     */
    public Property getByPosition(int... position) {
        if (position.length == 1) { // if a simple one integer coord is passed
            for (Property property : getProperties())
                for (Position pos : property.getPositions())
                    if (pos.getPosition() == position[0]) // compare position
                        return property;
        } else if (position.length == 2) { // if a two integer coord is passed
            for (Property property : getProperties())
                for (Position pos : property.getPositions())
                    if (pos.getX() == position[0] && pos.getY() == position[1]) // compare x & y
                        return property;
        }

        return null; // position out of bounds or no property with coords
    }

    /**
     * @param group The group to filter properties by.
     * @return The list of properties in the defined group.
     */
    public Collection<Property> getProperties(Property.Group group) {
        return getProperties().stream().filter(property -> property.getGroup() == group).collect(Collectors.toList());
    }

    // getters & setters

    /**
     * @return The list of properties.
     */
    public ArrayList<Property> getProperties() {return properties;}

    /**
     * @return The property card currently selected.
     */
    public PropertyCard getSelectedProperty() {return selectedProperty;}

    /**
     * @param property The property card to set as selected.
     */
    public void setSelectedProperty(PropertyCard property) {this.selectedProperty = property;}

    /**
     * @return The panel in the center of the board.
     */
    public BoardCenter getCenter() {return center;}

    /**
     * Simple enumeration for the 4 quadrants of the board.
     */
    public enum Quadrant {LEFT, TOP, RIGHT, BOTTOM}

}