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
            for (int i = 1; i < 10; i++) g2d.drawLine(302 + i * 30, 50, 302 + i * 30, 650);
            for (int j = 1; j < 20; j++) g2d.drawLine(302, 50 + j * 30, 602, 50 + j * 30);

            drawCard(g2d, 180, 100, 100, 120, "Hold");
            drawPieceInCard(g2d, 195, 155, Color.CYAN, "I"); 

            drawCard(g2d, 180, 300, 100, 60, "Highscore");
            drawVal(g2d, 183, 327, 94, 30, "3368");

            int sY = 400;
            drawValLbl(g2d, 180, sY, 100, 50, "Level", "1");
            drawValLbl(g2d, 180, sY + 50, 100, 50, "Score", "390");
            drawValLbl(g2d, 180, sY + 100, 100, 50, "Lines", "0");

            drawCard(g2d, 620, 100, 100, 300, "Next");
            drawPieceInCard(g2d, 645, 150, Color.GREEN, "S");
            drawPieceInCard(g2d, 645, 230, Color.ORANGE, "L");
            drawPieceInCard(g2d, 645, 310, new Color(160, 80, 255), "T");

            drawPieceInGame(g2d, 4, 10, new Color(160, 80, 255), "T"); 
        }

        private void drawCard(Graphics2D g, int x, int y, int w, int h, String t) {
            g.setColor(Color.WHITE);
            g.fillRoundRect(x, y, w, h, 15, 15);
            g.setColor(new Color(10, 15, 30));
            g.fillRect(x + 3, y + 25, w - 6, h - 28);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Tahoma", Font.BOLD, 13));
            FontMetrics fm = g.getFontMetrics();
            int tw = fm.stringWidth(t);
            g.drawString(t, x + (w - tw) / 2, y + 18);
        }

        private void drawVal(Graphics2D g, int x, int y, int w, int h, String v) {
            g.setColor(new Color(30, 40, 70));
            g.fillRect(x, y, w, h);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Verdana", Font.BOLD, 15));
            FontMetrics fm = g.getFontMetrics();
            int tw = fm.stringWidth(v);
            g.drawString(v, x + (w - tw) / 2, y + h / 2 + fm.getAscent() / 2 - 2);
        }

        private void drawValLbl(Graphics2D g, int x, int y, int w, int h, String l, String v) {
            g.setColor(Color.WHITE);
            g.drawRect(x, y, w, h);
            g.fillRect(x, y, w, 20);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Tahoma", Font.BOLD, 11));
            FontMetrics fm = g.getFontMetrics();
            int lw = fm.stringWidth(l);
            g.drawString(l, x + (w - lw) / 2, y + 15);
            g.setColor(new Color(10, 15, 30));
            g.fillRect(x + 2, y + 22, w - 4, h - 24);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Verdana", Font.BOLD, 15));
            fm = g.getFontMetrics();
            int vw = fm.stringWidth(v);
            g.drawString(v, x + (w - vw) / 2, y + 43);
        }

        private void drawPieceInCard(Graphics2D g, int x, int y, Color c, String type) {
            int s = 18; 
            g.setColor(c);
            if (type.equals("I")) {
                for (int i = 0; i < 4; i++) g.fillRect(x + i*s, y, s-2, s-2);
            } else if (type.equals("L")) {
                g.fillRect(x, y, s-2, s-2); g.fillRect(x, y+s, s-2, s-2);
                g.fillRect(x+s, y+s, s-2, s-2); g.fillRect(x+2*s, y+s, s-2, s-2);
            } else if (type.equals("S")) {
                g.fillRect(x+s, y, s-2, s-2); g.fillRect(x+2*s, y, s-2, s-2);
                g.fillRect(x, y+s, s-2, s-2); g.fillRect(x+s, y+s, s-2, s-2);
            } else if (type.equals("T")) {
                g.fillRect(x+s, y, s-2, s-2); g.fillRect(x, y+s, s-2, s-2);
                g.fillRect(x+s, y+s, s-2, s-2); g.fillRect(x+2*s, y+s, s-2, s-2);
            }
        }

        private void drawPieceInGame(Graphics2D g, int col, int row, Color c, String type) {
            int s = 30; 
            int ox = 302;
            int oy = 50;
            g.setColor(c);
            int px = ox + col * s;
            int py = oy + row * s;
            if (type.equals("T")) {
                g.fillRect(px, py, s-2, s-2);
                g.fillRect(px-s, py+s, s-2, s-2);
                g.fillRect(px, py+s, s-2, s-2);
                g.fillRect(px+s, py+s, s-2, s-2);
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