/*
    Jeremiah Osborne
    Date: 4/1/2022
 */

package game;

import game.components.entity.Token;
import game.components.gui.Menu;
import game.components.gui.board.Board;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;

public class Game {

    public static Game INSTANCE; // global instance so it can be referred at any time

    private final ArrayList<? extends Token> players; // list of players (Token)
    private Board board; // the current game board

    private int enteredSize;

    public Game(int size) { // constructor
        INSTANCE = this; // initialize the static instance of the game
        players = new ArrayList<>(); // initialize the players list

        setEnteredSize(size);
        new Menu(this); // new menu frame
    }

    public static void main(String[] args) {

        // sets the look & feel of the game to that of the system default, if the class can be acuired
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
                 | IllegalAccessException e) {e.printStackTrace();} // print the stacktrace

        int size = 1000;

        if (args.length > 0) // if there were args
            try {
                size = Integer.parseUnsignedInt(args[0]); // assuming only one argument & that it will always be positive
            } catch (Exception e) {
                System.out.println("Error: The passed arg was not a valid integer, launching with defaults...");
            }

        new Game(size); // new game instance (initializes without creating static references)
    }

    public Image getIcon() {
        URL logo = Game.class.getResource("/resources/tokens/thimble.png");
        if (logo != null) return new ImageIcon(logo).getImage();
        return null;
    }

    // getters & setters

    /**
     * @return The current game board instance.
     */
    public Board getBoard() {return board;}

    public void setBoard(Board board) {this.board = board;}

    /**
     * @return The list of players (any object extending the token class).
     */
    public ArrayList<? extends Token> getPlayers() {return players;}

    public int getEnteredSize() {return enteredSize;}

    public void setEnteredSize(int enteredSize) {this.enteredSize = enteredSize;}

}