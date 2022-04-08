package game.components.gui;

import game.Game;
import game.components.property.Position;
import game.components.property.Property;
import game.components.property.PropertyCard;

import javax.naming.InsufficientResourcesException;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;

public class Board extends JFrame {

    private final Game INSTANCE;
    private final ArrayList<Property> properties;

    private Point cursorPosition;
    private PropertyCard propertyMouseOver;

    public Board(Game gameInstance, String title, int width, int height, int closeOperation)
            throws InsufficientResourcesException {
        INSTANCE = gameInstance;
        properties = new ArrayList<>();
        setupProperties();

        setTitle(title);
        setDefaultCloseOperation(closeOperation);
        setLayout(null);

        final Color greenBackground = new Color(144, 238, 144);
        getContentPane().setBackground(greenBackground);

        final GridBagLayout layout = new GridBagLayout();
        final double cornerWeight = 0.25, sideWeight = 0.18;

        layout.rowWeights = new double[]{cornerWeight, sideWeight, sideWeight, sideWeight, sideWeight,
                sideWeight, sideWeight, sideWeight, sideWeight, sideWeight, cornerWeight};
        layout.columnWeights = new double[]{cornerWeight, sideWeight, sideWeight, sideWeight, sideWeight,
                sideWeight, sideWeight, sideWeight, sideWeight, sideWeight, cornerWeight};

        getContentPane().setLayout(layout);

        int x, y; // default grid coords

        // creating panels on each side

        final Insets insets = new Insets(0, 0, 0, 0);
        for (int i = -1; ++i < 4; ) {
            for (int j = -1; ++j < 11; ) {
                PropertyCard propertyCard = null; // new property card
                switch (i) {

                    case 0: { // top
                        x = j;
                        y = 0;

                        final int finalX = x, finalY = y;
                        Optional<Property> optional = getProperties().stream()
                                .filter(property -> property.contains(finalX, finalY)).findFirst();
                        if (optional.isPresent()) propertyCard = new PropertyCard(optional.get(), Quadrant.TOP);
                        break;
                    }

                    case 1: { // left
                        x = 0;
                        y = j;

                        final int finalX = x, finalY = y;
                        Optional<Property> optional = getProperties().stream()
                                .filter(property -> property.contains(finalX, finalY)).findFirst();
                        if (optional.isPresent()) propertyCard = new PropertyCard(optional.get(), Quadrant.LEFT);

                        break;
                    }

                    case 2: { // right
                        x = 10;
                        y = j;

                        final int finalX = x, finalY = y;
                        Optional<Property> optional = getProperties().stream()
                                .filter(property -> property.contains(finalX, finalY)).findFirst();
                        if (optional.isPresent()) propertyCard = new PropertyCard(optional.get(), Quadrant.RIGHT);
                        break;
                    }

                    default: { // bottom
                        x = j;
                        y = 10;

                        final int finalX = x, finalY = y;
                        Optional<Property> optional = getProperties().stream()
                                .filter(property -> property.contains(finalX, finalY)).findFirst();
                        if (optional.isPresent()) propertyCard = new PropertyCard(optional.get(), Quadrant.BOTTOM);
                        break;
                    }
                }

                if (propertyCard != null) {
                    final GridBagConstraints constraints = new GridBagConstraints(x, y, 1, 1, 0, 0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0);
                    getContentPane().add(propertyCard, constraints);
                    propertyCard.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                }
            }
        }

        // Main Inner Area Notice Starts at (1,1) and takes up 11x11
        // JPanel innerPanel = new JPanel();
        // innerPanel.setBackground(greenBackground);

        //Menu menu = new Menu();

        // getContentPane().add(menu, new GridBagConstraints(2, 2, 7, 7,
        //         0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        //         new Insets(0, 0, 0, 0), 0, 0));

        pack();
        setSize(width, height);
        setMinimumSize(new Dimension(getWidth(), getHeight()));

        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        final Graphics2D g2 = (Graphics2D) g.create();

        if (getCursorPosition() != null) {
            if (getPropertyMouseOver().getQuadrant() == Quadrant.LEFT) {
                g2.setColor(Color.WHITE);

                final int x = (int) Math.max(getCursorPosition().x, getPropertyMouseOver().getWidth() + (getPropertyMouseOver().getWidth() * 0.15)),
                        y = (int) Math.min(Math.max(getPropertyMouseOver().getY(), (getPropertyMouseOver().getWidth() + (getPropertyMouseOver().getWidth() * 0.33))),
                                getContentPane().getHeight() - ((getPropertyMouseOver().getWidth() * 3) - (getPropertyMouseOver().getWidth() * 0.94))),
                        width = (getWidth() / 4), height = (getHeight() / 7);

                g2.fillRoundRect(x, y, width, height, 20, 20);

                g2.setColor(getPropertyMouseOver().getProperty().getGroup().getColor());
                g2.drawRect(x, y, width, height);

                g2.setColor(Color.BLACK);

                final Font font = new Font("Arial Black", Font.BOLD, 14);
                g2.setFont(font);
                g2.drawString(getPropertyMouseOver().getProperty().getName(), (int) (x + (x * 0.08)), (int) (y + (x * 0.15)));

                g2.setFont(font.deriveFont(Font.PLAIN)); // update font to plain
                g2.drawString("Cost: " + getPropertyMouseOver().getProperty().getValue(), (int) (x + (x * 0.08)), (int) (y + (x * 0.3)));
            }
        }
    }

    private void setupProperties() throws InsufficientResourcesException {

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

    // getters & setters
    public ArrayList<Property> getProperties() {return properties;}

    public Point getCursorPosition() {return cursorPosition;}

    public void setCursorPosition(Point cursorPosition) {this.cursorPosition = cursorPosition;}

    public PropertyCard getPropertyMouseOver() {return propertyMouseOver;}

    public void setPropertyMouseOver(PropertyCard propertyMouseOver) {this.propertyMouseOver = propertyMouseOver;}

    public enum Quadrant {LEFT, TOP, RIGHT, BOTTOM}

}