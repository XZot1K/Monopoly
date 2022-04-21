package game.components.special;

import game.Game;
import game.components.entity.Token;
import game.components.property.Property;

import javax.swing.*;
import java.util.Random;

public enum Chance {

    ADVANCE_BW("Advance to Boardwalk."),
    ADVANCE_GO("Advance to Go (Collect $200)"),
    ADVANCE_IA("Advance to Illinois Avenue. If you pass Go, collect $200."),
    ADVANCE_SCP("Advance to St. Charles Place. If you pass Go, collect $200."),
    ADVANCE_RR("Advance to the nearest Railroad. If unowned, you may buy it from the Bank."
            + " If owned, pay wonder twice the rental to which they are otherwise entitled"),
    ADVANCE_UTIL("Advance token to nearest Utility. If unowned, you may buy it from the"
            + " Bank. If owned, throw dice and pay owner a total ten times amount thrown."),
    BANK_PAY("Bank pays you dividend of $50."),
    GOJF_CARD("Get Out of Jail Free Card."),
    MOVE_BACK("Go back 3 spaces."),
    GO_TO_JAIL("Go to Jail. Go directly to Jail, do not pass Go, do not collect $200"),
    REPAIRS("Make general repairs on all your property. For each house pay $25. For each hotel pay $100."),
    SPEEDING_FINE("Speeding fine $15."),
    TRIP_RR("Take a trip to Reading Railroad. If you pass Go, collect $200."),
    ELECTED_CHAIRMAN("You have been elected Chairman of the Board. Pay each player $50."),
    BUILDING_LOAN_MATURES("Your building loan matures. Collect $150.");

    private final String description;

    Chance(String description) {
        this.description = description;
    }

    public static Chance draw() {return values()[new Random().nextInt(values().length)];}

    public void apply(Token token) {

        switch (this) {

            case ADVANCE_BW: {
                token.setLocation(Game.INSTANCE.getByPosition(39)); // move player to board walk

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" was advanced to \"Boardwalk\".");
                break;
            }

            case ADVANCE_GO: {
                token.setLocation(Game.INSTANCE.getByPosition(0)); // move player to go

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" was advanced to go and collected $200.");
                token.setMoney(token.getMoney() + 200); // add $200 to the player
                break;
            }

