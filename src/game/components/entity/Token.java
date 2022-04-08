package game.components.entity;

import game.components.property.Property;

public abstract class Token {

    private String name;
    private int money;

    private Property location;

    public Token(String name) {
        setName(name);
        setMoney(1500);


    }


    public abstract void movePlayer(int distance);

    // getters & setters
    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public int getMoney() {return money;}

    public void setMoney(int money) {this.money = money;}

    public Property getLocation() {return location;}

    public void setLocation(Property location) {this.location = location;}

}