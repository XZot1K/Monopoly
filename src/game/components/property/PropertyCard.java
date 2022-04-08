package game.components.property;

import game.Game;
import game.components.gui.Board;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class PropertyCard extends JPanel {

    public final PropertyCard CARD_INSTANCE;
    private final Property property;
    private final Board.Quadrant quadrant;

    public PropertyCard(Property property, Board.Quadrant quadrant) {
        this.CARD_INSTANCE = this;
        this.property = property;
        this.quadrant = quadrant;

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                Game.INSTANCE.getBoard().setCursorPosition(e.getPoint());
                Game.INSTANCE.getBoard().setPropertyMouseOver(CARD_INSTANCE);
                Game.INSTANCE.getBoard().repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                Game.INSTANCE.getBoard().setCursorPosition(null);
                Game.INSTANCE.getBoard().setPropertyMouseOver(null);
                Game.INSTANCE.getBoard().repaint();
            }
        });

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

            try {
                if (getProperty().getName().equals("GO")) {
                    final URL url = Game.class.getResource("/resources/corners/go.png");
                    if (url != null) {
                        g2.drawImage(ImageIO.read(url), 0, 0, getWidth(), getHeight(), this);
                    }

                } else if (getProperty().getName().equals("IN JAIL/JUST VISITING")) {
                    final URL url = Game.class.getResource("/resources/corners/jail.png");
                    if (url != null) {
                        g2.drawImage(ImageIO.read(url), 0, 0, getWidth(), getHeight(), this);
                    }
                } else if (getProperty().getName().equals("FREE PARKING")) {
                    final URL url = Game.class.getResource("/resources/corners/parking.png");
                    if (url != null) {
                        g2.drawImage(ImageIO.read(url), 0, 0, getWidth(), getHeight(), this);
                    }
                } else if (getProperty().getName().equals("GO TO JAIL")) {
                    final URL url = Game.class.getResource("/resources/corners/go_to_jail.png");
                    if (url != null) {
                        g2.drawImage(ImageIO.read(url), 0, 0, getWidth(), getHeight(), this);
                    }
                }
            } catch (IOException e) {e.printStackTrace();}
        } else {
            if (quadrant == Board.Quadrant.LEFT) {
                final int portion = (int) (getWidth() * 0.25), x = (getWidth() - portion);

                final AffineTransform affineTransform = new AffineTransform();
                affineTransform.rotate(Math.toRadians(90), 0, 0);

                final Font font = new Font("Arial Black", Font.BOLD, 14);
                g2.setFont(font.deriveFont(affineTransform));
                g2.setColor(Color.BLACK);

                if (getProperty().getGroup() == Property.Group.STATIONS) {
                    try {
                        final URL url = Game.class.getResource("/resources/stations/station_left.png");
                        if (url != null) {
                            g2.scale(0.85, 1);
                            g2.drawImage(ImageIO.read(url), 0, 0, getWidth(), getHeight(), this);
                            g2.setTransform(original);
                        }
                    } catch (IOException e) {e.printStackTrace();}

                    if (property.getName().contains(" ")) {
                        final String[] args = property.getName().split(" ");
                        StringBuilder sb = new StringBuilder();
                        for (String word : args) sb.append(word.charAt(0));
                        g2.drawString(sb.toString(), (int) ((getWidth() / 2) + (getWidth() * 0.35)), (int) ((getHeight() / 2) - (getHeight() * 0.13)));
                    }
                } else if (getProperty().getName().equals("CHANCE")) {
                    try {
                        final URL url = Game.class.getResource("/resources/chance/q-purple.png");
                        if (url != null) {
                            g2.scale(0.3, 0.3);
                            g2.drawImage(ImageIO.read(url), 0, 0, getWidth(), getHeight(), this);
                            g2.setTransform(original);
                        }
                    } catch (IOException e) {e.printStackTrace();}
                    g2.drawString(getProperty().getName(), (int) ((getWidth() / 2) + (getWidth() * 0.35)), (int) ((getHeight() / 2) - (getHeight() * 0.1)));

                } else if (getProperty().getName().equals("COMMUNITY CHEST")) {
                    try {
                        final URL url = Game.class.getResource("/resources/chest.png");
                        if (url != null) {
                            g2.drawImage(ImageIO.read(url), 0, 0, getWidth(), getHeight(), this);
                        }
                    } catch (IOException e) {e.printStackTrace();}
                    g2.drawString(getProperty().getName(), (int) ((getWidth() / 2) + (getWidth() * 0.35)), (int) ((getHeight() / 2) - (getHeight() * 0.1)));

                } else {
                    g2.setColor(getProperty().getGroup().getColor());
                    g2.fillRect(x, 0, portion, getHeight());

                    g2.setColor(Color.BLACK);
                    g2.drawRect(x, 0, portion, getHeight());

                    if (property.getName().contains(" ")) {
                        final String[] args = property.getName().split(" ");
                        StringBuilder sb = new StringBuilder();
                        for (String word : args) sb.append(word.charAt(0));
                        g2.drawString(sb.toString(), getWidth() / 2, (int) ((getHeight() / 2) - (getHeight() * 0.13)));
                    }
                }


            } else if (quadrant == Board.Quadrant.RIGHT) {
                final int portion = (int) (getWidth() * 0.25), x = 0;

                final AffineTransform affineTransform = new AffineTransform();
                affineTransform.rotate(Math.toRadians(270), 0, 0);

                final Font font = new Font("Arial Black", Font.BOLD, 14);
                g2.setFont(font.deriveFont(affineTransform));
                g2.setColor(Color.BLACK);

                if (getProperty().getGroup() == Property.Group.STATIONS) {
                    try {
                        final URL url = Game.class.getResource("/resources/stations/station_right.png");
                        if (url != null) {
                            g2.scale(0.85, 1);
                            g2.drawImage(ImageIO.read(url), 0, 0, getWidth(), getHeight(), this);
                            g2.setTransform(original);
                        }
                    } catch (IOException e) {e.printStackTrace();}

                    if (property.getName().contains(" ")) {
                        final String[] args = property.getName().split(" ");
                        StringBuilder sb = new StringBuilder();
                        for (String word : args) sb.append(word.charAt(0));
                        g2.drawString(sb.toString(), (int) ((getWidth() / 2) + (getWidth() * 0.35)), (int) ((getHeight() / 2) - (getHeight() * 0.13)));
                    }
                } else {
                    g2.setColor(getProperty().getGroup().getColor());
                    g2.fillRect(x, 0, portion, getHeight());

                    g2.setColor(Color.BLACK);
                    g2.drawRect(x, 0, portion, getHeight());
                }
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