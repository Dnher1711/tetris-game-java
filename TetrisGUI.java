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
            for (int i = 0; i < 50; i++) {
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

            g2d.setColor(new Color(255, 255, 255, 15));
            g2d.fillRect(50, 100, 150, 150);
            g2d.fillRect(700, 450, 180, 180);

            for (Star star : stars) {
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.fillOval(star.x, star.y, star.size, star.size);
            }

            g2d.setColor(new Color(255, 255, 255, 30));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawLine(0, 0, getWidth(), getHeight());
            g2d.drawLine(0, getHeight() / 4, getWidth(), getHeight() + getHeight() / 4);
            g2d.drawLine(getWidth() / 4, 0, getWidth() + getWidth() / 4, getHeight());
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