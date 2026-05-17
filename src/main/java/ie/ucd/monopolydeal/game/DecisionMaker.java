package ie.ucd.monopolydeal.game;

import ie.ucd.monopolydeal.model.*;
import java.util.List;

public interface DecisionMaker {
    Player selectNextPlayer(Player currentPlayer, List<Player> players, String prompt);
    PropertyColor selectColor(String prompt, List<PropertyColor> players);
    UseMode useCard(ActionCard action);
    WildPropertyCard selectWildCardToMove(Player current, List<WildPropertyCard> wildCards);
    List<Card> selectDiscards(Player current, List<Card> cards, int count);
    Card selectPropertyCard(Player owner, List<Card> cards, String prompt);
    List<Card> selectPaymentCards(Player owner, List<Card> cards, int amount);
    boolean reconfirm(String prompt);
}
