/*
    Jeremiah Osborne
    Date: 4/1/2022
 */

package game;

import game.components.entity.Token;
import game.components.gui.Menu;
import game.components.gui.board.Board;
import game.components.property.Position;
import game.components.property.Property;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class Game {

    public static Game INSTANCE; // global instance so it can be referred at any time

    private final ArrayList<Token> players; // list of players (Token)

    private final ArrayList<Property> properties; // the properties list

    // these are used to handle the custom drawn tooltips (BOTH will be NULL when a property card is not hovered on)
    private Property selectedProperty; // the selected property card (can be NULL)

    private Token currentPlayerTurn; // current player's turn;

    private Board board; // the current game board

    private int enteredSize, // stored entry for size
            jackpot; // the jackpot won from landing on free parking

    public Game(int size) { // constructor
        INSTANCE = this; // initialize the static instance of the game
        players = new ArrayList<>(); // initialize the players list
        properties = new ArrayList<>(); // initialize the property list

        // initialize values
        setCurrentPlayerTurn(null);
        setEnteredSize(size);
        setJackpot(0);

        setupProperties(); // initialize and add all the properties to a list in-memory

        new Menu(); // new menu frame
    }

    public static void main(String[] args) {

        // sets the look & feel of the game to that of the system default, if the class can be acuired
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
                 | IllegalAccessException e) {e.printStackTrace();} // print the stacktrace

        int size = 1000;

        if (args.length > 0) // if there were args
            try {
                size = Integer.parseUnsignedInt(args[0]); // assuming only one argument & that it will always be positive
            } catch (Exception e) {
                System.out.println("Error: The passed arg was not a valid integer, launching with defaults...");
            }

        new Game(size); // new game instance (initializes without creating static references)
    }

    /**
     * @return Get the thimble image.
     */
    public Image getIcon() {
        URL logo = Game.class.getResource("/resources/tokens/thimble.png"); // load the image
        if (logo != null) return new ImageIcon(logo).getImage(); // return an image icon
        return null;
    }

    // property methods
    private void setupProperties() {

        /*
         * For each property it is first created given a name, group, value/cost, and an array of positions
         * given a simple position (0-39) and its actual layout coordinates.
         */

        // corners
        getProperties().add(new Property("GO", Property.Group.NONE, 0, 0, 0, new Position(0, 0, 10)));
        getProperties().add(new Property("IN JAIL/JUST VISITING", Property.Group.NONE, 0, 0, 0, new Position(10, 0, 0)));
        getProperties().add(new Property("FREE PARKING", Property.Group.NONE, 0, 0, 0, new Position(20, 10, 0)));
        getProperties().add(new Property("GO TO JAIL", Property.Group.NONE, 0, 0, 0, new Position(30, 10, 10)));

        // chance & community chest
        getProperties().add(new Property("COMMUNITY CHEST", Property.Group.NONE, 0, 0, 0, new Position(2, 0, 8)));
        getProperties().add(new Property("COMMUNITY CHEST", Property.Group.NONE, 0, 0, 0, new Position(17, 7, 0)));
        getProperties().add(new Property("COMMUNITY CHEST", Property.Group.NONE, 0, 0, 0, new Position(33, 7, 10)));
        getProperties().add(new Property("CHANCE", Property.Group.NONE, 0, 0, 0, new Position(7, 0, 3)));
        getProperties().add(new Property("CHANCE", Property.Group.NONE, 0, 0, 0, new Position(22, 10, 2)));
        getProperties().add(new Property("CHANCE", Property.Group.NONE, 0, 0, 0, new Position(36, 4, 10)));

        // tax properties
        getProperties().add(new Property("INCOME TAX", Property.Group.NONE, 0, 200, 0, new Position(4, 0, 6)));
        getProperties().add(new Property("LUXURY TAX", Property.Group.NONE, 0, 100, 0, new Position(38, 2, 10)));

        // utility properties
        getProperties().add(new Property("ELECTRIC COMPANY", Property.Group.UTILITIES, 150, 0, 75, new Position(12, 2, 0)));
        getProperties().add(new Property("WATER WORKS", Property.Group.UTILITIES, 150, 0, 75, new Position(28, 10, 8)));

        // railroad-station properties
        getProperties().add(new Property("READING RAILROAD", Property.Group.STATIONS, 200, 25, 100, new Position(5, 0, 5)));
        getProperties().add(new Property("PENNSYLVANIA RAILROAD", Property.Group.STATIONS, 200, 25, 100, new Position(15, 5, 0)));
        getProperties().add(new Property("B & O. RAILROAD", Property.Group.STATIONS, 200, 25, 100, new Position(25, 10, 5)));
        getProperties().add(new Property("SHORT LINE", Property.Group.STATIONS, 200, 25, 100, new Position(35, 5, 10)));

        // brown properties
        getProperties().add(new Property("MEDITERRANEAN AVENUE", Property.Group.BROWN, 60, 2, 30, new Position(1, 0, 9)));
        getProperties().add(new Property("BALTIC AVENUE", Property.Group.BROWN, 60, 4, 30, new Position(3, 0, 7)));

        // light-blue properties
        getProperties().add(new Property("ORIENTAL AVENUE", Property.Group.LIGHT_BLUE, 100, 6, 50, new Position(6, 0, 4)));
        getProperties().add(new Property("VERMONT AVENUE", Property.Group.LIGHT_BLUE, 100, 6, 50, new Position(8, 0, 2)));
        getProperties().add(new Property("CONNECTICUT AVENUE", Property.Group.LIGHT_BLUE, 120, 8, 60, new Position(9, 0, 1)));

        // magenta/pink properties
        getProperties().add(new Property("ST. CHARLES PLACE", Property.Group.PINK, 140, 10, 70, new Position(11, 1, 0)));
        getProperties().add(new Property("STATES AVENUE", Property.Group.PINK, 140, 10, 70, new Position(13, 3, 0)));
        getProperties().add(new Property("VIRGINIA AVENUE", Property.Group.PINK, 160, 12, 80, new Position(14, 4, 0)));

        // orange properties
        getProperties().add(new Property("ST. JAMES PLACE", Property.Group.ORANGE, 180, 14, 90, new Position(16, 6, 0)));
        getProperties().add(new Property("TENNESSEE AVENUE", Property.Group.ORANGE, 180, 14, 90, new Position(18, 8, 0)));
        getProperties().add(new Property("NEW YORK AVENUE", Property.Group.ORANGE, 200, 16, 100, new Position(19, 9, 0)));

        // red properties
        getProperties().add(new Property("KENTUCKY AVENUE", Property.Group.RED, 220, 18, 110, new Position(21, 10, 1)));
        getProperties().add(new Property("INDIANA AVENUE", Property.Group.RED, 220, 18, 110, new Position(23, 10, 3)));
        getProperties().add(new Property("ILLINOIS AVENUE", Property.Group.RED, 240, 20, 120, new Position(24, 10, 4)));

        // yellow properties
        getProperties().add(new Property("ATLANTIC AVENUE", Property.Group.YELLOW, 260, 22, 130, new Position(26, 10, 6)));
        getProperties().add(new Property("VENTNOR AVENUE", Property.Group.YELLOW, 260, 22, 130, new Position(27, 10, 7)));
        getProperties().add(new Property("MARVIN GARDENS", Property.Group.YELLOW, 280, 24, 140, new Position(29, 10, 9)));

        // green properties
        getProperties().add(new Property("PACIFIC AVENUE", Property.Group.GREEN, 300, 26, 150, new Position(31, 9, 10)));
        getProperties().add(new Property("NORTH CAROLINA AVENUE", Property.Group.GREEN, 300, 26, 150, new Position(32, 8, 10)));
        getProperties().add(new Property("PENNSYLVANIA AVENUE", Property.Group.GREEN, 320, 28, 160, new Position(34, 6, 10)));

        // dark-blue properties
        getProperties().add(new Property("PARK PLACE", Property.Group.DARK_BLUE, 350, 35, 175, new Position(37, 3, 10)));
        getProperties().add(new Property("BOARDWALK", Property.Group.DARK_BLUE, 400, 50, 200, new Position(39, 1, 10)));
    }

    /**
     * @param position The position to filter by, can be either 0-39 or the layout coordinates x: <0-10>, y: <0-10>.
     * @return The property at the defined position, CAN RETURN NULL.
     */
    public Property getByPosition(int... position) {
        // search for property with position
        Optional<Property> propertyOptional = getProperties().stream().filter(property -> property.isSame(position)).findFirst();
        return propertyOptional.orElse(null);
    }

    /**
     * @param x The x coordinate to get the simple from.
     * @param y The y coordinate to get the simple from.
     * @return The found simple position.
     */
    public int getSimplePosition(int x, int y) {
        // find the property given the x & y
        final Optional<Property> propertyOptional = getProperties().stream().filter(property -> property.isSame(x, y)).findFirst();
        // return the simple position associated to it, -1 if none is found
        return propertyOptional.map(property -> property.getPosition().getSimplePosition()).orElse(-1);
    }

    /**
     * @param group The group to filter properties by.
     * @return The list of properties in the defined group.
     */
    public Collection<Property> getProperties(Property.Group group) {
        return getProperties().stream().filter(property ->
                property.getGroup() == group).collect(Collectors.toList());
    }

    /**
     * @param group      The group to filter properties by.
     * @param playerName The player to check for ownership.
     * @return The list of properties in the defined group.
     */
    public Collection<Property> getOwnedProperties(Property.Group group, String playerName) {
        return getProperties().stream().filter(property -> (property.getGroup() == group && property.getOwner() != null
                && property.getOwner().getName().equalsIgnoreCase(playerName))).collect(Collectors.toList());
    }

    /**
     * @param playerName The player to check for ownership.
     * @return The list of properties in the defined group.
     */
    public Collection<Property> getOwnedProperties(String playerName) {
        return getProperties().stream().filter(property -> (property.getOwner() != null
                && property.getOwner().getName().equalsIgnoreCase(playerName))).collect(Collectors.toList());
    }

    /**
     * @param group      The group to collect properties.
     * @param playerName The player to check ownership.
     * @return Whether the player owns all properties under the specified group.
     */
    public boolean ownsAll(Property.Group group, String playerName) {
        for (Property property : getProperties().stream().filter(property ->
                (property.getGroup() == group)).collect(Collectors.toList())) {
            if (property.getOwner() == null || (property.getOwner() != null
                    && !property.getOwner().getName().equalsIgnoreCase(playerName)))
                return false;
        }
        return true;
    }

    /**
     * @param playerName The name for comparison.
     * @return The token associated to the player with the given name.
     */
    public Token getPlayerByName(String playerName) {
        return getPlayers().stream().filter(t ->
                t.getName().equalsIgnoreCase(playerName)).findFirst().orElse(null);
    }

    /**
     * @return The token associated to the player who's turn is up next.
     */
    public Token getNextTurnPlayer() {
        for (int i = -1; ++i < getPlayers().size(); ) { // loop players
            final Token token = getPlayers().get(i); // get player
            if (token != getCurrentPlayerTurn()) continue; // continue until current turn player is found

            // determine the next player index
            if ((i + 1) >= getPlayers().size()) return getPlayers().get(0);

            return getPlayers().get(i + 1); // return next index player
        }

        return getPlayers().get(0); // return first player in the list
    }

    /**
     * @return Gets any 1 token that is selling a GOJ card.
     */
    public Token getGOJSellingPlayer() {
        final Optional<Token> sellingToken = Game.INSTANCE.getPlayers().stream()
                .filter(token -> (token.hasGOJCard() && token.isSellingGOJCard())).findAny();
        return sellingToken.orElse(null);
    }

    /**
     * moves on to the next turn
     */
    public void nextTurn() {
        final Token upNextPlayer = Game.INSTANCE.getNextTurnPlayer(); // get next player
        setCurrentPlayerTurn(upNextPlayer);

        if (getPlayers().size() <= 1) { // check if there is only 1 player, or less

            getBoard().dispose(); // dispose board frame

            // display winner message
            JOptionPane.showMessageDialog(null, "Congratulations \""
                            + upNextPlayer.getName() + "\", you have won the game of Monopoly!",
                    "Winner Winner Chicken Dinner", JOptionPane.INFORMATION_MESSAGE);

            setJackpot(0); // reset jackpot

            setCurrentPlayerTurn(null); // set current turn to null
            getPlayers().clear(); // clear players from list

            // reset all properties
            setSelectedProperty(null);
            getProperties().clear();
            setupProperties();

            // go back to main menu
            new Menu();
            return;
        }

        if (upNextPlayer.isInJail()) { // check if player is in jail

            if (upNextPlayer.getJailCounter() >= 3) { // check if the player has waited 3 turns

                // reset jail state
                upNextPlayer.setInJail(false);
                upNextPlayer.setJailCounter(0);

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + upNextPlayer.getName() + " has served their time and is no longer in jail!");

            } else
                upNextPlayer.setJailCounter(upNextPlayer.getJailCounter() + 1); // update turn counter for player's jail time
        }

        // check if instances of board components exist
        if (getBoard() != null && getBoard().getCenter() != null && getBoard().getCenter().getController() != null) {

            getBoard().getCenter().getController().setPostRoll(false); // update post roll to false

            // log action
            getBoard().getCenter().getLogBox().append("\nIt's now " + upNextPlayer.getName() + "'s turn!");
        }

        updateBoard(); // update board
    }

    /**
     * Begins a chain reaction which updates all board components.
     */
    public void updateBoard() {getBoard().repaint();}

    /**
     * Saves all data to a selected file.
     */
    public void save() {
        try {
            // create confirmation dialog
            final int response = JOptionPane.showConfirmDialog(null, "Would you like to save the game?",
                    "Save Prompt", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            // check for yes response
            if (response == JOptionPane.YES_OPTION) {
                JFileChooser fileChooser = new JFileChooser(); // request file selection
                fileChooser.setDialogTitle("Select a file to save");
                fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);

                // filter the files by the .monopoly extension
                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {return (!f.isDirectory() && f.getName().toLowerCase().endsWith(".monopoly"));}

                    @Override
                    public String getDescription() {return "*.monopoly";}
                });
                fileChooser.setDragEnabled(true);

                // check if save is clicked
                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();

                    // double check file for extension (update if not)
                    if (!fileToSave.getName().toLowerCase().endsWith(".monopoly"))
                        fileToSave = new File(fileToSave.getPath() + ".monopoly");

                    // new writer
                    final BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave, false));

                    // compact all player data into a single formatted string
                    final StringBuilder playerLine = new StringBuilder();
                    getPlayers().forEach(token -> {
                        if (playerLine.length() > 0) playerLine.append(":");
                        playerLine.append(token.toString());
                    });

                    // compact all property data (avoiding defaults) into a single formatted string
                    final StringBuilder propertyLine = new StringBuilder();
                    getProperties().forEach(property -> {
                        if (property.getOwner() != null) {
                            if (propertyLine.length() > 0) propertyLine.append(":");
                            propertyLine.append(property);
                        }
                    });

                    // write the player data line to the first line, property data to the second line, and log to the third
                    writer.write(playerLine.toString());
                    writer.write("\n" + propertyLine);
                    writer.write("\n" + Game.INSTANCE.getBoard().getCenter().getLogBox().getText().replace("\n", "|"));

                    writer.close(); //close the writer
                }
            }

        } catch (IOException e) {e.printStackTrace();} // print the exception (should not ever occur)
    }

    // getters & setters

    /**
     * @return The list of properties.
     */
    public ArrayList<Property> getProperties() {return properties;}

    /**
     * @return The property card currently selected.
     */
    public Property getSelectedProperty() {return selectedProperty;}

    /**
     * @param property The property card to set as selected.
     */
    public void setSelectedProperty(Property property) {this.selectedProperty = property;}

    /**
     * @return The current game board instance.
     */
    public Board getBoard() {return board;}

    /**
     * @param board The board to set.
     */
    public void setBoard(Board board) {this.board = board;}

    /**
     * @return The list of players (any object extending the token class).
     */
    public ArrayList<Token> getPlayers() {return players;}

    /**
     * @return The default or commandline entered size.
     */
    public int getEnteredSize() {return enteredSize;}

    /**
     * @param enteredSize The default or commandline entered size used for the board.
     */
    public void setEnteredSize(int enteredSize) {this.enteredSize = enteredSize;}

    /**
     * @return The token whose turn it currently is.
     */
    public Token getCurrentPlayerTurn() {return currentPlayerTurn;}

    /**
     * @param currentPlayerTurn The token to update as the current player turn.
     */
    public void setCurrentPlayerTurn(Token currentPlayerTurn) {this.currentPlayerTurn = currentPlayerTurn;}

    /**
     * @return The jackpot value for when free parking is landed on.
     */
    public int getJackpot() {return jackpot;}

    /**
     * @param jackpot The new jackpot value to set.
     */
    public void setJackpot(int jackpot) {this.jackpot = jackpot;}
}