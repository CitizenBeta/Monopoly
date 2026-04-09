package ie.ucd.monopolydeal.model;

import java.util.List;

public class WildPropertyCard extends Card{
    private final Color currentColor;
    private final List<Color> possibleColors;

    public WildPropertyCard(String name, int value, Color currentColor, List<Color> possibleColors){
        super(name,value);
        this.currentColor = currentColor;
        this.possibleColors = possibleColors;
    }

    public Color getCurrentColor(){
        return currentColor;
    }

    public List<Color> getPossibleColors(){
        return possibleColors;
    }
}
