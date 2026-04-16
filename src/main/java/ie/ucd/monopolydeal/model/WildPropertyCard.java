package ie.ucd.monopolydeal.model;

import java.util.List;

public class WildPropertyCard extends Card{
    private  Color currentColor;
    private final List<Color> possibleColors;

    public WildPropertyCard(String name, int value, List<Color> possibleColors){
        super(name,value);
        this.possibleColors = possibleColors;
    }

    public Color getCurrentColor(){
        return currentColor;
    }

    public void setColor(Color color){
        if(possibleColors.contains(color)){
            currentColor = color;
        }else{
            throw new RuntimeException("The color can not be used!");
        }
    }

    public List<Color> getPossibleColors(){
        return possibleColors;
    }
}
