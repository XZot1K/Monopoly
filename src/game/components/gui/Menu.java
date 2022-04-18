package game.components.gui;

import game.Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Menu extends JFrame {

    private final Menu MENU_INSTANCE;

    public Menu() {
        this.MENU_INSTANCE = this;

        setTitle("Monopoly - Main Menu");
        setSize(500, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(getWidth(), getHeight()));
        setIconImage(Game.INSTANCE.getIcon());

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocationRelativeTo(null);
        setLocation((int) ((screenSize.getWidth() / 2) - (getPreferredSize().getWidth() / 2)),
                (int) ((screenSize.getHeight() / 2) - (getPreferredSize().getHeight() / 2)));

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        final Color greenBackground = new Color(144, 238, 144); // a nice green
        getContentPane().setBackground(greenBackground); // update the content pane's background color

        final Component spacer = Box.createRigidArea(new Dimension(0, (int) (getHeight() * 0.35)));
        add(spacer);

        final Font font = new Font("Arial Black", Font.BOLD, 14);

        JButton loadButton = new JButton("Load");
        loadButton.setFont(font);
        loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadButton.setMaximumSize(new Dimension((int) (getWidth() * 0.55), (int) (getHeight() * 0.17)));
        loadButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(new File("/"));
                fileChooser.setDialogTitle("Select the Monopoly save: ");
                fileChooser.setFileFilter(new FileNameExtensionFilter(".monopoly", ".monopoly"));

                if (fileChooser.showDialog(MENU_INSTANCE, "Load") == JFileChooser.APPROVE_OPTION) {

                    final File file = fileChooser.getSelectedFile();

                    if (file == null || !file.exists() || !file.getName().toLowerCase().endsWith(".monopoly")) // check if the file is a monopoly save
                    {
                        // open error message that it wasn't
                        JOptionPane.showMessageDialog(MENU_INSTANCE, "The selected save was unable to be read.",
                                "Loading Save Failed", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // TODO load the save
                }
            }
        });
        add(loadButton, BorderLayout.CENTER);

        final Component spacer2 = Box.createRigidArea(new Dimension(0, (int) (getHeight() * 0.01)));
        add(spacer2);

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

        final Component spacer3 = Box.createRigidArea(new Dimension(0, (int) (getHeight() * 0.01)));
        add(spacer3);

        JButton quitButton = new JButton("Quit");
        quitButton.setFont(font);
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitButton.setMaximumSize(new Dimension((int) (getWidth() * 0.55), (int) (getHeight() * 0.17)));
        quitButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {System.exit(0);}
        });
        add(quitButton, BorderLayout.CENTER);

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

        g2d.setTransform(original);
    }

}