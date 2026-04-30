package ie.ucd.monopolydeal.model;

public class ActionCard implements Card {
    private final String name;
    private final int bankValue;
    private final ActionType actionType;

    public ActionCard(String name, int bankValue, ActionType actionType) {
        this.name = name;
        this.bankValue = bankValue;
        this.actionType = actionType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getBankValue() {
        return bankValue;
    }

    @Override
    public String getDetail() {
        return name + " [Action, " + actionType + ", bank " + bankValue + "M]";
    }

    public ActionType getActionType() {
        return actionType;
    }

    @Override
    public String toString() {
        return getDetail();
    }
}