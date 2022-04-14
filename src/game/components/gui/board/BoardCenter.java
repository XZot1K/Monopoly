package game.components.gui.board;

import game.Game;
import game.components.gui.LogBox;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class BoardCenter extends JPanel {

    private final Board board;
    private final LogBox logBox;
    public int width, height;


    public BoardCenter(Board board, int width, int height) {
        this.board = board;
        setBackground(new Color(144, 238, 144)); // a nice green

        this.width = width;
        this.height = height;

        setLayout(new GridLayout(2, 2, (int) (width * 0.15), (int) (height * 0.3)));

        add(new JLabel(), 0);
        add(new JLabel(), 1);
        add((logBox = new LogBox()).getScrollPane(), 2);

        update();
        setVisible(true);
    }

    public void update() {
        GridLayout layout = (GridLayout) getLayout();
        layout.setHgap((int) (width * 0.15));
        layout.setVgap((int) (height * 0.3));
        setBorder(BorderFactory.createEmptyBorder((int) (height * 0.12), (int) (width * 0.12),
                (int) (height * 0.12), (int) (width * 0.12)));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2 = (Graphics2D) g.create(); // 2d graphics
        final AffineTransform original = (AffineTransform) g2.getTransform().clone(); // original transform

        try {
            final URL url = Game.class.getResource("/resources/logo.png"); // url to the logo image file (inside the JAR)
            if (url != null) { // not null
                /*
                 * scale the image down by half its original size, draw it based on the panel's size and position /w the observer as the frame,
                 * and then reset the transform to the original state.
                 */

                final BufferedImage image = ImageIO.read(url);

                g2.scale(0.5, 0.5);
                g2.drawImage(image, getWidth() / 2, getHeight() / 2, image.getWidth(), image.getHeight(), this);
                g2.setTransform(original);
            }
        } catch (IOException e) {e.printStackTrace();} // the image wasn't found and an error occurred, shouldn't happen so print stack trace

    }

    public LogBox getLogBox() {return logBox;}

}