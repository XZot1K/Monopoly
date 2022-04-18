package game.components.gui;

import game.Game;
import game.components.entity.Token;
import game.components.gui.board.Board;

import javax.naming.InsufficientResourcesException;
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

        final Dimension dim = new Dimension(400, 400);
        setPreferredSize(dim);
        setMinimumSize(dim);

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocationRelativeTo(null);
        setLocation((int) ((screenSize.getWidth() / 2) - (getPreferredSize().getWidth() / 2)),
                (int) ((screenSize.getHeight() / 2) - (getPreferredSize().getHeight() / 2)));

        setTitle("Monopoly - Player Creation"); // update title of frame
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // exit on close
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        final Color greenBackground = new Color(144, 238, 144); // a nice green
        getContentPane().setBackground(greenBackground); // update the content pane's background color

        createComponents(greenBackground);

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

        setVisible(true);
    }

    private void createComponents(Color greenBackground) {
        final Font font = new Font("Arial Black", Font.BOLD, 15);

        final DefaultListModel<String> tokens = new DefaultListModel<>();
        final JList<String> playerList = new JList<>(tokens);

        final Component spacer = Box.createRigidArea(new Dimension((int) (getWidth() * 0.01), (int) (getHeight() * 0.06)));
        add(spacer);

        final JLabel cpLabel = new JLabel("Current Players (" + tokens.getSize() + "):");
        cpLabel.setFont(font);
        cpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cpLabel.setVisible(true);
        add(cpLabel);

        final Component spacer2 = Box.createRigidArea(new Dimension((int) (getWidth() * 0.01), (int) (getWidth() * 0.01)));
        add(spacer2);

        final JComboBox<Token.Icon> icons = new JComboBox<>(Token.Icon.values());
        final JTextField playerName = new JTextField();
        final JButton removePlayer = new JButton("Remove Player");

        removePlayer.setAlignmentX(Box.LEFT_ALIGNMENT);
        removePlayer.setPreferredSize(new Dimension((int) (getWidth() * 0.34), (int) (getHeight() * 0.1)));
        removePlayer.setEnabled(false);
        removePlayer.setFont(font.deriveFont(12f));
        removePlayer.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playerList.getSelectedValue() == null) {
                    JOptionPane.showMessageDialog(null, "Please select a player to remove them.",
                            "Invalid Player Selection", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                final Token.Icon icon = Token.Icon.getFromString(playerList.getSelectedValue());
                icons.insertItemAt(icon, 0);

                Game.INSTANCE.getPlayers().removeIf(token -> (playerList.getSelectedValue().toUpperCase().contains(token.getName().toUpperCase())
                        && playerList.getSelectedValue().toUpperCase().replace(" ", "_").contains(token.getIcon().name())));
                tokens.remove(playerList.getSelectedIndex());
                cpLabel.setText("Current Players (" + tokens.getSize() + "):");
            }
        });

        playerList.setFont(font.deriveFont(Font.PLAIN, 12));
        playerList.addListSelectionListener(e -> removePlayer.setEnabled(playerList.getSelectedValue() != null));

        final JScrollPane listScroll = new JScrollPane(playerList);
        listScroll.setMaximumSize(new Dimension((int) (getWidth() * 0.8), (int) (getHeight() * 0.4)));
        listScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(listScroll);

        final Component spacer3 = Box.createRigidArea(new Dimension((int) (getWidth() * 0.01), (int) (getWidth() * 0.01)));
        add(spacer3);


        // setup button panel
        JButton start = new JButton("Start");
        start.setAlignmentX(Box.LEFT_ALIGNMENT);
        start.setPreferredSize(new Dimension((int) (getWidth() * 0.25), (int) (getHeight() * 0.1)));
        start.setEnabled(false);
        start.setFont(font.deriveFont(12f));
        start.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                started = true;
                dispose(); // dispose frame

                // set the initial player turn
                final Optional<Token> firstFound = Game.INSTANCE.getPlayers().stream().findFirst();
                firstFound.ifPresent(token -> Game.INSTANCE.setCurrentPlayerTurn(token));

                // create the board instance
                try {
                    Game.INSTANCE.setBoard(new Board(Game.INSTANCE));
                } catch (InsufficientResourcesException ex) {ex.printStackTrace();}
            }
        });

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(greenBackground);
        buttonPanel.setMaximumSize(new Dimension(getWidth(), (int) (getHeight() * 0.3)));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton addPlayer = new JButton("Add Player");
        addPlayer.setEnabled(false);
        addPlayer.setFont(font.deriveFont(12f));
        addPlayer.setAlignmentX(Box.LEFT_ALIGNMENT);
        addPlayer.setPreferredSize(new Dimension((int) (getWidth() * 0.28), (int) (getHeight() * 0.1)));
        addPlayer.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (playerName.getText() == null || playerName.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No player name was entered.",
                            "Invalid Player Name", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                for (int i = -1; ++i < tokens.getSize(); ) {
                    final String token = tokens.getElementAt(i);
                    final String[] args = token.split(" ");
                    if (args[0].equalsIgnoreCase(playerName.getText())) {
                        JOptionPane.showMessageDialog(null, "A player by the name \"" + playerName.getText() + "\" already exists.",
                                "Invalid Player Name", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                final Token token = new Token(playerName.getText()) {
                    @Override
                    public void move(int distance) {} // TODO implement
                };
                token.setIcon((Token.Icon) icons.getSelectedItem());
                Game.INSTANCE.getPlayers().add(token);

                tokens.addElement(playerName.getText().replace(" ", "_") + " (" + token.getIcon().name() + ")");
                cpLabel.setText("Current Players (" + tokens.getSize() + "):");

                icons.removeItemAt(icons.getSelectedIndex());
                if (icons.getModel().getSize() > 0) icons.setSelectedIndex(0);


                playerName.setText("");
                addPlayer.setEnabled(false);

                start.setEnabled((tokens.size() >= 2));
            }
        });
        buttonPanel.add(addPlayer);

        final Component spacer4 = Box.createRigidArea(new Dimension((int) (getWidth() * 0.01), (int) (getHeight() * 0.02)));
        add(spacer4);

        buttonPanel.add(removePlayer);

        final Component spacer5 = Box.createRigidArea(new Dimension((int) (getWidth() * 0.01), (int) (getHeight() * 0.02)));
        add(spacer5);

        buttonPanel.add(start);

        // setup icon panel
        final JPanel iconPanel = new JPanel();
        iconPanel.setBackground(greenBackground);
        iconPanel.setMaximumSize(new Dimension(getWidth(), (int) (getHeight() * 0.3)));
        iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        final JLabel iconLabel = new JLabel("Icon:");
        iconLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
        iconLabel.setFont(font.deriveFont(14f));
        iconLabel.setVisible(true);
        iconPanel.add(iconLabel);

        final Component spacer6 = Box.createRigidArea(new Dimension((int) (getWidth() * 0.01), (int) (getHeight() * 0.02)));
        iconPanel.add(spacer6);

        icons.setFont(font.deriveFont(Font.PLAIN, 12));
        icons.setAlignmentX(Box.LEFT_ALIGNMENT);
        icons.setPreferredSize(new Dimension((int) (getWidth() * 0.6), (int) (getHeight() * 0.08)));
        iconPanel.add(icons);

        // setup name panel
        final JPanel namePanel = new JPanel();
        namePanel.setBackground(greenBackground);
        namePanel.setMaximumSize(new Dimension(getWidth(), (int) (getHeight() * 0.3)));
        namePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        final JLabel nameLabel = new JLabel("Name:");
        nameLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
        nameLabel.setFont(font.deriveFont(14f));
        nameLabel.setVisible(true);
        namePanel.add(nameLabel);

        final Component spacer7 = Box.createRigidArea(new Dimension((int) (getWidth() * 0.01), (int) (getHeight() * 0.02)));
        add(spacer7);

        playerName.setFont(font.deriveFont(Font.PLAIN, 12));
        playerName.setAlignmentX(Box.LEFT_ALIGNMENT);
        playerName.setPreferredSize(new Dimension((int) (getWidth() * 0.6), (int) (getHeight() * 0.08)));

        playerName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {update();}

            @Override
            public void removeUpdate(DocumentEvent e) {update();}

            @Override
            public void changedUpdate(DocumentEvent e) {update();}

            private void update() {addPlayer.setEnabled(playerName.getText() != null && !playerName.getText().isEmpty() && icons.getSelectedItem() != null);}

        });
        namePanel.add(playerName);
        add(namePanel);

        // add icon panel second to last
        add(iconPanel);

        // add button panel last
        add(buttonPanel);
    }

}