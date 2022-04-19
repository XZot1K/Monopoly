package game.components.gui;

import game.Game;
import game.components.entity.Token;
import game.components.gui.board.BoardCenter;
import game.components.property.Property;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashMap;

public class Controller extends JPanel {

    private final Controller INSTANCE;

    private final BoardCenter center;
    private final HashMap<String, JButton> buttonMap;

    private final JLabel header;

    public Controller(BoardCenter center) {
        this.INSTANCE = this;
        this.center = center;
        this.buttonMap = new HashMap<>();

        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        final Font font = new Font("Arial Black", Font.BOLD, 14);
        buildButtons(font);

        header = new JLabel("<html>" + Game.INSTANCE.getCurrentPlayerTurn().getName() + "'s Turn<br>Money: "
                + Game.INSTANCE.getCurrentPlayerTurn().getMoney() + "</html>");
        header.setFont(font.deriveFont(16f));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setVisible(true);
        add(header);
        add(Box.createRigidArea(new Dimension(50, 50)));

        update(false);

        setVisible(true);
    }

    public void update(boolean postRoll) {
        getHeader().setText("<html>" + Game.INSTANCE.getCurrentPlayerTurn().getName() + "'s Turn<br>Money: "
                + Game.INSTANCE.getCurrentPlayerTurn().getMoney() + "</html>");

        Arrays.stream(getComponents()).filter(JButton.class::isInstance).forEach(this::remove); // remove all buttons from the panel

        final Token token = Game.INSTANCE.getCurrentPlayerTurn();

        if (!token.isInJail()) {
            add(getButton("roll", !postRoll));

            if (token.getLocation().getOwner() != null && token.getLocation().getOwner().equals(token)) {

                if (token.getLocation().canBuild(token))
                    add(getButton("build", true));

                if (!token.getLocation().isMortgaged()) {
                    add(getButton("mortgage", true));
                    add(getButton("sell", true));
                } else add(getButton("unmortgage", true));
            } else if (token.getLocation().isPurchasable()) {

                if (token.getMoney() >= token.getLocation().getBaseValue())
                    add(getButton("buy", true));

            }

            if (token.hasGOJCard() && !token.isSellingGOJCard())
                add(getButton("sellGOJCard", true));

        } else {
            add(getButton("r4d", true));

            if (token.hasGOJCard()) add(getButton("useGOJCard", true));
            add(getButton("et", true));
        }

        if (postRoll) add(getButton("et", true));

        repaint();
        revalidate();
    }

