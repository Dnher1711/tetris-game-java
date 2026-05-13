import java.awt.*;

public class BackgroundManager {
    private Color currentColor;
    private Color targetColor;

    // Mảng màu tương ứng cho từng level
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

    public void update(int level) {
        int colorIndex = Math.min(level - 1, levelColors.length - 1);
        targetColor = levelColors[colorIndex];

        // Hiệu ứng chuyển màu mượt (Interpolation)
        int r = interpolate(currentColor.getRed(), targetColor.getRed());
        int g = interpolate(currentColor.getGreen(), targetColor.getGreen());
        int b = interpolate(currentColor.getBlue(), targetColor.getBlue());
        currentColor = new Color(r, g, b);
    }

    private int interpolate(int current, int target) {
        if (current < target) return Math.min(current + 1, target);
        if (current > target) return Math.max(current - 1, target);
        return target;
    }

    public void draw(Graphics2D g2d, int width, int height) {
        GradientPaint gp = new GradientPaint(0, 0, currentColor, 0, height, Color.BLACK);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);
    }
}