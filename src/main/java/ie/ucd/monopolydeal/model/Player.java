package ie.ucd.monopolydeal.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    public static final int MAX_CARDS_AT_HAND = 7;
    public static final int MAX_ACTIONS_PER_TURN = 3;

    private final String name;
    private final int number;
    private final List<Card> cardsAtHand = new ArrayList<>();
    private final List<Card> cardsAtBank = new ArrayList<>();

    public Player(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public List<Card> getCardsAtHand() {
        return cardsAtHand;
    }

    public List<Card> getCardsAtBank() {
        return cardsAtBank;
    }

    public void addCardToHand(Card card) {
        if (card == null) {
            return;
        }
        cardsAtHand.add(card);
    }

    public void addCardToBank(Card card) {
        if (card == null) {
            return;
        }
        cardsAtHand.remove(card);
        cardsAtBank.add(card);
    }

    public boolean removeCardFromHand(Card card) {
        if(card == null){
            return false;
        }
        return cardsAtHand.remove(card);
    }

    public boolean removeCardFromBank(Card card) {
        if(card == null){
            return false;
        }
        return cardsAtBank.remove(card);
    }

    public int getBankTotalValue() {
        int total = 0;
        for (Card card : cardsAtBank) {
            total += card.getBankValue();
        }
        return total;
    }

    public boolean isHandFull() {
        return cardsAtHand.size() > MAX_CARDS_AT_HAND;
    }
}