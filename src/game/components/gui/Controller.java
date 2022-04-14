package game.components.gui;

import game.components.gui.board.BoardCenter;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Controller extends JPanel {

    private final BoardCenter center;
    private final HashMap<String, JButton> buttonMap;

    public Controller(BoardCenter center) {
        this.center = center;
        this.buttonMap = new HashMap<>();

        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new GridLayout(4, 4, 10, 10));


        setVisible(true);
    }

    public JButton getButton(String id) {return buttonMap.getOrDefault(id.toLowerCase(), null);}

}