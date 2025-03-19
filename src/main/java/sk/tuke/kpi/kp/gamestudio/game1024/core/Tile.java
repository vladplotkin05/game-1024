package sk.tuke.kpi.kp.gamestudio.game1024.core;

public class Tile {
    private int value;

    public Tile(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void doubleValue() {
        this.value *= 2;
    }
}