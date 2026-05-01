import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tetris — Nhóm Java");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            GamePanel panel = new GamePanel();
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            panel.startGame();
        });
    }
}

class GamePanel extends JPanel implements GameLoopListener {

    static final int CELL_SIZE = 30;
    static final int BOARD_W = Board.WIDTH  * CELL_SIZE;   // 300px
    static final int BOARD_H = Board.HEIGHT * CELL_SIZE;   // 600px
    static final int SIDE_W = 160;
    static final int PANEL_W = BOARD_W + SIDE_W + 20;      // 480px
    static final int PANEL_H = BOARD_H + 20;               // 620px

    static final Color BG_BOARD = new Color(15, 15, 25);
    static final Color BG_PANEL = new Color(10, 10, 18);
    static final Color GRID_LINE = new Color(30, 30, 50);
    static final Color GHOST_CLR = new Color(255, 255, 255, 40);
    static final Color TEXT_CLR = new Color(220, 220, 240);

    private final Board board;
    private final ScoreManager scoreManager;
    private final TetrominoFactory factory;
    private GameLoop gameLoop;

    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_W, PANEL_H));
        setBackground(BG_PANEL);
        setFocusable(true);

        board = new Board();
        scoreManager = new ScoreManager();
        factory = new TetrominoFactory();
        gameLoop = new GameLoop(board, scoreManager, factory, this);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e.getKeyCode());
            }
            @Override
            public void keyReleased(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_DOWN)
                    gameLoop.onKeySoftDrop(false);
            }
        });
    }

    public void startGame() {
        gameLoop.start();
        requestFocusInWindow();
    }

    @Override public void onUpdate()   { repaint(); }
    @Override public void onGameOver() { repaint(); }

    private void handleKeyPressed(int key) {
        switch (key) {
            case KeyEvent.VK_LEFT: gameLoop.onKeyLeft(); break; // ← 
            case KeyEvent.VK_RIGHT: gameLoop.onKeyRight(); break; // →
            case KeyEvent.VK_DOWN: gameLoop.onKeySoftDrop(true); break; // ↓ 
            case KeyEvent.VK_UP: gameLoop.onKeyRotateCW(); break; // ↑ 
            case KeyEvent.VK_Z: gameLoop.onKeyRotateCCW(); break; 
            case KeyEvent.VK_X: gameLoop.onKeyRotateCW(); break; 
            case KeyEvent.VK_SPACE: gameLoop.onKeyHardDrop();  break;
            case KeyEvent.VK_C: gameLoop.onKeyHold(); break;
            case KeyEvent.VK_SHIFT: gameLoop.onKeyHold(); break; 
            case KeyEvent.VK_P: gameLoop.onKeyPause(); break; // P pause
            case KeyEvent.VK_R: restartGame(); break; // R restart
        }
    }

    private void restartGame() {
        gameLoop.stop();
        board.reset();
        scoreManager.reset();
        gameLoop = new GameLoop(board, scoreManager, factory, this);
        gameLoop.start();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ox = 10, oy = 10;

        drawBoard(g2, ox, oy);
        drawGrid(g2, ox, oy);

        Tetromino cur = gameLoop.getCurrentPiece();
        if (cur != null) {
            drawGhostPiece(g2, ox, oy, cur);
            drawCurrentPiece(g2, ox, oy, cur);
        }

        drawSidePanel(g2, ox + BOARD_W + 20, oy);

        if (gameLoop.isPaused()) drawOverlay(g2, "PAUSED", "Nhấn P để tiếp tục");
        if (gameLoop.isGameOver()) drawOverlay(g2, "GAME OVER", "Nhấn R để chơi lại  |  Điểm: " + scoreManager.getScore());
    }

    private void drawBoard(Graphics2D g, int ox, int oy) {
        g.setColor(BG_BOARD);
        g.fillRect(ox, oy, BOARD_W, BOARD_H);

        for (int r = Board.HIDDEN_ROWS; r < Board.TOTAL_ROWS; r++) {
            for (int c = 0; c < Board.WIDTH; c++) {
                Cell cell = board.getCell(r, c);
                if (cell != null && cell.isOccupied()) {
                    int px = ox + c * CELL_SIZE;
                    int py = oy + (r - Board.HIDDEN_ROWS) * CELL_SIZE;
                    drawCell(g, px, py, cell.getColor());
                }
            }
        }
    }

    private void drawGrid(Graphics2D g, int ox, int oy) {
        g.setColor(GRID_LINE);
        g.setStroke(new BasicStroke(0.5f));
        for (int r = 0; r <= Board.HEIGHT; r++)
            g.drawLine(ox, oy + r * CELL_SIZE, ox + BOARD_W, oy + r * CELL_SIZE);
        for (int c = 0; c <= Board.WIDTH; c++)
            g.drawLine(ox + c * CELL_SIZE, oy, ox + c * CELL_SIZE, oy + BOARD_H);
        g.setColor(new Color(60, 60, 90));
        g.setStroke(new BasicStroke(2f));
        g.drawRect(ox, oy, BOARD_W, BOARD_H);
    }

    private void drawGhostPiece(Graphics2D g, int ox, int oy, Tetromino cur) {
        int ghostY = board.getGhostY(cur);
        int[][] shape = cur.getShape();
        int px = cur.getPosition().x;

        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] == 0) continue;
                int boardRow = ghostY + r;
                if (boardRow < Board.HIDDEN_ROWS) continue;
                int drawX = ox + (px + c) * CELL_SIZE;
                int drawY = oy + (boardRow - Board.HIDDEN_ROWS) * CELL_SIZE;
                g.setColor(GHOST_CLR);
                g.fillRect(drawX + 1, drawY + 1, CELL_SIZE - 2, CELL_SIZE - 2);
                g.setColor(new Color(255, 255, 255, 60));
                g.drawRect(drawX + 1, drawY + 1, CELL_SIZE - 3, CELL_SIZE - 3);
            }
        }
    }

    private void drawCurrentPiece(Graphics2D g, int ox, int oy, Tetromino cur) {
        int[][] shape = cur.getShape();
        int px  = cur.getPosition().x;
        int py = cur.getPosition().y;
        Color color = cur.getColor();

        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] == 0) continue;
                int boardRow = py + r;
                if (boardRow < Board.HIDDEN_ROWS) continue;
                int drawX = ox + (px + c) * CELL_SIZE;
                int drawY = oy + (boardRow - Board.HIDDEN_ROWS) * CELL_SIZE;
                drawCell(g, drawX, drawY, color);
            }
        }
    }

    private void drawSidePanel(Graphics2D g, int x, int y) {
        Tetromino next = gameLoop.getNextPiece();
        Tetromino held = gameLoop.getHeldPiece();

        // SCORE
        drawLabel(g, "SCORE", x, y + 20);
        drawValue(g, String.valueOf(scoreManager.getScore()), new Color(167, 139, 250), x, y + 42);

        // LEVEL
        drawLabel(g, "LEVEL", x, y + 80);
        drawValue(g, String.valueOf(scoreManager.getLevel()), new Color(110, 231, 183), x, y + 102);

        // LINES
        drawLabel(g, "LINES", x, y + 140);
        drawValue(g, String.valueOf(scoreManager.getLines()), new Color(147, 197, 253), x, y + 162);

        // NEXT
        drawLabel(g, "NEXT", x, y + 205);
        if (next != null) drawMiniPiece(g, x, y + 215, next, 120, 75);

        // HOLD
        drawLabel(g, "HOLD", x, y + 310);
        if (held != null) drawMiniPiece(g, x, y + 320, held, 120, 75);
        else {
            g.setColor(BG_BOARD); g.fillRect(x, y + 320, 120, 75);
            g.setColor(GRID_LINE); g.drawRect(x, y + 320, 120, 75);
        }

        // Controls
        g.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g.setColor(new Color(80, 80, 110));
        int cy = y + 420;
        String[] tips = {"← → di chuyển","↑/X  xoay CW","Z    xoay CCW","↓    soft drop","SPC  hard drop","C    hold","P    pause","R    restart"};
        for (String tip : tips) { g.drawString(tip, x, cy); cy += 15; }
    }

    private void drawLabel(Graphics2D g, String text, int x, int y) {
        g.setFont(new Font("Monospaced", Font.BOLD, 11));
        g.setColor(new Color(100, 100, 140));
        g.drawString(text, x, y);
    }

    private void drawValue(Graphics2D g, String text, Color color, int x, int y) {
        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        g.setColor(color);
        g.drawString(text, x, y);
    }

    private void drawMiniPiece(Graphics2D g, int x, int y, Tetromino t, int boxW, int boxH) {
        g.setColor(BG_BOARD); g.fillRect(x, y, boxW, boxH);
        g.setColor(GRID_LINE); g.drawRect(x, y, boxW, boxH);

        int[][] shape = t.getShape();
        Color color = t.getColor();
        int cs = 20;
        int startX = x + (boxW - shape[0].length * cs) / 2;
        int startY = y + (boxH - shape.length    * cs) / 2;

        for (int r = 0; r < shape.length; r++)
            for (int c = 0; c < shape[r].length; c++)
                if (shape[r][c] == 1)
                    drawCell(g, startX + c * cs, startY + r * cs, color, cs);
    }

    private void drawOverlay(Graphics2D g, String title, String sub) {
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(10, 10, BOARD_W, BOARD_H);

        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(new Color(167, 139, 250));
        g.drawString(title, 10 + (BOARD_W - fm.stringWidth(title)) / 2, 10 + BOARD_H / 2 - 20);

        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        fm = g.getFontMetrics();
        g.setColor(TEXT_CLR);
        g.drawString(sub, 10 + (BOARD_W - fm.stringWidth(sub)) / 2, 10 + BOARD_H / 2 + 12);
    }

    private void drawCell(Graphics2D g, int x, int y, Color color) { drawCell(g, x, y, color, CELL_SIZE); }

    private void drawCell(Graphics2D g, int x, int y, Color color, int size) {
        g.setColor(color);
        g.fillRect(x + 1, y + 1, size - 2, size - 2);
        g.setColor(color.brighter().brighter());
        g.fillRect(x + 1, y + 1, size - 2, 3);
        g.fillRect(x + 1, y + 1, 3, size - 2);
        g.setColor(color.darker().darker());
        g.fillRect(x + 1, y + size - 3, size - 2, 2);
        g.fillRect(x + size - 3, y + 1, 2, size - 2);
    }
}

class ScoreManager {

    private static final int[] LINE_SCORES = {0, 100, 300, 500, 800};

    private int score;
    private int level;
    private int lines;

    public ScoreManager() { reset(); }

    public void addLineClear(int linesCleared) {
        if (linesCleared <= 0) return;
        score += LINE_SCORES[linesCleared] * level;
        lines += linesCleared;
        level  = (lines / 10) + 1;
    }

    public void addSoftDrop(int rows) { score += rows; }

    public void addHardDrop(int rows) { score += rows * 2; }

    public int getFallSpeed() {
        return Math.max(80, 1000 - (level - 1) * 100);
    }

    public void reset() { score = 0; level = 1; lines = 0; }

    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getLines() { return lines; }
}
