package game.components.special;

import game.Game;
import game.components.entity.Token;
import game.components.property.Property;

import java.util.Random;

public enum Chance {

    ADVANCE_BW(""), ADVANCE_GO(""), ADVANCE_IA(""), ADVANCE_SCP(""), ADVANCE_RR(""), ADVANCE_UTIL(""),
    BANK_PAY(""), GOJF_CARD(""), GO_TO_JAIL(""), REPAIRS(""), SPEEDING_FINE(""), TRIP_RR(""), ELECTED_CHAIRMAN(""),
    BUILDING_LOAN_MATURES("");

    Chance(String description) {

    }

    public static Chance draw() {return values()[new Random().nextInt(values().length)];}

    public void apply(Token token) {
        switch (this) {

            case ADVANCE_BW: {
                token.setLocation(Game.INSTANCE.getByPosition(1, 10));
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" was advanced to \"Boardwalk\".");
                break;
            }

            case ADVANCE_GO: {
                token.setLocation(Game.INSTANCE.getByPosition(0, 10));
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" was advanced to go and collected $200.");
                token.setMoney(token.getMoney() + 200);
                break;
            }

            case ADVANCE_IA: {
                token.setLocation(Game.INSTANCE.getByPosition(10, 4));
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" was advanced to \"ILLINOIS AVENUE\".");
                break;
            }

            case ADVANCE_SCP: {
                token.setLocation(Game.INSTANCE.getByPosition(1, 0));
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" was advanced to \"ST. CHARLES PLACE\".");
                break;
            }

            case ADVANCE_RR: {
                final Property property = token.findNearest(Property.Group.STATIONS);
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" was advanced to \"" + property.getName() + "\".");

                // TODO if owned pay owner twice the rent

                break;
            }

            case ADVANCE_UTIL: {
                final Property property = token.findNearest(Property.Group.UTILITIES);
                Game.INSTANCE.getBoard().getCenter().getLogBox().append("\n\"" + token.getName() + "\" was advanced to \"" + property.getName() + "\".");

                // TODO if owned pay owner 10x the rent

                break;
            }


            default: {break;}
        }

        Game.INSTANCE.getBoard().repaint();
    }

}