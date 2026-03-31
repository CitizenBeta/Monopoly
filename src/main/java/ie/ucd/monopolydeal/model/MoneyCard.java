package ie.ucd.monopolydeal.model;

public record MoneyCard(String name, int value) implements Card {
    @Override
    public String toString() {
        return name + " [Money, " + value + "M]";
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public int getValue(){
        return value;
    }


}