    private void buildButtons(Font font) {

        JButton et = new JButton("End Turn");
        et.setAlignmentX(Box.CENTER_ALIGNMENT);
        et.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        et.setFont(font);
        et.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //bankrupt check
                final Token currentPlayer = Game.INSTANCE.getCurrentPlayerTurn();
                if (currentPlayer.getMoney() <= 0) {
                    Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + currentPlayer.getName() + " has gone bankrupt!");
                    Game.INSTANCE.getPlayers().remove(currentPlayer);
                } else Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + currentPlayer.getName() + " chose to end their turn.");

                Game.INSTANCE.nextTurn();
                Game.INSTANCE.getBoard().repaint();
            }
        });
        buttonMap.put("et", et);

        JButton r4d = new JButton("Roll For Doubles");
        r4d.setAlignmentX(Box.CENTER_ALIGNMENT);
        r4d.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        r4d.setFont(font);
        r4d.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final int result = JOptionPane.showConfirmDialog(null, "Do you want to pay $50 and roll for doubles?",
                        "Are You Sure?", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    final Token currentPlayer = Game.INSTANCE.getCurrentPlayerTurn();

                    center.setRoll((int) (Math.random() * 6 + 1), (int) (Math.random() * 6 + 1)); // roll a number between 1 and 6 for the left & right die

                    if (center.getRoll()[0] == center.getRoll()[1]) {
                        currentPlayer.setInJail(false);
                        currentPlayer.setJailCounter(0);
                        JOptionPane.showMessageDialog(null, "Congratulations " + currentPlayer.getName() + ", you have rolled doubles! Next turn\n"
                                + " you may return to the street!", "Got Lucky!", JOptionPane.INFORMATION_MESSAGE);

                        Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + currentPlayer.getName() + " was very lucky and rolled doubles!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Well, The good news is you have another free meal \n"
                                + "to look forward to! The bad news is you are still in jail and slightly more broke...", "Not So Lucky!", JOptionPane.INFORMATION_MESSAGE);

                        Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + currentPlayer.getName() + " needs to find a lucky charm or something!");
                    }

                    Game.INSTANCE.nextTurn();
                    Game.INSTANCE.getBoard().repaint();
                }
            }
        });
        r4d.setVisible(true);
        buttonMap.put("r4d", r4d);

        JButton roll = new JButton("Roll");
        roll.setAlignmentX(Box.CENTER_ALIGNMENT);
        roll.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        roll.setFont(font);
        roll.setVisible(true);
        roll.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                roll.setEnabled(false);
                center.setRoll((int) (Math.random() * 6 + 1), (int) (Math.random() * 6 + 1)); // roll a number between 1 and 6 for the left & right die

                final Token currentPlayer = Game.INSTANCE.getCurrentPlayerTurn();
                final int distance = (center.getRoll()[0] + center.getRoll()[1]);

                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + currentPlayer.getName() + " rolled a " + distance + "!");

                if (currentPlayer.move(distance)) Game.INSTANCE.nextTurn();
            }
        });
        buttonMap.put("roll", roll);

        JButton buy = new JButton("Buy Property");
        buy.setAlignmentX(Box.CENTER_ALIGNMENT);
        buy.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        buy.setFont(font);
        buy.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Token currentPlayer = Game.INSTANCE.getCurrentPlayerTurn();
                final Property property = currentPlayer.getLocation();

                if (property.isPurchasable()) {
                    final int result = JOptionPane.showConfirmDialog(null, "Would you like to buy this property for $"
                            + property.getBaseValue() + "?", "Are You Sure?", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {

                        if (currentPlayer.getMoney() < property.getBaseValue()) {
                            JOptionPane.showMessageDialog(null, "You are broke.", "Insufficient Funds", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // disable the button
                        ((Component) e.getSource()).setEnabled(false);

                        // remove money from player
                        currentPlayer.setMoney(currentPlayer.getMoney() - property.getBaseValue());

                        // set owner of property
                        property.setOwner(currentPlayer);

                        // log purchase
                        Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + currentPlayer.getName() + " has purchased the \""
                                + property.getName() + "\" property!");
                    }
                }
            }
        });
        buy.setVisible(true);
        buttonMap.put("buy", buy);

        JButton mortgage = new JButton("Mortgage Property");
        mortgage.setAlignmentX(Box.CENTER_ALIGNMENT);
        mortgage.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        mortgage.setFont(font);
        mortgage.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {


            }
        });
        buttonMap.put("mortgage", mortgage);

        JButton unmortgage = new JButton("Un-Mortgage Property");
        unmortgage.setAlignmentX(Box.CENTER_ALIGNMENT);
        unmortgage.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        unmortgage.setFont(font);
        unmortgage.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        buttonMap.put("unmortgage", unmortgage);

        JButton sell = new JButton("Sell Property");
        sell.setAlignmentX(Box.CENTER_ALIGNMENT);
        sell.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        sell.setFont(font);
        sell.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                final Token currentPlayer = Game.INSTANCE.getCurrentPlayerTurn();
                final Property property = currentPlayer.getLocation();

                final int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to sell the \""
                        + property.getName() + "\" for $" + property.getSellValue() + "? (All buildings will be sold)", "Are You Sure?", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    property.setHouses(0);
                    property.setHotels(0);
                    property.setOwner(null);
                    currentPlayer.setMoney(currentPlayer.getMoney() + property.getSellValue());

                    // log purchase
                    Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + currentPlayer.getName() + " has sold the \""
                            + property.getName() + "\" property.");
                }

            }
        });
        sell.setVisible(true);
        buttonMap.put("sell", sell);

        JButton build = new JButton("Build");
        build.setAlignmentX(Box.CENTER_ALIGNMENT);
        build.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        build.setFont(font);
        build.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                final Token currentPlayer = Game.INSTANCE.getCurrentPlayerTurn();
                final Property property = currentPlayer.getLocation();

                if (property.canBuild(currentPlayer)) {

                    if ((property.getHouses() + 1) > 4) {
                        final int result = JOptionPane.showConfirmDialog(null,
                                "Would you like to build a hotel on the \"" + property.getName() + "\" property?",
                                "Are You Sure?", JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.YES_OPTION) {
                            currentPlayer.setMoney(currentPlayer.getMoney() - property.getPerHouseCost());
                            property.setHotels(1);
                            property.setHouses(0);

                            // log build
                            Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + currentPlayer.getName() + " has built a hotel on the \""
                                    + property.getName() + "\" property!");

                            if (!property.canBuild(currentPlayer)) {
                                // disable the button
                                ((Component) e.getSource()).setEnabled(false);
                            }
                        }
                    } else {
                        final int result = JOptionPane.showConfirmDialog(null,
                                "Would you like to build a house on the \"" + property.getName() + "\" property?",
                                "Are You Sure?", JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.YES_OPTION) {
                            currentPlayer.setMoney(currentPlayer.getMoney() - property.getPerHouseCost());
                            property.setHouses(property.getHouses() + 1);

                            // log build
                            Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + currentPlayer.getName() + " has built a house on the \""
                                    + property.getName() + "\" property!");

                            if (!property.canBuild(currentPlayer)) {
                                // disable the button
                                ((Component) e.getSource()).setEnabled(false);
                            }
                        }
                    }
                }

            }
        });
        build.setVisible(true);
        buttonMap.put("build", build);

        JButton useGOJCard = new JButton("Use \"Get Out of Jail\" Card");
        useGOJCard.setAlignmentX(Box.CENTER_ALIGNMENT);
        useGOJCard.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        useGOJCard.setFont(font);
        useGOJCard.setEnabled(false);
        useGOJCard.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Token currentPlayer = Game.INSTANCE.getCurrentPlayerTurn();

                final int result = JOptionPane.showConfirmDialog(null, "Do you want to use your \"Get Out of Jail Free\" Card?",
                        "Are You Sure?", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {

                    // disable the button
                    ((Component) e.getSource()).setEnabled(false);

                    currentPlayer.setGOJCard(false);
                    currentPlayer.setSellingGOJCard(false);
                    currentPlayer.setInJail(false);
                    currentPlayer.setJailCounter(0);
                    Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + currentPlayer.getName() + "\" has been released from jail.");

                    Game.INSTANCE.nextTurn();
                    Game.INSTANCE.getBoard().repaint();
                }
            }
        });
        useGOJCard.setVisible(true);
        buttonMap.put("useGOJCard", useGOJCard);

        JButton sellGOJCard = new JButton("Sell \"Get Out of Jail\" Card");
        sellGOJCard.setAlignmentX(Box.CENTER_ALIGNMENT);
        sellGOJCard.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        sellGOJCard.setFont(font);
        sellGOJCard.setEnabled(false);
        sellGOJCard.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Token currentPlayer = Game.INSTANCE.getCurrentPlayerTurn();

                final int result = JOptionPane.showConfirmDialog(null, "Do you want to sell your \"Get Out of Jail Free\" Card for $50?",
                        "Are You Sure?", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    // disable the button
                    ((Component) e.getSource()).setEnabled(false);

                    currentPlayer.setGOJCard(false);
                    currentPlayer.setSellingGOJCard(false);
                    currentPlayer.setMoney(currentPlayer.getMoney() + 50);

                    Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + currentPlayer.getName() + "\" has sold their \"Get Out of Jail Free\" Card for $50.");
                }
            }
        });
        sellGOJCard.setVisible(true);
        buttonMap.put("sellGOJCard", sellGOJCard);
    }

    public JButton getButton(String id, boolean enable) {
        final JButton button = buttonMap.getOrDefault(id.toLowerCase(), null);
        if (button != null) button.setEnabled(enable);
        return button;
    }

    public JLabel getHeader() {return header;}

}