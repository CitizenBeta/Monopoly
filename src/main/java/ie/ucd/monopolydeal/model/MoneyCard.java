package ie.ucd.monopolydeal.model;

public class MoneyCard implements Card {
    private final String name;
    private final int value;

    public MoneyCard(String name, int value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getValue() {
        return value;
    }
}