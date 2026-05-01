import java.awt.Color;

public enum TetrisColor {
    CYAN   (new Color(0,   240, 240)),  // I
    YELLOW (new Color(240, 240,   0)),  // O
    PURPLE (new Color(160,   0, 240)),  // T
    GREEN  (new Color(0,   240,   0)),  // S
    RED    (new Color(240,   0,   0)),  // Z
    BLUE   (new Color(0,     0, 240)),  // J
    ORANGE (new Color(240, 160,   0));  // L

    private final Color awtColor;

    TetrisColor(Color c) { this.awtColor = c; }

    public Color toAwtColor() { return awtColor; }

    public static TetrisColor fromType(TetrominoType type) {
        switch (type) {
            case I: return CYAN;   case O: return YELLOW;
            case T: return PURPLE; case S: return GREEN;
            case Z: return RED;    case J: return BLUE;
            case L: return ORANGE;
            default: throw new IllegalArgumentException("Unknown: " + type);
        }
    }
}