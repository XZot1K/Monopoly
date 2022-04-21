package game.components.gui;

import game.Game;
import game.components.entity.Token;
import game.components.gui.board.Board;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Optional;

public class Creation extends JFrame {
    private boolean started;

    public Creation() {
        started = false;

        URL logo = Game.class.getResource("/resources/tokens/thimble.png");
        if (logo != null) setIconImage(new ImageIcon(logo).getImage());

        setSize(400, 400);

        // setup dimensions
        final Dimension dim = new Dimension(400, 400);
        setPreferredSize(dim);
        setMinimumSize(dim);

        // center the window on screen
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocationRelativeTo(null);
        setLocation((int) ((screenSize.getWidth() / 2) - (getPreferredSize().getWidth() / 2)),
                (int) ((screenSize.getHeight() / 2) - (getPreferredSize().getHeight() / 2)));

        setTitle("Monopoly - Player Creation"); // update title of frame
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // exit on close
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        final Color greenBackground = new Color(144, 238, 144); // a nice green
        getContentPane().setBackground(greenBackground); // update the content pane's background color

        // form the labels, boxes, buttons, etc.
        createComponents(greenBackground);

        // when the window closes open the main menu
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);

                if (!started) {
                    Game.INSTANCE.getPlayers().clear(); // clear the added tokens
                    new Menu(); // new menu instance
                }
            }
        });

        // make visible
        setVisible(true);
    }

    private void createComponents(Color greenBackground) {

        /*
         * NOTE: some of these components are out of order or seperated from their
         *       modifications in order to be used by other components.
         */

        final Font font = new Font("Arial Black", Font.BOLD, 15); // new font

        final DefaultListModel<String> tokens = new DefaultListModel<>(); // the list of all tokens made so far
        final JList<String> playerList = new JList<>(tokens); // the selection list

        // new spacer
        final Component spacer = Box.createRigidArea(new Dimension((int) (getWidth() * 0.01), (int) (getHeight() * 0.06)));
        add(spacer);

        // label that updates based on the total tokens made thus far
        final JLabel cpLabel = new JLabel("Current Players (" + tokens.getSize() + "):");
        cpLabel.setFont(font);
        cpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cpLabel.setVisible(true);
        add(cpLabel);

        // new spacer
        final Component spacer2 = Box.createRigidArea(new Dimension((int) (getWidth() * 0.01), (int) (getWidth() * 0.01)));
        add(spacer2);

        final JComboBox<Token.Icon> icons = new JComboBox<>(Token.Icon.values()); // combo box full of all the possible icons for selection
        final JTextField playerName = new JTextField(); // the player's name field

        // new start button to begin the game and move on
        JButton start = new JButton("Start");
        start.setEnabled(false);

        // button to remove the player selected in the list of tokens/players
        final JButton removePlayer = new JButton("Remove Player");
        removePlayer.setAlignmentX(Box.LEFT_ALIGNMENT);
        removePlayer.setPreferredSize(new Dimension((int) (getWidth() * 0.34), (int) (getHeight() * 0.1)));
        removePlayer.setEnabled(false);
        removePlayer.setFont(font.deriveFont(12f));
        removePlayer.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playerList.getSelectedValue() == null) { // check if a token/player is selected
                    // send dialog stating none was selected
                    JOptionPane.showMessageDialog(null, "Please select a player to remove them.",
                            "Invalid Player Selection", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // re-add the used icon to the icon drop down
                final Token.Icon icon = Token.Icon.getFromString(playerList.getSelectedValue());
                icons.insertItemAt(icon, 0);

                // remove the player that is selected
                Game.INSTANCE.getPlayers().removeIf(token -> (playerList.getSelectedValue().toUpperCase().contains(token.getName().toUpperCase())
                        && playerList.getSelectedValue().toUpperCase().replace(" ", "_").contains(token.getIcon().name())));
                tokens.remove(playerList.getSelectedIndex());

                // update the label token counter
                cpLabel.setText("Current Players (" + tokens.getSize() + "):");

                start.setEnabled((tokens.size() >= 2)); // check token counter to ensure they can NOT start the game without 2 player at minimum
            }
        });

        // adjust the player list's font and implement a selection listener that updates the remove button based on if a player is even selected
        playerList.setFont(font.deriveFont(Font.PLAIN, 12));
        playerList.addListSelectionListener(e -> removePlayer.setEnabled(playerList.getSelectedValue() != null));

        // new scroll bar for the player list (likely won't ever be visible)
        final JScrollPane listScroll = new JScrollPane(playerList);
        listScroll.setMaximumSize(new Dimension((int) (getWidth() * 0.8), (int) (getHeight() * 0.4)));
        listScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(listScroll);

        // new spacer
        final Component spacer3 = Box.createRigidArea(new Dimension((int) (getWidth() * 0.01), (int) (getWidth() * 0.01)));
        add(spacer3);


        // modifiers for the start button
        start.setAlignmentX(Box.LEFT_ALIGNMENT);
        start.setPreferredSize(new Dimension((int) (getWidth() * 0.25), (int) (getHeight() * 0.1)));
        start.setFont(font.deriveFont(12f));
        start.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                started = true; // set to true so the frame doesn't exit to main menu
                dispose(); // dispose frame

                // set the initial player turn
                final Optional<Token> firstFound = Game.INSTANCE.getPlayers().stream().findFirst();
                firstFound.ifPresent(token -> Game.INSTANCE.setCurrentPlayerTurn(token));

                // create the board instance
                Game.INSTANCE.setBoard(new Board());
            }
        });

        // new panel for all the buttons
        final JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(greenBackground);
        buttonPanel.setMaximumSize(new Dimension(getWidth(), (int) (getHeight() * 0.3)));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // new add player button
        JButton addPlayer = new JButton("Add Player");
        addPlayer.setEnabled(false);
        addPlayer.setFont(font.deriveFont(12f));
        addPlayer.setAlignmentX(Box.LEFT_ALIGNMENT);
        addPlayer.setPreferredSize(new Dimension((int) (getWidth() * 0.28), (int) (getHeight() * 0.1)));
        addPlayer.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // ensure the entered player name is valid
                if (playerName.getText() == null || playerName.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No player name was entered.",
                            "Invalid Player Name", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // check through all existing tokens to ensure another player with the same name does NOT exist
                for (int i = -1; ++i < tokens.getSize(); ) {
                    final String token = tokens.getElementAt(i);
                    final String[] args = token.split(" ");
                    if (args[0].equalsIgnoreCase(playerName.getText())) {
                        JOptionPane.showMessageDialog(null, "A player by the name \"" + playerName.getText() + "\" already exists.",
                                "Invalid Player Name", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // create the token and add them to the player list
                final Token token = new Token(playerName.getText());
                token.setIcon((Token.Icon) icons.getSelectedItem());
                Game.INSTANCE.getPlayers().add(token);

                // update player count label and add them to the selection list
                tokens.addElement(playerName.getText().replace(" ", "_") + " (" + token.getIcon().name() + ")");
                cpLabel.setText("Current Players (" + tokens.getSize() + "):");

                // remove the icon from the dropdown
                icons.removeItemAt(icons.getSelectedIndex());
                if (icons.getModel().getSize() > 0) icons.setSelectedIndex(0);


                // reset the player name field and disable the add player button
                playerName.setText("");
                addPlayer.setEnabled(false);

                if (icons.getModel().getSize() <= 0) {
                    playerName.setEnabled(false); // disable player name field since all slots are filled
                    icons.setEnabled(false); // disable icons dropdown since all slots are filled
                }

                start.setEnabled((tokens.size() >= 2)); // only enable the start button once 2+ players are present
            }
        });
        buttonPanel.add(addPlayer); // add the add button to button panel

        // new spacer
        final Component spacer4 = Box.createRigidArea(new Dimension((int) (getWidth() * 0.01), (int) (getHeight() * 0.02)));
        add(spacer4);

        buttonPanel.add(removePlayer); // add remove button to button panel

        // new spacer
        final Component spacer5 = Box.createRigidArea(new Dimension((int) (getWidth() * 0.01), (int) (getHeight() * 0.02)));
        add(spacer5);

        buttonPanel.add(start); // add the start button to button panel

        // new panel for the icon drop down
        final JPanel iconPanel = new JPanel();
        iconPanel.setBackground(greenBackground);
        iconPanel.setMaximumSize(new Dimension(getWidth(), (int) (getHeight() * 0.3)));
        iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // new icon label
        final JLabel iconLabel = new JLabel("Icon:");
        iconLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
        iconLabel.setFont(font.deriveFont(14f));
        iconLabel.setVisible(true);
        iconPanel.add(iconLabel);

        // new spacer
        final Component spacer6 = Box.createRigidArea(new Dimension((int) (getWidth() * 0.01), (int) (getHeight() * 0.02)));
        iconPanel.add(spacer6);

        // modifiers for the icons dropdown
        icons.setFont(font.deriveFont(Font.PLAIN, 12));
        icons.setAlignmentX(Box.LEFT_ALIGNMENT);
        icons.setPreferredSize(new Dimension((int) (getWidth() * 0.6), (int) (getHeight() * 0.08)));
        iconPanel.add(icons);

        // new panel for the player name entry
        final JPanel namePanel = new JPanel();
        namePanel.setBackground(greenBackground);
        namePanel.setMaximumSize(new Dimension(getWidth(), (int) (getHeight() * 0.3)));
        namePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // new name label
        final JLabel nameLabel = new JLabel("Name:");
        nameLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
        nameLabel.setFont(font.deriveFont(14f));
        nameLabel.setVisible(true);
        namePanel.add(nameLabel); // add to name panel

        // new spacer
        final Component spacer7 = Box.createRigidArea(new Dimension((int) (getWidth() * 0.01), (int) (getHeight() * 0.02)));
        add(spacer7);

        // modifiers for the player name field
        playerName.setFont(font.deriveFont(Font.PLAIN, 12));
        playerName.setAlignmentX(Box.LEFT_ALIGNMENT);
        playerName.setPreferredSize(new Dimension((int) (getWidth() * 0.6), (int) (getHeight() * 0.08)));

        // add a document listener for when text is updated
        playerName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {update();}

            @Override
            public void removeUpdate(DocumentEvent e) {update();}

            @Override
            public void changedUpdate(DocumentEvent e) {update();}

            private void update() {addPlayer.setEnabled(playerName.getText() != null && !playerName.getText().isEmpty() && icons.getSelectedItem() != null);}

        });
        namePanel.add(playerName); // add to name panel
        add(namePanel);

        // add icon panel second to last
        add(iconPanel);

        // add button panel last
        add(buttonPanel);
    }

}