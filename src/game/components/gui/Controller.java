package game.components.gui;

import game.Game;
import game.components.entity.Token;
import game.components.gui.board.BoardCenter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashMap;

public class Controller extends JPanel {

    private final BoardCenter center;
    private final HashMap<String, JButton> buttonMap;

    private final JLabel header;

    public Controller(BoardCenter center) {
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
        header.setAlignmentX(Component.RIGHT_ALIGNMENT);
        header.setVisible(true);
        add(header);
        add(Box.createVerticalGlue());

        update();

        setVisible(true);
    }

    public void update() {
        getHeader().setText("<html>" + Game.INSTANCE.getCurrentPlayerTurn().getName() + "'s Turn<br>Money: "
                + Game.INSTANCE.getCurrentPlayerTurn().getMoney() + "</html>");

        Arrays.stream(getComponents()).filter(component -> (component instanceof JButton
                || component instanceof Box.Filler)).forEach(this::remove); // remove all buttons from the panel

        final Token token = Game.INSTANCE.getCurrentPlayerTurn();

        if (!token.isInJail()) {
            add(getButton("roll", true));

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
    }

    private void buildButtons(Font font) {

        JButton et = new JButton("End Turn");
        et.setAlignmentX(Box.CENTER_ALIGNMENT);
        et.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        et.setFont(font);
        et.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                final Token currentPlayer = Game.INSTANCE.getCurrentPlayerTurn();
                if (currentPlayer.getMoney() <= 0) {
                    Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + currentPlayer.getName() + " has gone bankrupt!");

                    // TODO more bankrupt

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
                final Token currentPlayer = Game.INSTANCE.getCurrentPlayerTurn();

                center.setRoll((int) (Math.random() * 6 + 1), (int) (Math.random() * 6 + 1)); // roll a number between 1 and 6 for the left & right die

                if (center.getRoll()[0] == center.getRoll()[1]) {
                    currentPlayer.setInJail(false);
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
        });
        buttonMap.put("r4d", r4d);

        JButton roll = new JButton("Roll");
        roll.setAlignmentX(Box.CENTER_ALIGNMENT);
        roll.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        roll.setFont(font);
        roll.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                roll.setEnabled(false);
                center.setRoll((int) (Math.random() * 6 + 1), (int) (Math.random() * 6 + 1)); // roll a number between 1 and 6 for the left & right die

                final Token currentPlayer = Game.INSTANCE.getCurrentPlayerTurn();
                final int distance = (center.getRoll()[0] + center.getRoll()[1]);

                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + currentPlayer.getName() + " rolled a " + distance + "!");

                currentPlayer.move(distance);
                Game.INSTANCE.getBoard().repaint();
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

            }
        });
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

            }
        });
        buttonMap.put("sell", sell);

        JButton build = new JButton("Build");
        build.setAlignmentX(Box.CENTER_ALIGNMENT);
        build.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        build.setFont(font);
        build.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        buttonMap.put("build", build);

        JButton useGOJCard = new JButton("Use \"Get Out of Jail\" Card");
        useGOJCard.setAlignmentX(Box.CENTER_ALIGNMENT);
        useGOJCard.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        useGOJCard.setFont(font);
        useGOJCard.setEnabled(false);
        useGOJCard.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        buttonMap.put("useGOJCard", useGOJCard);

        JButton sellGOJCard = new JButton("Sell \"Get Out of Jail\" Card");
        sellGOJCard.setAlignmentX(Box.CENTER_ALIGNMENT);
        sellGOJCard.setPreferredSize(new Dimension((int) (getWidth() * 0.08), (int) (getHeight() * 0.05)));
        sellGOJCard.setFont(font);
        sellGOJCard.setEnabled(false);
        sellGOJCard.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        buttonMap.put("sellGOJCard", sellGOJCard);
    }

    public JButton getButton(String id, boolean enable) {
        final JButton button = buttonMap.getOrDefault(id.toLowerCase(), null);
        if (button != null) button.setEnabled(enable);
        return button;
    }

    public JLabel getHeader() {return header;}

}