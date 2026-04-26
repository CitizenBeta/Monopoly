package ie.ucd.monopolydeal.model;

public class MoneyCard implements Card {
    private final String name;
    private final int bankValue;

    public MoneyCard(String name, int bankValue) {
        this.name = name;
        this.bankValue = bankValue;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getBankValue() {
        return bankValue;
    }

    @Override
    public String getDetail() {
        return name + " [Money, " + bankValue + "M]";
    }

    @Override
    public String toString() {
        return getDetail();
    }
}