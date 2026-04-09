package ie.ucd.monopolydeal.model;

public class ActionCard extends Card {
    public ActionType actionType;

    public ActionCard(String name, int value, ActionType actionType) {
        super(name, value);
        this.actionType = actionType;
    }


}
