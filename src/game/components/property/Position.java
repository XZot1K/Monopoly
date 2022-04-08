package game.components.property;

public class Position {

    private final int position, x, y;

    public Position(int position, int x, int y) {
        this.position = position;
        this.x = x;
        this.y = y;
    }

    public int getPosition() {return position;}

    public int getX() {return x;}

    public int getY() {return y;}

}