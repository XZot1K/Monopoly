/*
    Jeremiah Osborne
    Date: 4/1/2022
 */

package game.components.property;

public class Position {

    private final int position, // integer between 0 & 39 for a linear simple coordinate
            x, y; // x & y coords for the layout

    public Position(int position, int x, int y) {
        // initialize
        this.position = position;
        this.x = x;
        this.y = y;
    }

    /**
     * @return Integer between 0 & 39 for a linear simple coordinate
     */
    public int getPosition() {return position;}

    /**
     * @return The 'x' coordinate for the layout.
     */
    public int getX() {return x;}

    /**
     * @return The 'y' coordinate for the layout.
     */
    public int getY() {return y;}

}