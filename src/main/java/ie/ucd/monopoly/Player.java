package ie.ucd.monopoly;

import java.util.ArrayList;

public class Player {
    private String name;

    private ArrayList<Card> cardPacket = new ArrayList<>();

    private int money = 0;

    public void addCard(Card card){
        cardPacket.add(card);
    }

    public void consumeCard(Card card){
        card.exert(this);
        cardPacket.remove(card);
    }
}
