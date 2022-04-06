package game.components.gui;

import javax.swing.*;
import java.awt.*;

public class Menu extends JPanel {

    private boolean closable;

    public Menu() {
        setClosable(true);
        setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(Color.GRAY);
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (isClosable()) { // if closable, paint the 'X' button
            final int closeButtonX = (int) (getWidth() - (getWidth() * 0.05));
            g2.setColor(Color.RED);
            g2.fillRect(closeButtonX, 1, (int) (getWidth() * 0.05), (int) (getHeight() * 0.05));

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Lucida Grande", Font.BOLD, 30));
            g2.drawString("X", closeButtonX + (int) (getWidth() * 0.0116), 28);
        }
    }

    public boolean isClosable() {return closable;}

    public void setClosable(boolean closable) {this.closable = closable;}

}