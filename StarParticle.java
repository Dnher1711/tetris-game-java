import java.awt.*;
import java.util.Random;

public class StarParticle {
    public double x, y;
    public double velX, velY;
    public int size;
    public Color color;
    public int life = 40; // Thời gian tồn tại (opacity giảm dần)
    private static final Random rand = new Random();

    public StarParticle(int startX, int startY, Color color) {
        this.x = startX;
        this.y = startY;
        this.color = color;
        this.size = rand.nextInt(10) + 11;
        // Tạo tốc độ bay ngẫu nhiên
        this.velX = (rand.nextDouble() - 0.5) * 15;
        this.velY = -(rand.nextDouble()  * 15 +10);
    }