package game.components.gui;

import javax.swing.*;
import java.awt.*;

public class LogBox extends JTextArea {

    private final JScrollPane scrollPane; // the scroll bar

    public LogBox() {
        super(10, 20); // call upon parent constructor for sizes

        // setup some fanciness
        setLineWrap(true);
        setWrapStyleWord(true);
        setEditable(false);
        setBackground(Color.WHITE);
        setMargin(new Insets(10, 10, 10, 10));
        setText("Welcome to Monopoly!");

        // formulate the scroll pane
        scrollPane = new JScrollPane(this);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
        scrollPane.setViewportBorder(null);
        scrollPane.setVisible(true);

        // make visible
        setVisible(true);
    }

    /**
     * @return The scroll bar.
     */
    public JScrollPane getScrollPane() {return scrollPane;}

}