package game;

import game.components.gui.Board;
import game.components.property.Property;

import javax.naming.InsufficientResourcesException;
import javax.swing.*;
import java.util.List;

public class Game {

    private static Board BOARD;

    public static void main(String[] args) {
        try {
            BOARD = new Board("Monopoly", 1000, 1000, JFrame.EXIT_ON_CLOSE);
            BOARD.revalidate();
        } catch (InsufficientResourcesException e) {
            e.printStackTrace();
        }


    }

    public static enum State {
        MENU
    }

}