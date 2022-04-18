/*
    Jeremiah Osborne
    Date: 4/1/2022
 */

package game.components.gui.board;

import game.Game;
import game.components.property.Property;
import game.components.property.PropertyCard;

import javax.naming.InsufficientResourcesException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;
import java.util.Optional;

public class Board extends JFrame {

    private final Game INSTANCE; // game instance
    private final BoardCenter center; // the panel in the center of the board

    public Board(Game gameInstance) throws InsufficientResourcesException {
        INSTANCE = gameInstance; // game instance pass-through

        setSize(gameInstance.getEnteredSize(), gameInstance.getEnteredSize());
        setPreferredSize(new Dimension(gameInstance.getEnteredSize(), gameInstance.getEnteredSize()));
        setMinimumSize(new Dimension(getContentPane().getWidth(), getContentPane().getHeight()));

        setTitle("Monopoly - Turn 1"); // update title of frame
        setDefaultCloseOperation(EXIT_ON_CLOSE); // exit on close
        setLayout(null); // set the JFrame's layout to null as an initializer (will be updated later anyway)

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocationRelativeTo(null);
        setLocation((int) ((screenSize.getWidth() / 2) - (getPreferredSize().getWidth() / 2)),
                (int) ((screenSize.getHeight() / 2) - (getPreferredSize().getHeight() / 2)));

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
                        Optional<Property> optional = INSTANCE.getProperties().stream()
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
                        Optional<Property> optional = INSTANCE.getProperties().stream()
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
                        Optional<Property> optional = INSTANCE.getProperties().stream()
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
                        Optional<Property> optional = INSTANCE.getProperties().stream()
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

        this.center = new BoardCenter(getContentPane().getWidth(), getContentPane().getHeight());
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

       /* final Graphics2D g2 = (Graphics2D) g.create(); // 2d graphics

        g2.dispose(); // dispose changes to graphics*/
    }

    private void tooltipHelper(Graphics2D g2, int x, int y, int width, int height) {
        g2.fillRoundRect(x, y, width, height, 20, 20); // draw a nice white box

      /*  // set the color to that of the group the property belongs to
        // g2.setColor(getPropertyMouseOver().getProperty().getGroup().getColor());
        g2.drawRect(x, y, width, height); // draw a nice hollow box with the colored border

        // set color to black & create a new font then set it
        g2.setColor(Color.BLACK);
        final Font font = new Font("Arial Black", Font.BOLD, 14);
        g2.setFont(font);*/
    }

    /**
     * @return The panel in the center of the board.
     */
    public BoardCenter getCenter() {return center;}

    /**
     * Simple enumeration for the 4 quadrants of the board.
     */
    public enum Quadrant {LEFT, TOP, RIGHT, BOTTOM}

}