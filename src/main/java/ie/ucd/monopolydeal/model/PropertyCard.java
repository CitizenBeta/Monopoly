package ie.ucd.monopolydeal.model;

public class PropertyCard extends Card {
    public Color color;

    public PropertyCard(String name, int value, Color color) {
        this.color = color;
        super(name, value);
    }

    public Color getColor() {
        return color;
    }
}
