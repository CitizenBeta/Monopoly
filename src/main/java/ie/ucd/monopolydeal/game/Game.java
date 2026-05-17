package ie.ucd.monopolydeal.game;

import ie.ucd.monopolydeal.model.*;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<Player> players = new ArrayList<>();
    private int currentPlayerIndex;
    private int actionsUsed;
    private int turnCount;
    private boolean started;
    private Deck deck = new Deck() ;
    private List<UsedCard> usedCards = new ArrayList<>();
    private boolean gameOver;

    public void setup(List<String> names) {
        players.clear();
        usedCards.clear();
        deck = new Deck();
        currentPlayerIndex = 0;
        actionsUsed = 0;
        turnCount = 0;
        started = true;
        gameOver = false;

        for (int i = 0; i < names.size(); i++) {
            Player player = new Player(names.get(i), i + 1);
            drawCards(player, 5);
            players.add(player);
        }

        startTurn();
    }

    public List<UsedCard> getUsedCards() {
        return new ArrayList<>(usedCards);
    }

    public boolean isOver(){
        return gameOver;
    }

    public Player getWinner() {
        for (Player player : players) {
            if (player.hasWon()) {
                return player;
            }
        }
        return null;
    }

    public boolean isStarted() {
        return started;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrPlayer() {
        return players.get(currentPlayerIndex);
    }

    public int getActionsUsed() {
        return actionsUsed;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public int getDrawPileNumber() {
        return deck.getDrawPileNumber();
    }

    public int getTotalCardNumber() {
        return deck.getTotalCardNumber();
    }

    public List<Player> getOtherPlayers(){
        List<Player> otherPlayers = new ArrayList<>();
        otherPlayers.addAll(getPlayers());
        otherPlayers.remove(getCurrPlayer());
        return otherPlayers;
    }

    public boolean playCard(Card card, DecisionMaker dm) {
        if (!started || gameOver || card == null) {
            return false;
        }

        Player current = getCurrPlayer();
        if (!current.getCardsAtHand().contains(card)) {
            return false;
        }

        if (actionsUsed >= Player.MAX_ACTIONS_PER_TURN) {
            return false;
        }

        if (!playSpecificCard(current, card, dm)) {
            return false;
        }

        actionsUsed++;
        current.removeCardFromHand(card);
        addUsedCard(current, card, CardAction.PLAYED);
        if (current.hasWon()) {
            gameOver = true;
        }
        return true;
    }

    public boolean playSpecificCard(Player player, Card card, DecisionMaker dm) {
        if (card instanceof MoneyCard) {
            player.addCardToBank(card);
            return true;
        }

        if (card instanceof PropertyCard propertyCard) {
            return player.addProperty(propertyCard);
        }

        if (card instanceof WildPropertyCard wildCard) {
            PropertyColor color = dm.selectColor("Choose a color for " + wildCard.getName(), wildCard.getPossibleColors());
            if (color == null) {
                return false;
            }
            return player.addWildProperty(wildCard, color);
        }

        if (card instanceof ActionCard actionCard) {
            return playActionCard(player, actionCard, dm);
        }

        return false;
    }

    private boolean playActionCard(Player player, ActionCard action, DecisionMaker dm) {
        UseMode mode = dm.useCard(action);
        if (mode == null) {
            return false;
        }

        if (mode == UseMode.BANK) {
            player.addCardToBank(action);
            return true;
        }

        return switch (action.getActionType()) {
            case PASS_GO -> playPassGo(player);
            case DEBT_COLLECTOR -> playDebtCollector(player, dm);
            case TODAY_IS_MY_BIRTHDAY -> playBirthday(player, dm);
            case RENT, MULTI_RENT -> playRentCard(player, action, dm, 1);
            case DOUBLE_RENT -> playDoubleRent(player, action, dm);
            case SLY_DEAL -> playSlyDeal(player, dm);
            case FORCED_DEAL -> playForcedDeal(player, dm);
            case DEAL_BREAKER -> playDealBreaker(player, dm);
            case HOUSE -> playHouse(player, dm);
            case HOTEL -> playHotel(player, dm);
            case JUST_SAY_NO -> false;
        };
    }

    private boolean playPassGo(Player player) {
        drawCards(player, 2);
        return true;
    }

    private boolean playDebtCollector(Player player, DecisionMaker dm) {
        List<Player> targets = playersWithPaymentOptions();
        Player target = dm.selectNextPlayer(player, targets, "Choose a player to pay you 5M.");
        if (target == null) {
            return false;
        }

        return collectPayment(target, player, 5, dm);
    }

    private boolean playBirthday(Player player, DecisionMaker dm) {
        List<Player> targets = playersWithPaymentOptions();
        if (targets.isEmpty()) {
            return false;
        }

        for (Player target : targets) {
            if (!collectPayment(target, player, 2, dm)) {
                return false;
            }
        }
        return true;
    }

    private boolean playRentCard(Player player, ActionCard action, DecisionMaker dm, int multiplier) {
        List<PropertyColor> colors = getRentColors(player, action);
        if (colors.isEmpty()) {
            return false;
        }

        PropertyColor color = chooseRentColor(dm, colors);
        if (color == null) {
            return false;
        }

        int amount = player.getPropertySets().get(color).calculateRent() * multiplier;
        if (amount <= 0) {
            return false;
        }

        if (action.getActionType() == ActionType.MULTI_RENT) {
            List<Player> targets = playersWithPaymentOptions();
            Player target = dm.selectNextPlayer(player, targets, "Choose a player to pay " + amount + "M rent.");
            if (target == null) {
                return false;
            }
            return collectPayment(target, player, amount, dm);
        }

        boolean hasPayer = false;
        for (Player target : getOtherPlayers()) {
            if (!hasPaymentOptions(target)) {
                continue;
            }

            hasPayer = true;
            if (!collectPayment(target, player, amount, dm)) {
                return false;
            }
        }
        return hasPayer;
    }

    private PropertyColor chooseRentColor(DecisionMaker dm, List<PropertyColor> colors) {
        if (colors.size() == 1) {
            return colors.getFirst();
        }

        return dm.selectColor("Choose rent color.", colors);
    }

    private List<PropertyColor> getRentColors(Player player, ActionCard action) {
        List<PropertyColor> sourceColors = action.getColors();
        if (action.getActionType() == ActionType.MULTI_RENT) {
            sourceColors = PropertyColor.getColors();
        }

        List<PropertyColor> colors = new ArrayList<>();
        for (PropertyColor color : sourceColors) {
            PropertySet set = player.getPropertySets().get(color);
            if (set != null && set.calculateRent() > 0) {
                colors.add(color);
            }
        }
        return colors;
    }

    private boolean playDoubleRent(Player player, ActionCard doubleRent, DecisionMaker dm) {
        List<Card> rentCards = new ArrayList<>();
        for (Card card : player.getCardsAtHand()) {
            if (card instanceof ActionCard actionCard && card != doubleRent && isRentCard(actionCard)) {
                rentCards.add(card);
            }
        }

        Card selected = dm.selectPropertyCard(player, rentCards, "Choose a rent card to double.");
        if (!(selected instanceof ActionCard rentCard)) {
            return false;
        }

        if (!playRentCard(player, rentCard, dm, 2)) {
            return false;
        }

        player.removeCardFromHand(rentCard);
        addUsedCard(player, rentCard, CardAction.PLAYED);
        return true;
    }

    private boolean isRentCard(ActionCard actionCard) {
        return actionCard.getActionType() == ActionType.RENT || actionCard.getActionType() == ActionType.MULTI_RENT;
    }

    private boolean playSlyDeal(Player player, DecisionMaker dm) {
        List<Player> targets = playersWithStealableCards(player);
        Player target = dm.selectNextPlayer(player, targets, "Choose a player to steal from.");
        if (target == null) {
            return false;
        }

        if (isBlockedByJustSayNo(target, player, dm)) {
            return true;
        }

        List<Card> cards = stealableCards(target, player);
        Card card = dm.selectPropertyCard(target, cards, "Choose a property to steal.");
        if (card == null) {
            return false;
        }

        return transferPropertyCard(target, player, card);
    }

    private boolean playForcedDeal(Player player, DecisionMaker dm) {
        List<Player> targets = playersForForcedDeal(player);
        Player target = dm.selectNextPlayer(player, targets, "Choose a player to trade with.");
        if (target == null) {
            return false;
        }

        if (isBlockedByJustSayNo(target, player, dm)) {
            return true;
        }

        List<Card> ownCards = stealableCards(player, target);
        Card ownCard = dm.selectPropertyCard(player, ownCards, "Choose one of your properties to give.");
        if (ownCard == null) {
            return false;
        }

        List<Card> targetCards = stealableCards(target, player);
        Card targetCard = dm.selectPropertyCard(target, targetCards, "Choose one property to receive.");
        if (targetCard == null) {
            return false;
        }

        return swapProperties(player, ownCard, target, targetCard);
    }

    private boolean playDealBreaker(Player player, DecisionMaker dm) {
        List<Player> targets = playersWithTransferableFullSets(player);
        Player target = dm.selectNextPlayer(player, targets, "Choose a player with a full set.");
        if (target == null) {
            return false;
        }

        if (isBlockedByJustSayNo(target, player, dm)) {
            return true;
        }

        List<PropertyColor> colors = transferableFullSetColors(target, player);
        PropertyColor color = dm.selectColor("Choose a full set to steal.", colors);
        if (color == null) {
            return false;
        }

        return target.transferFullSetTo(player, color);
    }

    private boolean playHouse(Player player, DecisionMaker dm) {
        List<PropertyColor> colors = buildableColors(player, true);
        PropertyColor color = dm.selectColor("Choose a full set for House.", colors);
        if (color == null) {
            return false;
        }
        return player.addHouse(color);
    }

    private boolean playHotel(Player player, DecisionMaker dm) {
        List<PropertyColor> colors = buildableColors(player, false);
        PropertyColor color = dm.selectColor("Choose a full set for Hotel.", colors);
        if (color == null) {
            return false;
        }
        return player.addHotel(color);
    }

    private List<PropertyColor> buildableColors(Player player, boolean house) {
        List<PropertyColor> colors = new ArrayList<>();
        for (PropertySet set : player.getPropertySets().values()) {
            if (!canBuildOn(set.getColor())) {
                continue;
            }

            if (house && set.canAddHouse()) {
                colors.add(set.getColor());
            }

            if (!house && set.canAddHotel()) {
                colors.add(set.getColor());
            }
        }
        return colors;
    }

    private boolean canBuildOn(PropertyColor color) {
        return color != PropertyColor.RAILROAD && color != PropertyColor.UTILITY;
    }

    private boolean collectPayment(Player payer, Player receiver, int amount, DecisionMaker dm) {
        if (amount <= 0) {
            return true;
        }

        if (isBlockedByJustSayNo(payer, receiver, dm)) {
            return true;
        }

        List<Card> options = paymentOptions(payer);
        if (options.isEmpty()) {
            return false;
        }

        int requiredAmount = Math.min(amount, totalValue(options));
        List<Card> selectedCards = dm.selectPaymentCards(payer, options, requiredAmount);
        if (selectedCards == null || selectedCards.isEmpty()) {
            return false;
        }

        List<Card> uniqueCards = new ArrayList<>();
        int paid = 0;
        for (Card selected : selectedCards) {
            if (!options.contains(selected) || uniqueCards.contains(selected)) {
                return false;
            }

            uniqueCards.add(selected);
            paid += selected.getBankValue();
        }

        if (paid < requiredAmount) {
            return false;
        }

        for (Card selected : uniqueCards) {
            if (!transferPaymentCard(payer, receiver, selected)) {
                return false;
            }
        }
        return true;
    }

    private List<Player> playersWithPaymentOptions() {
        List<Player> targets = new ArrayList<>();
        for (Player player : getOtherPlayers()) {
            if (hasPaymentOptions(player)) {
                targets.add(player);
            }
        }
        return targets;
    }

    private boolean hasPaymentOptions(Player player) {
        return !paymentOptions(player).isEmpty();
    }

    private int totalValue(List<Card> cards) {
        int total = 0;
        for (Card card : cards) {
            total += card.getBankValue();
        }
        return total;
    }

    private List<Card> paymentOptions(Player player) {
        List<Card> options = new ArrayList<>();
        options.addAll(player.getCardsAtBank());
        for (PropertySet set : player.getPropertySets().values()) {
            options.addAll(set.getCards());
        }
        return options;
    }

    private boolean transferPaymentCard(Player payer, Player receiver, Card card) {
        if (payer.getCardsAtBank().contains(card)) {
            if (!payer.removeCardFromBank(card)) {
                return false;
            }
            receiver.addCardToBank(card);
            return true;
        }

        return transferPropertyCard(payer, receiver, card);
    }

    private boolean transferPropertyCard(Player source, Player receiver, Card card) {
        PropertyColor color = source.getPropertyColor(card);
        if (!canReceiveProperty(receiver, card, color)) {
            return false;
        }

        if (!source.removePropertyCard(card)) {
            return false;
        }

        if (!receiver.receivePropertyCard(card, color)) {
            source.receivePropertyCard(card, color);
            return false;
        }

        return true;
    }

    private boolean swapProperties(Player firstPlayer, Card firstCard, Player secondPlayer, Card secondCard) {
        PropertyColor firstColor = firstPlayer.getPropertyColor(firstCard);
        PropertyColor secondColor = secondPlayer.getPropertyColor(secondCard);

        if (!canReceiveAfterRemoving(firstPlayer, secondCard, secondColor, firstCard)) {
            return false;
        }

        if (!canReceiveAfterRemoving(secondPlayer, firstCard, firstColor, secondCard)) {
            return false;
        }

        firstPlayer.removePropertyCard(firstCard);
        secondPlayer.removePropertyCard(secondCard);
        firstPlayer.receivePropertyCard(secondCard, secondColor);
        secondPlayer.receivePropertyCard(firstCard, firstColor);
        return true;
    }

    private boolean canReceiveAfterRemoving(Player receiver, Card incoming, PropertyColor incomingColor, Card outgoing) {
        if (!isValidPropertyColor(incoming, incomingColor)) {
            return false;
        }

        PropertySet set = receiver.getPropertySets().get(incomingColor);
        if (set == null) {
            return false;
        }

        int count = set.getCards().size();
        if (receiver.getPropertyColor(outgoing) == incomingColor) {
            count--;
        }

        return count < incomingColor.getSize();
    }

    private boolean canReceiveProperty(Player receiver, Card card, PropertyColor color) {
        if (!isValidPropertyColor(card, color)) {
            return false;
        }

        PropertySet set = receiver.getPropertySets().get(color);
        return set != null && set.canAddProperty();
    }

    private boolean isValidPropertyColor(Card card, PropertyColor color) {
        if (color == null) {
            return false;
        }

        if (card instanceof WildPropertyCard wildCard) {
            return wildCard.getPossibleColors().contains(color);
        }

        if (card instanceof PropertyCard propertyCard) {
            return propertyCard.getColor() == color;
        }

        return false;
    }

    private List<Player> playersForForcedDeal(Player player) {
        List<Player> targets = new ArrayList<>();
        for (Player target : getOtherPlayers()) {
            if (!stealableCards(player, target).isEmpty() && !stealableCards(target, player).isEmpty()) {
                targets.add(target);
            }
        }
        return targets;
    }

    private List<Player> playersWithStealableCards(Player receiver) {
        List<Player> targets = new ArrayList<>();
        for (Player player : getOtherPlayers()) {
            if (!stealableCards(player, receiver).isEmpty()) {
                targets.add(player);
            }
        }
        return targets;
    }

    private List<Card> stealableCards(Player source, Player receiver) {
        List<Card> cards = new ArrayList<>();
        for (Card card : source.getStealableCards()) {
            PropertyColor color = source.getPropertyColor(card);
            if (canReceiveProperty(receiver, card, color)) {
                cards.add(card);
            }
        }
        return cards;
    }

    private List<Player> playersWithTransferableFullSets(Player receiver) {
        List<Player> targets = new ArrayList<>();
        for (Player player : getOtherPlayers()) {
            if (!transferableFullSetColors(player, receiver).isEmpty()) {
                targets.add(player);
            }
        }
        return targets;
    }

    private List<PropertyColor> transferableFullSetColors(Player source, Player receiver) {
        List<PropertyColor> colors = new ArrayList<>();
        for (PropertyColor color : source.getFullSetColors()) {
            PropertySet sourceSet = source.getPropertySets().get(color);
            PropertySet receiverSet = receiver.getPropertySets().get(color);
            if (sourceSet != null && receiverSet != null
                    && receiverSet.getCards().size() + sourceSet.getCards().size() <= color.getSize()) {
                colors.add(color);
            }
        }
        return colors;
    }

    private boolean isBlockedByJustSayNo(Player target, Player actor, DecisionMaker dm) {
        ActionCard justSayNo = findJustSayNo(target);
        if (justSayNo == null) {
            return false;
        }

        if (!dm.reconfirm(target.getName() + ": use Just Say No against " + actor.getName() + "?")) {
            return false;
        }

        target.removeCardFromHand(justSayNo);
        addUsedCard(target, justSayNo, CardAction.PLAYED);
        return true;
    }

    private ActionCard findJustSayNo(Player player) {
        for (Card card : player.getCardsAtHand()) {
            if (card instanceof ActionCard actionCard && actionCard.getActionType() == ActionType.JUST_SAY_NO) {
                return actionCard;
            }
        }

        return null;
    }

    public int getCurrBankTotal() {
        int total = 0;
        for (Card card : getCurrPlayer().getCardsAtBank()) {
            total += card.getBankValue();
        }
        return total;
    }

    public boolean endTurn(DecisionMaker dm) {
        if (!started || players.isEmpty() || gameOver) {
            return false;
        }
        Player currPlayer = getCurrPlayer();
        int discardCount = currPlayer.getCardsAtHand().size() - Player.MAX_CARDS_AT_HAND;

        if (discardCount > 0) {
            List<Card> discards = dm.selectDiscards(currPlayer, currPlayer.getCardsAtHand(), discardCount);
            if (discards == null || discards.size() != discardCount) {
                return false;
            }

            for (Card discard : discards) {
                if (discards.indexOf(discard) != discards.lastIndexOf(discard)) {
                    return false;
                }

                if (!currPlayer.getCardsAtHand().contains(discard)) {
                    return false;
                }
            }

            for (Card discard : discards) {
                currPlayer.removeCardFromHand(discard);
                deck.discard(discard);
                addUsedCard(currPlayer, discard, CardAction.DISCARDED);
            }
        }

        currentPlayerIndex++;
        if (currentPlayerIndex >= players.size()) {
            currentPlayerIndex = 0;
        }

        actionsUsed = 0;
        startTurn();
        return true;
    }

    private void startTurn() {
        Player player = getCurrPlayer();
        int drawCardsNumber = 2;

        if (player.getCardsAtHand().isEmpty()) {
            drawCardsNumber = 5;
        }

        drawCards(player, drawCardsNumber);
        if (currentPlayerIndex == 0) {
            turnCount++;
        }
    }

    private void drawCards(Player player, int number){
        for(int i = 0; i < number; i++){
            Card card = deck.draw();
            if(card!=null){
                player.addCardToHand(card);
            }
        }
    }

    private void addUsedCard(Player player, Card card, CardAction action) {
        usedCards.addFirst(new UsedCard(action, player.getName(), card));
    }

    public enum CardAction {
        PLAYED("Played"),
        DISCARDED("Discarded");

        private final String label;

        CardAction(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public record UsedCard(CardAction action, String player, Card card) {}
}
