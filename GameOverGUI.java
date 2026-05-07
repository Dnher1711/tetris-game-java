import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameOverGUI extends JFrame {

    public GameOverGUI() {
        setTitle("Tetris Pro - Game Over");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(new GameOverPanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameOverGUI());
    }

    class GameOverPanel extends JPanel {
        private List<Star> stars;
        private List<DecorationPiece> decoPieces;
        private Random random = new Random();
        private JButton playAgainButton;

        public GameOverPanel() {
            setPreferredSize(new Dimension(900, 750));
            setLayout(null);
            generateStars();
            generateDecoPieces();

            playAgainButton = new JButton("PLAY AGAIN");
            playAgainButton.setFont(new Font("Tahoma", Font.BOLD, 45));
            playAgainButton.setForeground(Color.BLACK);
            playAgainButton.setBackground(Color.WHITE);
            playAgainButton.setFocusPainted(false);
            playAgainButton.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 4));
            playAgainButton.setBounds(200, 520, 500, 100);
            add(playAgainButton);
        }

        private void generateStars() {
            stars = new ArrayList<>();
            for (int i = 0; i < 60; i++) {
                stars.add(new Star(random.nextInt(900), random.nextInt(750), random.nextInt(3) + 1));
            }
        }

        private void generateDecoPieces() {
            decoPieces = new ArrayList<>();
            String[] types = {"I", "L", "S", "T", "O", "Z"};
            Color[] colors = {Color.CYAN, Color.ORANGE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.RED};
            for (int i = 0; i < 25; i++) {
                int idx = random.nextInt(types.length);
                decoPieces.add(new DecorationPiece(random.nextInt(850), random.nextInt(700), colors[idx], types[idx]));
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
            for (int i = -500; i < 900; i += 40) g2d.drawLine(i, 0, i + 750, 750);

            for (Star star : stars) {
                g2d.setColor(new Color(255, 255, 255, 120));
                g2d.fillOval(star.x, star.y, star.size, star.size);
            }

            for (DecorationPiece p : decoPieces) drawDeco(g2d, p.x, p.y, p.color, p.type);

            g2d.setFont(new Font("Verdana", Font.BOLD, 100));
            String gO = "GAME OVER";
            g2d.setColor(Color.RED.darker());
            g2d.drawString(gO, 105, 305);
            g2d.setColor(Color.RED);
            g2d.drawString(gO, 100, 300);

            drawScoreBoard(g2d, 350, 340, 200, 150);
        }

        private void drawDeco(Graphics2D g, int x, int y, Color c, String type) {
            int s = 20;
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 60));
            if (type.equals("I")) { for (int i = 0; i < 4; i++) g.fillRect(x, y + i * s, s - 2, s - 2); }
            else if (type.equals("O")) {
                g.fillRect(x, y, s-2, s-2); g.fillRect(x+s, y, s-2, s-2);
                g.fillRect(x, y+s, s-2, s-2); g.fillRect(x+s, y+s, s-2, s-2);
            } else {
                g.fillRect(x, y, s - 2, s - 2); g.fillRect(x + s, y, s - 2, s - 2);
                g.fillRect(x, y + s, s - 2, s - 2);
            }
        }

        private void drawScoreBoard(Graphics2D g, int x, int y, int w, int h) {
            g.setColor(Color.WHITE);
            g.fillRoundRect(x, y, w, h, 15, 15);
            g.setColor(new Color(10, 15, 30));
            g.fillRect(x + 5, y + 35, w - 10, 35);
            g.fillRect(x + 5, y + 105, w - 10, 35);

            g.setColor(Color.BLACK);
            g.setFont(new Font("Tahoma", Font.BOLD, 14));
            FontMetrics fm = g.getFontMetrics();
            g.drawString("SCORE", x + (w - fm.stringWidth("SCORE")) / 2, y + 25);
            g.drawString("HIGHEST SCORE", x + (w - fm.stringWidth("HIGHEST SCORE")) / 2, y + 95);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Verdana", Font.BOLD, 18));
            fm = g.getFontMetrics();
            g.drawString("1250", x + (w - fm.stringWidth("1250")) / 2, y + 60);
            g.drawString("3368", x + (w - fm.stringWidth("3368")) / 2, y + 130);
        }
    }

    class DecorationPiece {
        int x, y; Color color; String type;
        DecorationPiece(int x, int y, Color c, String t) { this.x = x; this.y = y; this.color = c; this.type = t; }
    }

    class Star {
        int x, y, size;
        Star(int x, int y, int size) { this.x = x; this.y = y; this.size = size; }
    }
}
