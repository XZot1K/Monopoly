package game.components.property;

import game.Game;
import game.components.gui.Board;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class PropertyCard extends JPanel {

    private final Property property;
    private final Board.Quadrant quadrant;

    public PropertyCard(Property property, Board.Quadrant quadrant) {
        this.property = property;
        this.quadrant = quadrant;
        setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2 = (Graphics2D) g;
        final AffineTransform original = (AffineTransform) g2.getTransform().clone(); // original transform

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (getProperty().getGroup() == Property.Group.NONE || getProperty().isCorner()) {

            if (getProperty().getName().equals("GO")) {
                try {
                    final URL goUrl = Game.class.getResource("/resources/go.png");
                    if (goUrl != null) {
                        g2.scale(0.2, 0.2);

                        g2.rotate(Math.toRadians(180), 0, 0);
                        g2.drawImage(ImageIO.read(goUrl), getWidth(), -getHeight(), null);
                       // g2.scale(11, 11); // reset scale

                       /* g2.setColor(Color.RED);
                        g2.setFont(new Font("Lucida Grande", Font.BOLD, 30));
                        g2.drawString("GO", (getWidth() / 4) - 5, -20);*/


                        // reset transform & scale
                        g2.setTransform(original);
                    }

                } catch (IOException e) {e.printStackTrace();}
            }

            return;
        }

        if (quadrant == Board.Quadrant.LEFT) {
            final int portion = (int) (getWidth() * 0.25),
                    x = (getWidth() - portion);

            g2.setColor(getProperty().getGroup().getColor());
            g2.fillRect(x, 0, portion, getHeight());

            g2.setColor(Color.BLACK);
            g2.drawRect(x, 0, portion, getHeight());

            final AffineTransform affineTransform = new AffineTransform();
            affineTransform.rotate(Math.toRadians(90), 0, 0);

            final Font font = new Font("Arial Black", Font.BOLD, 12);
            g2.setFont(font.deriveFont(affineTransform));

            if (property.getName().contains(" ")) {
                final String[] args = property.getName().split(" ");
                int xOffset = (int) (getWidth() - (getWidth() * 0.5));
                for (String line : args) {
                    g2.drawString(line, xOffset, (int) (getHeight() * 0.15));
                    xOffset -= 20;
                }
            }


        } else if (quadrant == Board.Quadrant.RIGHT) {
            final int portion = (int) (getWidth() * 0.25), x = 0;

            g2.setColor(getProperty().getGroup().getColor());
            g2.fillRect(x, 0, portion, getHeight());

            g2.setColor(Color.BLACK);
            g2.drawRect(x, 0, portion, getHeight());
        } else if (quadrant == Board.Quadrant.TOP) {
            final int portion = (int) (getHeight() * 0.25),
                    y = (getHeight() - portion);

            g2.setColor(getProperty().getGroup().getColor());
            g2.fillRect(0, y, getWidth(), portion);

            g2.setColor(Color.BLACK);
            g2.drawRect(0, y, getWidth(), portion);
        } else if (quadrant == Board.Quadrant.BOTTOM) {
            final int portion = (int) (getHeight() * 0.25), y = 0;

            g2.setColor(getProperty().getGroup().getColor());
            g2.fillRect(0, y, getWidth(), portion);

            g2.setColor(Color.BLACK);
            g2.drawRect(0, y, getWidth(), portion);
        }

        g2.setTransform(original);
    }

    public BufferedImage toImage() {
        BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        print(g2d);
        g2d.dispose();
        return img;
    }

    public Property getProperty() {return property;}

    public Board.Quadrant getQuadrant() {return quadrant;}
}