            case ADVANCE_IA: {
                token.setLocation(Game.INSTANCE.getByPosition(23)); // move player to Illinois Avenue

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" was advanced to \"ILLINOIS AVENUE\".");
                break;
            }

            case ADVANCE_SCP: {
                token.setLocation(Game.INSTANCE.getByPosition(11)); // move player to St. Charles Place

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" was advanced to \"ST. CHARLES PLACE\".");
                break;
            }

            case ADVANCE_RR: {
                final Property property = token.findNearest(Property.Group.STATIONS); // get nearest player

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName()
                        + "\" was advanced to \"" + property.getName() + "\".");

                if (property.getOwner() != null && !property.getOwner().getName().equals(token.getName())) { // check ownership
                    // set current player's money
                    final int amountToRemove = Math.min(token.getMoney(), property.getRent());

                    // log action
                    Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" has paid \""
                            + property.getOwner().getName() + "\" $" + amountToRemove + " for rent.");

                    token.setMoney(token.getMoney() - amountToRemove); // remove the amount from the player

                    // give money to property owner
                    property.getOwner().setMoney(property.getOwner().getMoney() + amountToRemove);
                }

                break;
            }

            case ADVANCE_UTIL: {
                final Property property = token.findNearest(Property.Group.UTILITIES); // get nearest player

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName()
                        + "\" was advanced to \"" + property.getName() + "\".");

                // TODO if owned pay owner 10x the rent
                if (property.getOwner() != null && !property.getOwner().getName().equals(token.getName())) {

                    // roll a number between 1 and 6 for the left & right die
                    Game.INSTANCE.getBoard().getCenter().setRoll((int) (Math.random() * 6 + 1), (int) (Math.random() * 6 + 1));

                    // set current player's money
                    final int amountToRemove = Math.min(token.getMoney(), (10 * (Game.INSTANCE.getBoard().getCenter().getRoll()[0]
                            + Game.INSTANCE.getBoard().getCenter().getRoll()[0])));
                    Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" has paid \""
                            + property.getOwner().getName() + "\" $" + amountToRemove + " for rent.");

                    token.setMoney(token.getMoney() - amountToRemove); // remove the amount from the player

                    // give money to property owner
                    property.getOwner().setMoney(property.getOwner().getMoney() + amountToRemove);

                }

                break;
            }

            case REPAIRS: {
                int amount = 0; // counter
                for (Property property : Game.INSTANCE.getOwnedProperties(token.getName())) { // loop owned properties
                    amount += (property.getHouses() * 25); // add $25 per house
                    amount += (property.getHotels() * 100); // add $100 per hotel
                }

                amount = Math.min(amount, token.getMoney()); // get minimum between player
                token.setMoney(token.getMoney() - amount); // remove the amount from the player
                Game.INSTANCE.setJackpot(Game.INSTANCE.getJackpot() + amount); // add the amount to the jackpot

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" has paid $" + amount + " for general repairs.");
                break;
            }

            case BANK_PAY: {
                token.setMoney(token.getMoney() + 50); // add $50 to the player

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" has been paid $50 by the bank.");
                break;
            }

            case GO_TO_JAIL: {
                // send player to jail
                token.setInJail(true);
                token.setJailCounter(0);
                token.setLocation(Game.INSTANCE.getByPosition(10));

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + token.getName() + " has been sent to jail.");
                Game.INSTANCE.nextTurn(); // next turn
                break;
            }

            case TRIP_RR: {
                // move player to reading railroad
                token.travel("READING RAILROAD", true);
                break;
            }

            case MOVE_BACK: {
                // move player back 3 spaces
                token.move(-3);

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" was moved back 3 spaces.");
                break;
            }

            case SPEEDING_FINE: {
                final int amount = Math.min(15, token.getMoney()); // get minimum between player balance and $15
                token.setMoney(token.getMoney() - amount); // remove the amount from the player
                Game.INSTANCE.setJackpot(Game.INSTANCE.getJackpot() + amount); // add the amount to the jackpot

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" has paid $" + amount + " for a speeding fine.");
                break;
            }

            case ELECTED_CHAIRMAN: {

                // pay $50 to each player
                for (Token player : Game.INSTANCE.getPlayers()) {
                    if (player.getName().equalsIgnoreCase(token.getName())) continue; // skip the current player (who is paying)

                    player.setMoney(player.getMoney() + 50); // pay the player
                    token.setMoney(token.getMoney() - 50); // remove 50 from player

                    if (!Game.INSTANCE.getPlayers().contains(token)) break; // break the loop if the current player went bankrupt
                }

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" became chairman and has paid $50 to all players.");
                break;
            }

            case BUILDING_LOAN_MATURES: {
                token.setMoney(token.getMoney() + 150); // give player $150

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" has collected $150 from their building load maturing.");
                break;
            }

            case GOJF_CARD: {

                if (token.hasGOJCard()) { // check if player has GOJ card
                    token.setMoney(token.getMoney() + 50); // give player $50 in exchange since they already have one

                    // log action
                    Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" drew a \"Get Out of Jail Free\" Card,"
                            + " but already has one in possession so instead collected $50.");
                } else {
                    // give player GOJ card and update whether they are selling it to 'false'
                    token.setGOJCard(true);
                    token.setSellingGOJCard(false);

                    // log action
                    Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" has collected a \"Get Out of Jail Free\" Card.");
                }

                break;
            }

            default: {break;}
        }

        // create dialog displaying the card's description
        JOptionPane.showMessageDialog(null, getDescription(), "CHANCE", JOptionPane.INFORMATION_MESSAGE);
        Game.INSTANCE.updateBoard(); // update board
    }

    public String getDescription() {return description;}

}