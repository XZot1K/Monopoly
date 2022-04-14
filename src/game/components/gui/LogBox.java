package game.components.gui;

import javax.swing.*;
import java.awt.*;

public class LogBox extends JTextArea {

    private final JScrollPane scrollPane;

    public LogBox() {
        super(10, 20);

        setLineWrap(true);
        setWrapStyleWord(true);
        setEditable(false);
        setBackground(Color.WHITE);
        setMargin(new Insets(10, 10, 10, 10));
        setText("Welcome to Monopoly!");

        scrollPane = new JScrollPane(this);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
        scrollPane.setViewportBorder(null);
        scrollPane.setVisible(true);

        setVisible(true);
    }

    public JScrollPane getScrollPane() {return scrollPane;}

}