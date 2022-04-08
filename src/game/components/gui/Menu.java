package game.components.gui;

import javax.swing.*;
import java.awt.*;

public class Menu extends JPanel {

    private boolean closable;
    private final GroupLayout layout;

    public Menu(String buttonCompletion) {
        setClosable(true);

        layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

    }

    public Menu create() {
        setLayout(layout);
        setVisible(true);
        return this;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(Color.GRAY);
        g2.fillRect(0, 0, getWidth(), getHeight());

       /* if (isClosable()) { // if closable, paint the 'X' button
            final int closeButtonX = (int) (getWidth() - (getWidth() * 0.05));
            g2.setColor(Color.RED);
            g2.fillRect(closeButtonX, 1, (int) (getWidth() * 0.05), (int) (getHeight() * 0.05));

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Lucida Grande", Font.BOLD, );
            g2.drawString("X", closeButtonX + (int) (getWidth() * 0.0116), (int) (getHeight() * 0.044));
        } */
    }

    public boolean isClosable() {return closable;}

    public Menu setClosable(boolean closable) {
        this.closable = closable;
        return this;
    }

}