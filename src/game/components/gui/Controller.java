/*
    Jeremiah Osborne & Dominick Delgado
    Date: 4/1/2022
 */

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

    private final BoardCenter center; // the board center
    private final HashMap<String, JButton> buttonMap; // button map for updating

    private final JLabel header; // the header information

    private boolean postRoll; // whether the update is post player roll

    public Controller(BoardCenter center) {

        // initialize variables
        this.center = center;
        this.buttonMap = new HashMap<>();
        setPostRoll(false);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        final Font font = new Font("Arial Black", Font.BOLD, 14); // new font
        buildButtons(font); // build buttons


        // create the header which states whose turn it is and their money
        header = new JLabel("<html>" + Game.INSTANCE.getCurrentPlayerTurn().getName() + "'s Turn<br>Money: "
                + Game.INSTANCE.getCurrentPlayerTurn().getMoney() + "</html>");
        header.setFont(font.deriveFont(16f));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setVisible(true);
        add(header);
        add(Box.createRigidArea(new Dimension(50, 50))); // spacer

        setVisible(true); // set visible
    }

    @Override
    public void repaint() {
        // update header while it exists
        if (getHeader() != null) {
            getHeader().setText("<html>" + Game.INSTANCE.getCurrentPlayerTurn().getName() + "'s Turn<br>Money: "
                    + Game.INSTANCE.getCurrentPlayerTurn().getMoney() + "</html>");
        }

        // remove all buttons from the panel, if exists
        if (getComponents().length > 0) Arrays.stream(getComponents()).filter(JButton.class::isInstance).forEach(this::remove);

        // check if button map is populated yet
        if (buttonMap != null && !buttonMap.isEmpty()) {

            // get current player turn
            final Token token = Game.INSTANCE.getCurrentPlayerTurn();

            // check if NOT in jail
            if (!token.isInJail()) {
                add(getButton("roll", !isPostRoll())); // add roll button (post roll is to ensure they don't roll twice)

                // check ownership
                if (token.getLocation().getOwner() != null && token.getLocation().getOwner().equals(token)) {

                    if (token.getLocation().canBuild(token)) // check if they can build a house or hotel
                        add(getButton("build", true)); // add build button

                    if (!token.getLocation().isMortgaged()) { // check if NOT mortgaged
                        add(getButton("mortgage", true)); // add mortgage button
                        add(getButton("sell", true)); // add sell button
                    } else
                        add(getButton("unmortgage", true)); // add unmortgage button

                } else if (token.getLocation().isPurchasable()) { // if property is purchasable

                    if (token.getMoney() >= token.getLocation().getBaseValue()) // check if player has enough money
                        add(getButton("buy", true)); // add buy button enabled
                    else
                        add(getButton("buy", false)); // add buy button disabled

                }

                if (token.hasGOJCard() && !token.isSellingGOJCard()) { // check if the player is selling their GOJ Card

                    // add the sell GOJ card, if possible
                    final JButton button = getButton("sellGOJCard", true);
                    if (button != null) add(button);

                }

                if (isPostRoll()) add(getButton("et", true)); // add end turn button, if post roll

            } else { // player is in jail

                add(getButton("r4d", true)); // add roll for double buttons

                // if they have one, add use GOJ card button
                if (token.hasGOJCard()) add(getButton("useGOJCard", true));

                add(getButton("et", true)); // add end turn button
            }
        }

        // repaint and revalidate the frame
        super.repaint();
        revalidate();
    }

    private void buildButtons(Font font) {

        // build and add the end turn button the hashmap
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

        // build and add the roll for doubles button the hashmap
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
                    Game.INSTANCE.updateBoard();
                }
            }
        });
        r4d.setVisible(true);
        buttonMap.put("r4d", r4d);

        // build and add the roll button the hashmap
        JButton roll = new JButton("Roll");
        roll.setAlignmentX(Box.CENTER_ALIGNMENT);
        roll.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        roll.setFont(font);
        roll.setVisible(true);
        roll.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPostRoll()) return; // catch the roll in-case of somehow rolling twice

                // disable the button
                ((Component) e.getSource()).setEnabled(false);

                setPostRoll(true);
                center.setRoll((int) (Math.random() * 6 + 1), (int) (Math.random() * 6 + 1)); // roll a number between 1 and 6 for the left & right die

                final Token currentPlayer = Game.INSTANCE.getCurrentPlayerTurn();
                final int distance = (center.getRoll()[0] + center.getRoll()[1]); // add the dice together

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + currentPlayer.getName() + " rolled a " + distance + "!");

                if (currentPlayer.move(distance)) Game.INSTANCE.nextTurn(); // move and end the turn if needed
            }
        });
        buttonMap.put("roll", roll);

        // build and add the buy property button the hashmap
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

                        Game.INSTANCE.updateBoard();
                    }
                }
            }
        });
        buy.setVisible(true);
        buttonMap.put("buy", buy);

        // build and add the mortgage property button the hashmap
        JButton mortgage = new JButton("Mortgage Property");
        mortgage.setAlignmentX(Box.CENTER_ALIGNMENT);
        mortgage.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        mortgage.setFont(font);
        mortgage.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Token currentPlayer = Game.INSTANCE.getCurrentPlayerTurn();
                final Property property = currentPlayer.getLocation();

                final int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to mortgage the \""
                                + property.getName() + "\" property for $" + property.getMortgage() + "? (The property will not gain rent)",
                        "Are You Sure?", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {

                    // disable the button
                    ((Component) e.getSource()).setEnabled(false);

                    property.setMortgaged(true);
                    currentPlayer.setMoney(currentPlayer.getMoney() + property.getMortgage());

                    // log purchase
                    Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + currentPlayer.getName() + " has mortgaged the \""
                            + property.getName() + "\" property for $" + property.getMortgage() + ".");

                    Game.INSTANCE.updateBoard();
                }
            }
        });
        buttonMap.put("mortgage", mortgage);

        // build and add the unmortgage property button the hashmap
        JButton unmortgage = new JButton("Un-Mortgage Property");
        unmortgage.setAlignmentX(Box.CENTER_ALIGNMENT);
        unmortgage.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        unmortgage.setFont(font);
        unmortgage.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Token currentPlayer = Game.INSTANCE.getCurrentPlayerTurn();
                final Property property = currentPlayer.getLocation();

                final int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to un-mortgage the \""
                                + property.getName() + "\" for $" + property.getUnMortgagedCost() + "? (Mortgage cost + 10% interest)",
                        "Are You Sure?", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {

                    // disable the button
                    ((Component) e.getSource()).setEnabled(false);

                    property.setMortgaged(false);
                    currentPlayer.setMoney(currentPlayer.getMoney() - property.getUnMortgagedCost());

                    // log purchase
                    Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + currentPlayer.getName() + " has un-mortgaged the \""
                            + property.getName() + "\" property paying $" + property.getUnMortgagedCost() + ".");

                    Game.INSTANCE.updateBoard();
                }
            }
        });
        buttonMap.put("unmortgage", unmortgage);

        // build and add the sell property button the hashmap
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

                    Game.INSTANCE.updateBoard();
                }

            }
        });
        sell.setVisible(true);
        buttonMap.put("sell", sell);

        // build and add the build button the hashmap
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

                    Game.INSTANCE.updateBoard();
                }

            }
        });
        build.setVisible(true);
        buttonMap.put("build", build);

        // build and add the use GOJ card button the hashmap
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

                    // update variables to make player no longer in jail
                    currentPlayer.setGOJCard(false);
                    currentPlayer.setSellingGOJCard(false);
                    currentPlayer.setInJail(false);
                    currentPlayer.setJailCounter(0); // reset turn counter

                    // log action
                    Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + currentPlayer.getName() + "\" has been released from jail.");

                    Game.INSTANCE.nextTurn(); // next turn
                    Game.INSTANCE.updateBoard(); // update board
                }
            }
        });
        useGOJCard.setVisible(true);
        buttonMap.put("useGOJCard", useGOJCard);

        // build and add the sell GOJ card button the hashmap
        JButton sellGOJCard = new JButton("Sell \"Get Out of Jail\" Card");
        sellGOJCard.setAlignmentX(Box.CENTER_ALIGNMENT);
        sellGOJCard.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        sellGOJCard.setFont(font);
        sellGOJCard.setEnabled(false);
        sellGOJCard.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Token currentPlayer = Game.INSTANCE.getCurrentPlayerTurn();

                final int result = JOptionPane.showConfirmDialog(null, "Do you want to sell your \"Get Out of Jail Free\" card for $50?",
                        "Are You Sure?", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    // disable the button
                    ((Component) e.getSource()).setEnabled(false);

                    currentPlayer.setGOJCard(false);
                    currentPlayer.setSellingGOJCard(false);
                    currentPlayer.setMoney(currentPlayer.getMoney() + 50);

                    Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + currentPlayer.getName() + "\" has sold their \"Get Out of Jail Free\" card for $50.");

                    Game.INSTANCE.updateBoard();
                }
            }
        });
        sellGOJCard.setVisible(true);
        buttonMap.put("sellGOJCard", sellGOJCard);

        // build and add the buy GOJ card button the hashmap
        JButton buyGOJCard = new JButton("Buy \"Get Out of Jail\" Card");
        buyGOJCard.setAlignmentX(Box.CENTER_ALIGNMENT);
        buyGOJCard.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        buyGOJCard.setFont(font);
        buyGOJCard.setEnabled(false);
        buyGOJCard.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Token currentPlayer = Game.INSTANCE.getCurrentPlayerTurn();

                final int result = JOptionPane.showConfirmDialog(null, "Do you want to buy a \"Get Out of Jail Free\" card from another player?",
                        "Are You Sure?", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {

                    final Token seller = Game.INSTANCE.getGOJSellingPlayer(); // find a seller
                    if (seller != null) { // if one exists
                        currentPlayer.setGOJCard(true);
                        currentPlayer.setSellingGOJCard(false);

                        // determine the amount to charge
                        int amount = Math.min(currentPlayer.getMoney(), 100);

                        // remove and add money between two players
                        currentPlayer.setMoney(currentPlayer.getMoney() - amount);
                        seller.setMoney(seller.getMoney() + amount);

                        // log action
                        Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + currentPlayer.getName()
                                + "\" has purchase a \"Get Out of Jail Free\" Card for $100 from \"" + seller.getName() + "\".");

                        Game.INSTANCE.updateBoard(); // update board
                    } else
                        JOptionPane.showMessageDialog(null, "No players are selling a \""
                                + "Get Out of Jail Free\" card.", "No Seller Found", JOptionPane.ERROR_MESSAGE);

                    // disable the button
                    ((Component) e.getSource()).setEnabled(false);


                }
            }
        });
        buyGOJCard.setVisible(true);
        buttonMap.put("buyGOJCard", buyGOJCard);
    }

    public JButton getButton(String id, boolean enable) {
        final JButton button = buttonMap.getOrDefault(id.toLowerCase(), null);
        if (button != null) button.setEnabled(enable);
        return button;
    }

    public JLabel getHeader() {return header;}

    public boolean isPostRoll() {return postRoll;}

    public void setPostRoll(boolean postRoll) {this.postRoll = postRoll;}

}