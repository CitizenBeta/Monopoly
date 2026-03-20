package ie.ucd.monopoly;

import java.util.ArrayList;
import java.util.Scanner;
public class Player {
    private String name;

    public Player(String name){
        this.name = name;
    }

    public Scanner scanner = new Scanner(System.in);

    private int wholePropertyNumber = 0;

    private int usedCardNumberThisTurn = 0;

    public static int MAXIMAL_CARDS_AT_HAND = 7;

    public static int MAXIMAL_CARDS_USED_PER_TURN = 3;

    private ArrayList<Card> cardPacket = new ArrayList<>();

    private ArrayList<Card> propertyCards = new ArrayList<>();

    private ArrayList<Card> moneyCards = new ArrayList<>();

    public void play(){
        if(usedCardNumberThisTurn< MAXIMAL_CARDS_USED_PER_TURN){
            consumeCard(chooseCard());
            usedCardNumberThisTurn++;
        }else {
            System.out.println("You've already exerted 3 cards this turn!");
        }
    }

    public void resetTurn(){
        usedCardNumberThisTurn = 0;
    }

    public void addCard(Card card){
        cardPacket.add(card);
    }

    public void consumeCard(Card card){
        card.exert(this);
        if(card instanceof MoneyCard){
            moneyCards.add(card);
        }else if(card instanceof PropertyCard){
            propertyCards.add(card);
        }
        cardPacket.remove(card);
    }

    public Card chooseCard() {
        while (true) {
            System.out.println("Please choose one of your cards!");
            for (int i = 0; i < cardPacket.size(); i++) {
                System.out.print("Card" + (i + 1) + ": " + cardPacket.get(i).getName() + " ");
            }
            System.out.println();
            if (scanner.hasNextInt()) {
                int input = scanner.nextInt();
                if (input > 0 && input <= cardPacket.size()) {
                    return cardPacket.get(input - 1);
                }
            } else {
                scanner.next();
            }
            System.out.println("Invalid input! Please enter a number between 1 and " + cardPacket.size());
        }
    }
}
