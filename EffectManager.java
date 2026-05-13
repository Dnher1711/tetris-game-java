import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EffectManager {
    private List<StarParticle> particles = new ArrayList<>();

    public void addExplosion(int row, int width) {
        Color[] colors = {Color.YELLOW, Color.WHITE, Color.ORANGE, Color.CYAN};
        int cellSize = 30; // Đảm bảo biến này khớp với kích thước ô gạch của bạn
        java.util.Random rand = new java.util.Random();

        // CHỈ DÙNG 1 VÒNG LẶP DUY NHẤT
        // Tạo khoảng 20-25 hạt ngẫu nhiên cho cả một hàng là đủ đẹp và cực mượt
        for (int i = 0; i < 50; i++) {
            // Vị trí X ngẫu nhiên chạy từ đầu đến cuối chiều rộng bảng
            int startX = rand.nextInt(width * cellSize);

            // Vị trí Y nằm ở giữa hàng bị xóa
            int startY = row * cellSize + (cellSize / 2);

            // Lấy màu ngẫu nhiên trong danh sách
            Color pColor = colors[rand.nextInt(colors.length)];

            particles.add(new StarParticle(startX, startY, pColor));
        }
    }
    public void update() {
        Iterator<StarParticle> it = particles.iterator();
        while (it.hasNext()) {
            StarParticle p = it.next();
            p.update();
            if (p.life <= 0) it.remove();
        }
    }

    public void draw(Graphics2D g2d) {
        for (StarParticle p : particles) {
            p.draw(g2d);
        }
    }
}
