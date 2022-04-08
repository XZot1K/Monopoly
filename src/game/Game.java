package game;

import game.components.entity.Token;
import game.components.gui.Board;

import javax.naming.InsufficientResourcesException;
import javax.swing.*;
import java.util.ArrayList;

public class Game {

    public static Game INSTANCE;

    private final ArrayList<Token> players;
    private Board board;

    public Game() {
        INSTANCE = this;
        players = new ArrayList<>();

        try {
            board = new Board(INSTANCE, "Monopoly", 1000, 1000, JFrame.EXIT_ON_CLOSE);
            board.revalidate();
        } catch (InsufficientResourcesException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException
                | InstantiationException | IllegalAccessException e) {e.printStackTrace();}

        new Game(); // new game instance
    }

    // getters & setters
    public Board getBoard() {return board;}

    public ArrayList<Token> getPlayers() {return players;}

    public enum State {
        MENU
    }

}