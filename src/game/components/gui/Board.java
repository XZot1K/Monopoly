package game.components.gui;

import game.components.property.Property;
import game.components.property.PropertyCard;

import javax.naming.InsufficientResourcesException;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;

public class Board extends JFrame {

    private final ArrayList<Property> properties;
    private Menu currentMenu;

    public Board(String title, int width, int height, int closeOperation) throws InsufficientResourcesException {
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

        Menu menu = new Menu();

        getContentPane().add(menu, new GridBagConstraints(2, 2, 7, 7,
                0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        pack();
        setSize(width, height);
        setMinimumSize(new Dimension(getWidth(), getHeight()));

        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        final Graphics2D g2 = (Graphics2D) g.create();



    }

    private void setupProperties() throws InsufficientResourcesException {

        getProperties().add(new Property("GO", Property.Group.NONE, 0, 0, 10));
        getProperties().add(new Property("IN JAIL/JUST VISITING", Property.Group.NONE, 0, 0, 0));
        getProperties().add(new Property("FREE PARKING", Property.Group.NONE, 0, 10, 0));
        getProperties().add(new Property("GO TO JAIL", Property.Group.NONE, 0, 10, 10));

        getProperties().add(new Property("COMMUNITY CHEST", Property.Group.NONE, 0, 0, 8, 7, 0, 7, 10));
        getProperties().add(new Property("CHANCE", Property.Group.NONE, 0, 0, 3, 10, 2, 4, 10));

        // tax properties
        getProperties().add(new Property("INCOME TAX", Property.Group.NONE, 200, 0, 6));
        getProperties().add(new Property("LUXURY TAX", Property.Group.NONE, 100, 2, 10));

        // utility properties
        getProperties().add(new Property("ELECTRIC COMPANY", Property.Group.UTILITIES, 150, 2, 0));
        getProperties().add(new Property("WATER WORKS", Property.Group.UTILITIES, 150, 10, 8));

        // railroad-station properties
        getProperties().add(new Property("READING RAILROAD", Property.Group.STATIONS, 200, 0, 5));
        getProperties().add(new Property("PENNSYLVANIA RAILROAD", Property.Group.STATIONS, 200, 5, 0));
        getProperties().add(new Property("B & O. RAILROAD", Property.Group.STATIONS, 200, 10, 5));
        getProperties().add(new Property("SHORT LINE", Property.Group.STATIONS, 200, 5, 10));

        // brown properties
        getProperties().add(new Property("MEDITERRANEAN AVENUE", Property.Group.BROWN, 60, 0, 9));
        getProperties().add(new Property("BALTIC AVENUE", Property.Group.BROWN, 60, 0, 7));

        // light-blue properties
        getProperties().add(new Property("ORIENTAL AVENUE", Property.Group.LIGHT_BLUE, 100, 0, 4));
        getProperties().add(new Property("VERMONT AVENUE", Property.Group.LIGHT_BLUE, 100, 0, 2));
        getProperties().add(new Property("CONNECTICUT AVENUE", Property.Group.LIGHT_BLUE, 120, 0, 1));

        // magenta/pink properties
        getProperties().add(new Property("ST. CHARLES PLACE", Property.Group.PINK, 140, 1, 0));
        getProperties().add(new Property("STATES AVENUE", Property.Group.PINK, 140, 3, 0));
        getProperties().add(new Property("VIRGINIA AVENUE", Property.Group.PINK, 160, 4, 0));

        // orange properties
        getProperties().add(new Property("ST. JAMES PLACE", Property.Group.ORANGE, 180, 6, 0));
        getProperties().add(new Property("TENNESSEE AVENUE", Property.Group.ORANGE, 180, 8, 0));
        getProperties().add(new Property("NEW YORK AVENUE", Property.Group.ORANGE, 200, 9, 0));

        // red properties
        getProperties().add(new Property("KENTUCKY AVENUE", Property.Group.RED, 220, 10, 1));
        getProperties().add(new Property("INDIANA AVENUE", Property.Group.RED, 220, 10, 3));
        getProperties().add(new Property("ILLINOIS AVENUE", Property.Group.RED, 240, 10, 4));

        // yellow properties
        getProperties().add(new Property("ATLANTIC AVENUE", Property.Group.YELLOW, 260, 10, 6));
        getProperties().add(new Property("VENTNOR AVENUE", Property.Group.YELLOW, 260, 10, 7));
        getProperties().add(new Property("MARVIN GARDENS", Property.Group.YELLOW, 280, 10, 9));

        // green properties
        getProperties().add(new Property("PACIFIC AVENUE", Property.Group.GREEN, 300, 9, 10));
        getProperties().add(new Property("NORTH CAROLINA AVENUE", Property.Group.GREEN, 300, 8, 10));
        getProperties().add(new Property("PENNSYLVANIA AVENUE", Property.Group.GREEN, 320, 6, 10));

        // dark-blue properties
        getProperties().add(new Property("PARK PLACE", Property.Group.DARK_BLUE, 350, 3, 10));
        getProperties().add(new Property("BOARDWALK", Property.Group.DARK_BLUE, 400, 1, 10));
    }

    // getters & setters
    public ArrayList<Property> getProperties() {return properties;}

    public Menu getCurrentMenu() {return currentMenu;}

    public void setCurrentMenu(Menu currentMenu) {this.currentMenu = currentMenu;}

    public enum Quadrant {
        LEFT, TOP, RIGHT, BOTTOM;

        public static Quadrant getQuadrant(int position) {
            if (position >= 0 && position < 10) return LEFT;
            else if (position >= 10 && position < 20) return TOP;
            else if (position >= 20 && position < 30) return RIGHT;
            else return BOTTOM;
        }

        public static boolean isCornerPosition(int position) {return (position == 0 || position == 10 || position == 20 || position == 30);}
    }

}