package game.components.gui;

import game.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

public class Creation extends JFrame {

    private final Game INSTANCE; // game instance

    public Creation(Game gameInstance) {
        INSTANCE = gameInstance; // game instance pass-through

        URL logo = Game.class.getResource("/resources/tokens/thimble.png");
        if (logo != null) setIconImage(new ImageIcon(logo).getImage());

        setSize(500, 600);
        setTitle("Monopoly - Player Creation"); // update title of frame
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // exit on close
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        final Color greenBackground = new Color(144, 238, 144); // a nice green
        getContentPane().setBackground(greenBackground); // update the content pane's background color

        final Font font = new Font("Arial Black", Font.BOLD, 14);

        final Component spacer = Box.createRigidArea(new Dimension((int) (getWidth() * 0.01), (int) (getHeight() * 0.01)));
        add(spacer);

        final JLabel cpLabel = new JLabel("Current Players:");
        cpLabel.setFont(font);
        cpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cpLabel.setVisible(true);
        add(cpLabel);

        final Component spacer2 = Box.createRigidArea(new Dimension((int) (getWidth() * 0.01), (int) (getWidth() * 0.01)));
        add(spacer2);

        final JList<String> playerList = new JList<>(new DefaultListModel<>());
        final JScrollPane listScroll = new JScrollPane(playerList);
        listScroll.setMaximumSize(new Dimension((int) (getWidth() * 0.3), (int) (getHeight() * 0.25)));
        listScroll.setAlignmentX(LEFT_ALIGNMENT);

        add(listScroll);
        add(Box.createHorizontalGlue());

        final Component spacer3 = Box.createRigidArea(new Dimension((int) (getWidth() * 0.01), 0));
        add(spacer3);
        add(Box.createHorizontalGlue());

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setMaximumSize(new Dimension(250, 80));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setVisible(true);
        add(buttonPanel);

        /*final Component spacer = Box.createRigidArea(new Dimension(0, (int) (getHeight() * 0.01)));
        add(spacer);

        final Component spacer3 = Box.createRigidArea(new Dimension((int) (getWidth() * 0.012), (int) (getWidth() * 0.012)));
        add(spacer3);
        add(Box.createHorizontalGlue());

        final JList<String> playerList = new JList<>();
        add(new JScrollPane(playerList));
        add(Box.createHorizontalGlue());

        final Component spacer4 = Box.createRigidArea(new Dimension((int) (getWidth() * 0.05), 0));
        add(spacer4);
        add(Box.createHorizontalGlue());

        final JPanel buttonPanel = new JPanel();
        cpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setVisible(true);
        add(buttonPanel);*/


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);

                new Menu(gameInstance);
            }
        });

        setVisible(true);
    }

}