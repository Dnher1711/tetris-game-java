import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TetrisGUI extends JFrame {

    public TetrisGUI() {
        setTitle("Tetris Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(new GamePanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    class GamePanel extends JPanel {
        private List<Star> stars;
        private Random random = new Random();

        public GamePanel() {
            setPreferredSize(new Dimension(900, 750));
            generateStars();
        }

        private void generateStars() {
            stars = new ArrayList<>();
            for (int i = 0; i < 60; i++) {
                stars.add(new Star(random.nextInt(900), random.nextInt(750), random.nextInt(3) + 1));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(0, 0, new Color(45, 55, 100), 0, getHeight(), new Color(25, 30, 60));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setColor(new Color(255, 255, 255, 20));
            g2d.setStroke(new BasicStroke(1));
            for (int i = -500; i < 900; i += 40) {
                g2d.drawLine(i, 0, i + 750, 750);
            }

            for (Star star : stars) {
                g2d.setColor(new Color(255, 255, 255, random.nextInt(100) + 100));
                g2d.fillOval(star.x, star.y, star.size, star.size);
            }

            g2d.setColor(new Color(255, 255, 255, 15));
            g2d.fillRect(50, 100, 150, 150);
            g2d.fillRect(700, 450, 180, 180);

            g2d.setColor(new Color(100, 150, 255));
            g2d.setStroke(new BasicStroke(4));
            g2d.drawRect(300, 48, 304, 604);
            g2d.setColor(new Color(10, 15, 30));
            g2d.fillRect(302, 50, 300, 600);

            g2d.setColor(new Color(40, 45, 70));
            g2d.setStroke(new BasicStroke(1));
            for (int i = 1; i < 10; i++) {
                g2d.drawLine(302 + i * 30, 50, 302 + i * 30, 650);
            }
            for (int j = 1; j < 20; j++) {
                g2d.drawLine(302, 50 + j * 30, 602, 50 + j * 30);
            }
        }

        private class Star {
            int x, y, size;
            Star(int x, int y, int size) {
                this.x = x;
                this.y = y;
                this.size = size;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TetrisGUI());
    }
}