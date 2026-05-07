import java.awt.Color;

public class Cell {

    private boolean isOccupied;
    private Color color;

    public Cell() { this.isOccupied = false; this.color = null; }

    public Cell(boolean isOccupied, Color color) {
        this.isOccupied = isOccupied;
        this.color = color;
    }

    public void occupy(Color color) {
        this.isOccupied = true;
        this.color = color;
    }

    public void clear() {
        this.isOccupied = false;
        this.color = null;
    }

    public Cell copy() { return new Cell(isOccupied, color); }

    public boolean isOccupied() { return isOccupied; }
    public Color   getColor()   { return color; }

    @Override
    public String toString() { return isOccupied ? "█" : "░"; }
}

