package ie.ucd.monopolydeal.model;

import java.util.ArrayList;
import java.util.List;

public class PropertySet {
    private final String color;
    private final int requiredCount;
    private final List<Card> cards;

    public PropertySet(String color, int requiredCount) {
        this.color = color;
        this.requiredCount = requiredCount;
        this.cards = new ArrayList<>();
    }

    public boolean addCard(Card card) {
        if (card == null) return false;
        if (cards.size() >= requiredCount) return false;
        return cards.add(card);
    }

    public boolean removeCard(Card card) {
        return cards.remove(card);
    }

    public boolean isComplete() {
        return cards.size() == requiredCount;
    }

    public String getColor() {
        return color;
    }

    public int getRequiredCount() {
        return requiredCount;
    }

    public int getCurrentCount() {
        return cards.size();
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }
}