package ie.ucd.monopolydeal.game;

import ie.ucd.monopolydeal.model.*;
import java.util.List;

public interface DecisionMaker {
    Player selectNextPlayer(Player currentPlayer, List<Player> players, String prompt);
    Color selectColor(String prompt, List<Color> players);
    UseMode useCard(ActionCard action);
    WildPropertyCard selectWildCardToMove(Player current, List<WildPropertyCard> wildCards);
    Card selectDiscard(Player current, List<Card> cards);
    Card selectPropertyCard(Player owner, List<Card> cards, String prompt);
    Card selectPaymentCard(Player owner, List<Card> cards, String prompt);
    boolean reconfirm(String prompt);
}
