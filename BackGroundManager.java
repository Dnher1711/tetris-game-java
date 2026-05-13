import java.awt.*;
public class BackgroundManager {
    private Color currentColor;
    private Color targetColor;
    private final Color[] levelColors = {
            new Color(20, 20, 40),   // Level 1: Xanh tối
            new Color(40, 20, 20),   // Level 2: Đỏ tối
            new Color(20, 40, 20),   // Level 3: Xanh lá tối
            new Color(40, 40, 20),   // Level 4: Vàng tối
            new Color(40, 20, 40),   // Level 5: Tím tối
            new Color(20, 40, 40)    // Level 6+
    };
    public BackgroundManager() {
        currentColor = levelColors[0];
        targetColor = levelColors[0];
    }