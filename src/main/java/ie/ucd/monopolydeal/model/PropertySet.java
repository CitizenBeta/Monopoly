package ie.ucd.monopolydeal.model;

import java.util.ArrayList;
import java.util.List;

public class PropertySet {
    private final PropertyColor color;
    private final List<Card> cards = new ArrayList<>();
    private ActionCard houseCard;
    private ActionCard hotelCard;

    public PropertySet(PropertyColor color) {
        this.color = color;
    }

    public PropertyColor getColor() {
        return color;
    }

    public List<Card> getCards() {
        return cards;
    }

    public List<Card> getUpgradeCards() {
        List<Card> upgrades = new ArrayList<>();
        if (houseCard != null) {
            upgrades.add(houseCard);
        }
        if (hotelCard != null) {
            upgrades.add(hotelCard);
        }
        return upgrades;
    }

    public List<Card> getAllCards() {
        List<Card> allCards = new ArrayList<>(cards);
        allCards.addAll(getUpgradeCards());
        return allCards;
    }

    public ActionCard getHouseCard() {
        return houseCard;
    }

    public ActionCard getHotelCard() {
        return hotelCard;
    }

    public int getPropertyCount() {
        return cards.size();
    }

    public int getHouseCount() {
        if (houseCard == null) {
            return 0;
        }
        return 1;
    }

    public int getHotelCount() {
        if (hotelCard == null) {
            return 0;
        }
        return 1;
    }

    public boolean isFullSet() {
        return cards.size() >= color.getSize();
    }

    public boolean addProperty(Card card) {
        if (!canAddProperty()) {
            return false;
        }
        cards.add(card);
        return true;
    }

    public void removeProperty(Card card) {
        cards.remove(card);
    }

    public boolean canAddProperty() {
        return cards.size() < color.getSize();
    }

    public boolean addHouse(ActionCard card) {
        if (!canAddHouse() || card == null || card.getActionType() != ActionType.HOUSE) {
            return false;
        }
        houseCard = card;
        return true;
    }

    public boolean addHotel(ActionCard card) {
        if (!canAddHotel() || card == null || card.getActionType() != ActionType.HOTEL) {
            return false;
        }
        hotelCard = card;
        return true;
    }

    public boolean removeUpgradeCard(Card card) {
        if (card == houseCard) {
            houseCard = null;
            if (hotelCard != null) {
                hotelCard = null;
            }
            return true;
        }

        if (card == hotelCard) {
            hotelCard = null;
            return true;
        }

        return false;
    }

    public void removeHouse() {
        houseCard = null;
        hotelCard = null;
    }

    public void removeHotel() {
        hotelCard = null;
    }

    public boolean canAddHouse() {
        return isFullSet() && houseCard == null && hotelCard == null;
    }

    public boolean canAddHotel() {
        return isFullSet() && hotelCard == null && houseCard != null;
    }

    private void clearUpgrades() {
        houseCard = null;
        hotelCard = null;
    }

    public int calculateRent() {
        if (cards.isEmpty()) {
            return 0;
        }
        int rent = color.getRent(cards.size());
        if (isFullSet()) {
            rent += getHouseCount() * 3;
            rent += getHotelCount() * 4;
        }
        return rent;
    }

    public void transferUpgradesTo(PropertySet target) {
        if (houseCard != null && target.houseCard == null) {
            target.houseCard = houseCard;
        }
        if (hotelCard != null && target.hotelCard == null) {
            target.hotelCard = hotelCard;
        }
        clearUpgrades();
    }

    public void restore(List<Card> propertyCards, ActionCard houseCard, ActionCard hotelCard) {
        cards.clear();
        cards.addAll(propertyCards);
        this.houseCard = houseCard;
        this.hotelCard = hotelCard;
    }

    public String summary() {
        return color.getName() + " set: " + cards.size() + "/" + color.getSize() +
                ", rent=" + calculateRent() + "M, house=" + getHouseCount() + ", hotel=" + getHotelCount();
    }
}
