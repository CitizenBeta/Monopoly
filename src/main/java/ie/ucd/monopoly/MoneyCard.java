package ie.ucd.monopoly;

public enum MoneyCard implements Card{

    ONE_MILLION("1M",1),
    TWO_MILLION("2M",2),
    THREE_MILLION("3M",3),
    FOUR_MILLION("4M",4),
    FIVE_MILLION("5M",5),
    TEN_MILLION("10M",10);


    private String name;

    private int amount;

    MoneyCard(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    public void exert(Player player){
        return;
    }

    public String getName(){
        return name;
    }

    public int getAmount() {
        return amount;
    }
}
