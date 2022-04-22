/*
    Jeremiah Osborne & Dominick Delgado
    Date: 4/1/2022
 */

package game.components.special;

import game.Game;
import game.components.entity.Token;
import game.components.property.Property;

import javax.swing.*;
import java.util.Random;

public enum CommunityChest {

    // all the community chest card possibilities
    ADVANCE_GO("Advance To Go (Collect $200)"),
    BANK_ERROR("Bank error in your favor. Collect $200."),
    DOCTOR_FEE("Doctor's fee. Pay $50."),
    FROM_STOCK_SALE("From sale of stock you get $50."),
    GOJF_CARD("Get Out of Jail Free"),
    GO_TO_JAIL("Go to Jail. Go directly to jail, do not pass Go, do not collect $200."),
    HOLIDAY_FUND("Holiday fund matures. Receive $100."),
    INCOME_TAX("Income tax refund. Collect $20."),
    BIRTHDAY("It is your birthday. Collect $10 from every player."),
    LIFE_INSURANCE("Life insurance matures. Collect $100."),
    HOSPITAL_FEE("Pay hospital fees of $100."),
    SCHOOL_FEE("Pay school fees of $50."),
    CONSULTANCY_FEE("Receive $25 consultancy fee."),
    STREET_REPAIR("You are assessed for street repair. $40 per house. $115 per hotel."),
    BEAUTY_CONTEST_PRIZE("You have won second prize in a beauty contest. Collect $10."),
    INHERIT("You inherit $100.");

    private final String description; // the description of the card

    CommunityChest(String description) {
        this.description = description;
    } // initialize values per card

    /**
     * @return A randomly drawn community chest card from the list of possibilities
     */
    public static CommunityChest draw() {return values()[new Random().nextInt(values().length)];}

    /**
     * @param token The token to apply the card selection's actions upon.
     */
    public void apply(Token token) {
        switch (this) {

            case BANK_ERROR: {
                token.setMoney(token.getMoney() + 200); // add $200 to the player

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" ran into a bank error and collected $200.");
                break;
            }

            case ADVANCE_GO: {
                token.setLocation(Game.INSTANCE.getByPosition(0)); // send player to GO
                token.setMoney(token.getMoney() + 200); // add $200 to the player

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" was advanced to go and collected $200.");
                break;
            }

            case DOCTOR_FEE: {
                token.setMoney(token.getMoney() - 50); // take $50 from the player
                Game.INSTANCE.setJackpot(Game.INSTANCE.getJackpot() + 50); // add $50 to the jackpot

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" paid a $50 Doctor's fee.");
                break;
            }

            case FROM_STOCK_SALE: {
                token.setMoney(token.getMoney() + 50); // add $50 to the player

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" collected $50 from a stocks sale.");
                break;
            }

            case SCHOOL_FEE: {
                token.setMoney(token.getMoney() - 50); // take $50 from the player
                Game.INSTANCE.setJackpot(Game.INSTANCE.getJackpot() + 50); // add $50 to the jackpot

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" paid a $50 school fee.");
                break;
            }

            case HOSPITAL_FEE: {
                token.setMoney(token.getMoney() - 100); // take $100 from the player
                Game.INSTANCE.setJackpot(Game.INSTANCE.getJackpot() + 100); // add $100 to the jackpot

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" paid a $100 hospital fee.");
                break;
            }

            case CONSULTANCY_FEE: {
                token.setMoney(token.getMoney() - 25); // take $25 from the player
                Game.INSTANCE.setJackpot(Game.INSTANCE.getJackpot() - 25); // add $25 to the jackpot

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" paid a $25 consultancy fee.");
                break;
            }

            case BEAUTY_CONTEST_PRIZE: {
                token.setMoney(token.getMoney() + 10); // add $10 to the player

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" collected $10 by winning second prize at a beauty contest.");
                break;
            }

            case INHERIT: {
                token.setMoney(token.getMoney() + 100); // add $100 to the player

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" inherited $100.");
                break;
            }

            case BIRTHDAY: {
                Game.INSTANCE.getPlayers().forEach(player -> {
                    if (!player.getName().equalsIgnoreCase(token.getName())) // make sure player is not the one who drew the card
                        player.setMoney(player.getMoney() - 10); // remove $10 from the player
                }); // take $10 from each the player
                token.setMoney(token.getMoney() + ((Game.INSTANCE.getPlayers().size() - 1) * 10)); // give $10 per player that is not the one who drew the card

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" collected $10 from each player. It's their birthday!");
                break;
            }

            case INCOME_TAX: {
                token.setMoney(token.getMoney() + 20); // ad $20 to the player

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" collected $20 from a tax refund.");
                break;
            }

            case HOLIDAY_FUND: {
                token.setMoney(token.getMoney() + 100); // add $100 to the player

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" collected $100 from a holiday fund.");
                break;
            }

            case STREET_REPAIR: {
                int amount = 0; // counter
                for (Property property : Game.INSTANCE.getOwnedProperties(token.getName())) { // loop owned properties
                    amount += (property.getHouses() * 40); // add $40 per house
                    amount += (property.getHotels() * 115); // add $115 per hotel
                }

                amount = Math.min(amount, token.getMoney()); // grab minimum between token
                token.setMoney(token.getMoney() - amount);
                Game.INSTANCE.setJackpot(Game.INSTANCE.getJackpot() + amount);

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" has paid $" + amount + " for street repairs.");
                break;
            }

            case LIFE_INSURANCE: {
                token.setMoney(token.getMoney() + 100); // add $100 to player

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" collected $100 from life insurance.");
                break;
            }

            case GOJF_CARD: {

                if (token.hasGOJCard()) { // check if the player has a GOJ card
                    token.setMoney(token.getMoney() + 50); // add $50 to player

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

            case GO_TO_JAIL: {
                // send the player to jail
                token.setInJail(true);
                token.setJailCounter(0);
                token.setLocation(Game.INSTANCE.getByPosition(10));

                // log action
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n" + token.getName() + " has been sent to jail.");

                Game.INSTANCE.nextTurn(); // next turn
                break;
            }


            default: {break;}
        }

        // create dialog displaying the card description
        JOptionPane.showMessageDialog(null, getDescription(), "COMMUNITY CHEST", JOptionPane.INFORMATION_MESSAGE);
        Game.INSTANCE.updateBoard(); // update board
    }

    /**
     * @return The card description.
     */
    public String getDescription() {return description;}

}