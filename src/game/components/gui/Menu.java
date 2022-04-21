package game.components.gui;

import game.Game;
import game.components.entity.Token;
import game.components.gui.board.Board;
import game.components.property.Property;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;

public class Menu extends JFrame {

    private final Menu MENU_INSTANCE; // this instance

    public Menu() {
        this.MENU_INSTANCE = this;

        // initialize values
        setTitle("Monopoly - Main Menu");
        setSize(500, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(getWidth(), getHeight()));
        setIconImage(Game.INSTANCE.getIcon());

        // center the window on screen
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocationRelativeTo(null);
        setLocation((int) ((screenSize.getWidth() / 2) - (getPreferredSize().getWidth() / 2)),
                (int) ((screenSize.getHeight() / 2) - (getPreferredSize().getHeight() / 2)));

        // new layout
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        final Color greenBackground = new Color(144, 238, 144); // a nice green
        getContentPane().setBackground(greenBackground); // update the content pane's background color

        // spacer from the top
        final Component spacer = Box.createRigidArea(new Dimension(0, (int) (getHeight() * 0.35)));
        add(spacer);

        // new font
        final Font font = new Font("Arial Black", Font.BOLD, 14);

        // form the load button and its action
        JButton loadButton = new JButton("Load");
        loadButton.setFont(font);
        loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadButton.setMaximumSize(new Dimension((int) (getWidth() * 0.55), (int) (getHeight() * 0.17)));
        loadButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // open file chooser
                JFileChooser fileChooser = new JFileChooser(new File("/"));
                fileChooser.setDialogTitle("Select the Monopoly save: ");

                // make only .monopoly extensions visible
                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {return (!f.isDirectory() && f.getName().toLowerCase().endsWith(".monopoly"));}

                    @Override
                    public String getDescription() {return "*.monopoly";}
                });

                // check if the load button is selected
                if (fileChooser.showDialog(MENU_INSTANCE, "Load") == JFileChooser.APPROVE_OPTION) {

                    final File file = fileChooser.getSelectedFile();
                    if (file == null || !file.exists() || !file.getName().toLowerCase().endsWith(".monopoly")) // check if the file is a monopoly save
                    {
                        // open error message that it wasn't
                        JOptionPane.showMessageDialog(MENU_INSTANCE, "The selected save was unable to be read.",
                                "Loading Save Failed", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String logText = null; // storage for log text

                    try (Scanner scanner = new Scanner(file)) { // new scanner

                        int counter = 0; // line counter
                        while (scanner.hasNextLine()) { // until there are no more lines
                            counter++;
                            final String dataLine = scanner.nextLine(); // get line text
                            if (dataLine == null || dataLine.isEmpty()) continue; // ensure its not empty

                            switch (counter) { // switch through counter value

                                case 1: {

                                    if (dataLine.contains(":")) { // attempt to split the line by colons
                                        final String[] args = dataLine.split(":");

                                        // split the data line by commas and setup token instances containing the found information
                                        for (String playerLine : args) {
                                            final String[] pArgs = playerLine.split(",");
                                            final Token token = new Token(pArgs[0]);

                                            token.setMoney(Integer.parseInt(pArgs[1]));
                                            token.setSimplePosition(Integer.parseInt(pArgs[2]));
                                            token.setLocation(Game.INSTANCE.getByPosition(token.getSimplePosition()));
                                            token.setIcon(Token.Icon.getFromString(pArgs[3]));
                                            token.setInJail(Boolean.parseBoolean(pArgs[4]));
                                            token.setJailCounter(Integer.parseInt(pArgs[5]));
                                            token.setGOJCard(Boolean.parseBoolean(pArgs[6]));
                                            token.setSellingGOJCard(Boolean.parseBoolean(pArgs[7]));

                                            Game.INSTANCE.getPlayers().add(token);
                                        }

                                    } else if (dataLine.contains(",")) { // split the data line by commas and setup token instances containing the found information
                                        final String[] args = dataLine.split(",");
                                        final Token token = new Token(args[0]);

                                        token.setMoney(Integer.parseInt(args[1]));
                                        token.setSimplePosition(Integer.parseInt(args[2]));
                                        token.setLocation(Game.INSTANCE.getByPosition(token.getSimplePosition()));
                                        token.setIcon(Token.Icon.getFromString(args[3]));
                                        token.setInJail(Boolean.parseBoolean(args[4]));
                                        token.setJailCounter(Integer.parseInt(args[5]));
                                        token.setGOJCard(Boolean.parseBoolean(args[6]));
                                        token.setSellingGOJCard(Boolean.parseBoolean(args[7]));

                                        Game.INSTANCE.getPlayers().add(token);
                                    }

                                    // add the token to the player list, if possible
                                    Optional<Token> optionalToken = Game.INSTANCE.getPlayers().stream().findFirst();
                                    optionalToken.ifPresent(token -> Game.INSTANCE.setCurrentPlayerTurn(token));
                                    break;
                                }

                                case 2: {

                                    if (dataLine.contains(":")) { // attempt to split the line by colons
                                        final String[] args = dataLine.split(":");

                                        // split the data line by commas and update property instances with the found information
                                        for (String propertyLine : args) {
                                            final String[] pArgs = propertyLine.split(",");

                                            final int simplePosition = Integer.parseInt(pArgs[0]);
                                            Optional<Property> propertyOptional = Game.INSTANCE.getProperties().stream()
                                                    .filter(prop -> (prop.getPosition().getSimplePosition() == simplePosition)).findFirst();
                                            if (propertyOptional.isPresent()) {
                                                final Property property = propertyOptional.get();

                                                final Token token = Game.INSTANCE.getPlayerByName(pArgs[1]);
                                                if (token != null) property.setOwner(token);

                                                property.setHotels(Integer.parseInt(pArgs[2]));
                                                property.setHouses(Integer.parseInt(pArgs[3]));
                                                property.setMortgaged(Boolean.parseBoolean(pArgs[4]));
                                            }
                                        }

                                    } else if (dataLine.contains(",")) {
                                        final String[] args = dataLine.split(",");

                                        // split the data line by commas and update property instances with the found information
                                        final int simplePosition = Integer.parseInt(args[0]);
                                        Optional<Property> propertyOptional = Game.INSTANCE.getProperties().stream()
                                                .filter(prop -> (prop.getPosition().getSimplePosition() == simplePosition)).findFirst();
                                        if (propertyOptional.isPresent()) {
                                            final Property property = propertyOptional.get();

                                            final Token token = Game.INSTANCE.getPlayerByName(args[1]);
                                            if (token != null) property.setOwner(token);

                                            property.setHotels(Integer.parseInt(args[2]));
                                            property.setHouses(Integer.parseInt(args[3]));
                                            property.setMortgaged(Boolean.parseBoolean(args[4]));
                                        }
                                    }

                                    break;
                                }

                                case 3: {
                                    logText = dataLine.replace("|", "\n"); // replace the new line replacement with the new line and update log text
                                    break;
                                }
                            }
                        }

                    } catch (IOException ex) {ex.printStackTrace();}

                    dispose(); // dispose frame

                    // create the board instance
                    Game.INSTANCE.setBoard(new Board()); // new board instance
                    if (logText != null) Game.INSTANCE.getBoard().getCenter().getLogBox().setText(logText.replace("|", "\n")); // update log box with the log text
                }
            }
        });
        add(loadButton, BorderLayout.CENTER);

        // second spacer
        final Component spacer2 = Box.createRigidArea(new Dimension(0, (int) (getHeight() * 0.01)));
        add(spacer2);

        // form the new game button and its action
        JButton newButton = new JButton("New Game");
        newButton.setFont(font);
        newButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newButton.setMaximumSize(new Dimension((int) (getWidth() * 0.55), (int) (getHeight() * 0.17)));
        newButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // dispose the menu frame
                new Creation(); // new creation menu instance
            }
        });
        add(newButton, BorderLayout.CENTER);

        // third spacer
        final Component spacer3 = Box.createRigidArea(new Dimension(0, (int) (getHeight() * 0.01)));
        add(spacer3);

        // form the quit button and its action
        JButton quitButton = new JButton("Quit");
        quitButton.setFont(font);
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitButton.setMaximumSize(new Dimension((int) (getWidth() * 0.55), (int) (getHeight() * 0.17)));
        quitButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {System.exit(0);}
        });
        add(quitButton, BorderLayout.CENTER);

        // setup component listener to resize things around and update internal component dimensions
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                final Dimension dimension = new Dimension((int) (getWidth() * 0.55), (int) (getHeight() * 0.17)),
                        onePercentDimension = new Dimension(0, (int) (getHeight() * 0.01)),
                        thirtyFiveDimension = new Dimension(0, (int) (getHeight() * 0.35));

                // resize each button and spacer based on resized frame dimensions
                spacer.setMaximumSize(thirtyFiveDimension);
                loadButton.setMaximumSize(dimension);
                spacer2.setMaximumSize(onePercentDimension);
                newButton.setMaximumSize(dimension);
                spacer3.setMaximumSize(onePercentDimension);
                quitButton.setMaximumSize(dimension);
            }
        });

        // pack and make visible
        pack();
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        final Graphics2D g2d = (Graphics2D) g;
        final AffineTransform original = (AffineTransform) g2d.getTransform().clone(); // original transform

        final AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(-40), 0, 0);
        g2d.setTransform(affineTransform);

        final URL url = Game.class.getResource("/resources/logo.png"); // url to the logo image file (inside the JAR)
        if (url != null) { // not null
            try {

                final BufferedImage image = ImageIO.read(url);

                g2d.scale(0.72, 0.62);
                g2d.drawImage(image, (int) -(getContentPane().getWidth() * 0.2), (int) (getContentPane().getHeight() * 0.55),
                        getContentPane().getWidth(), getContentPane().getHeight(), this);

            } catch (IOException e) {e.printStackTrace();}
        }

        // reset transform
        g2d.setTransform(original);
    }

}