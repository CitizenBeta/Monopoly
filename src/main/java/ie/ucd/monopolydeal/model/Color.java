package ie.ucd.monopolydeal.model;

public enum Color {
    BROWN("Brown", 2, new int[]{1, 2}),
    LIGHT_BLUE("Light Blue", 3, new int[]{1, 2, 3}),
    PINK("Pink", 3, new int[]{1, 2, 4}),
    ORANGE("Orange", 3, new int[]{1, 3, 5}),
    RED("Red", 3, new int[]{2, 3, 6}),
    YELLOW("Yellow", 3, new int[]{2, 4, 6}),
    GREEN("Green", 3, new int[]{2, 4, 7}),
    DARK_BLUE("Dark Blue", 2, new int[]{3, 8}),
    RAILROAD("Railroad", 4, new int[]{1, 2, 3, 4}),
    UTILITY("Utility", 2, new int[]{1, 2});

    private final String name;
    private final int setSize;
    private final int[] rent;

    Color(String name, int setSize, int[] rent) {
        this.name = name;
        this.setSize = setSize;
        this.rent = rent;
    }

    public String getName() {
        return name;
    }

    public int getSetSize() {
        return setSize;
    }

    public int getRent(int ownedPropertyCount) {
        if (ownedPropertyCount < 1) {
            ownedPropertyCount = 1;
        } else if (ownedPropertyCount > rent.length) {
            ownedPropertyCount = rent.length;
        }

        return rent[ownedPropertyCount - 1];
    }
}
