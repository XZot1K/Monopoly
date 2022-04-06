package game.components.entity;

public abstract class Player {

    private String name;
    private int money, position;

    public Player(String name) {
        setName(name);
        setMoney(1500);
        setPosition(0);
    }


    // getters & setters
    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public int getMoney() {return money;}

    public void setMoney(int money) {this.money = money;}

    public int getPosition() {return position;}

    public void setPosition(int position) {this.position = position;}

}