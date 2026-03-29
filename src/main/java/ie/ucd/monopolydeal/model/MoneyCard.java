package ie.ucd.monopolydeal.model;

public record MoneyCard(String name, int value) implements Card {
    @Override
    public String toString() {
        return name + " [Money, " + value + "M]";
    }
}
