import java.awt.*;
import java.util.Random;
public class StarParticle {
    public double x, y;
    public double velX, velY;
    public int size;
    public Color color;
    public int life = 40;
    private static final Random rand = new Random();
    public StarParticle(int startX, int startY, Color color) {
        this.x = startX;
        this.y = startY;
        this.color = color;
        this.size = rand.nextInt(10) + 11;
        this.velX = (rand.nextDouble() - 0.5) * 15;
        this.velY = -(rand.nextDouble()  * 15 +10);
    }
    public void update() {
        x += velX;
        y += velY;
        velY += -1; // Trọng lực nhẹ làm hạt rơi xuống
        life -= 6;   // Biến mất dần
    }
    public void draw(Graphics2D g2d) {
        if (life <= 0) return;
        int alpha = Math.max(0, Math.min(255, (life * 255) / 60));
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (life * 255) / 100));
        int[] xPoints = {(int)x, (int)x+size/2, (int)x+size, (int)x+size/2};
        int[] yPoints = {(int)y+size/2, (int)y, (int)y+size/2, (int)y+size};
        g2d.fillPolygon(xPoints, yPoints, 4);
    }
}
