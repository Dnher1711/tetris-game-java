public class Board {

    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;
    public static final int HIDDEN_ROWS = 2;
    public static final int TOTAL_ROWS = HEIGHT + HIDDEN_ROWS;

    private Cell[][] grid;

    public Board() {
        grid = new Cell[TOTAL_ROWS][WIDTH];
        for (int r = 0; r < TOTAL_ROWS; r++)
            for (int c = 0; c < WIDTH; c++)
                grid[r][c] = new Cell();
    }

    public boolean isValidPosition(Tetromino t, Point p, int[][] shape) {
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] == 0) continue; 

                int boardCol = p.x + c; 
                int boardRow = p.y + r; 

                if (boardCol < 0 || boardCol >= WIDTH) return false;
                if (boardRow >= TOTAL_ROWS) return false;
                if (boardRow < 0) continue;
                if (grid[boardRow][boardCol].isOccupied()) return false;
            }
        }
        return true;
    }

    public void placeTetromino(Tetromino t) {
        java.awt.Color color = t.getColor();

        for (Point p : t.getCells()) {
            if (p.y >= 0 && p.y < TOTAL_ROWS && p.x >= 0 && p.x < WIDTH) {
                grid[p.y][p.x].occupy(color);
            }
        }
    }

    public int clearLines() {
        int cleared = 0;

        for (int r = TOTAL_ROWS - 1; r >= 0; r--) {
            if (isRowFull(r)) {
                removeRow(r);
                cleared++;
                r++;
            }
        }
        return cleared;
    }

    public static int calcScore(int linesCleared, int level) {
        final int[] BASE_SCORES = {0, 100, 300, 500, 800};

        if (linesCleared < 0 || linesCleared > 4) return 0;
        return BASE_SCORES[linesCleared] * (level + 1);
    }

    private boolean isRowFull(int r) {
        for (int c = 0; c < WIDTH; c++)
            if (!grid[r][c].isOccupied()) return false;
        return true;
    }

    private void removeRow(int r) {
        for (int row = r; row > 0; row--) {
            for (int c = 0; c < WIDTH; c++) {
                grid[row][c] = grid[row - 1][c].copy();
            }
        }

        for (int c = 0; c < WIDTH; c++) {
            grid[0][c] = new Cell();
        }
    }

    public int[] getClearedRowIndices() {
        java.util.List<Integer> indices = new java.util.ArrayList<>();
        for (int r = HIDDEN_ROWS ; r < TOTAL_ROWS; r++) {
            if (isRowFull(r)) indices.add(r);
        }
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }

    public int getGhostY(Tetromino t) {
        int ghostY = t.getPosition().y;
        while (isValidPosition(t, new Point(t.getPosition().x, ghostY + 1), t.getShape())) {
            ghostY++;
        }
        return ghostY; 
    }

    public Cell getCell(Point p) {
        if (p.x < 0 || p.x >= WIDTH || p.y < 0 || p.y >= TOTAL_ROWS) return null;
        return grid[p.y][p.x];
    }

    public Cell getCell(int row, int col) {
        return getCell(new Point(col, row));
    }

    public boolean isGameOver() {
        for (int r = 0; r < HIDDEN_ROWS; r++)
            for (int c = 0; c < WIDTH; c++)
                if (grid[r][c].isOccupied()) return true;
        return false;
    }

    public void reset() {
        for (int r = 0; r < TOTAL_ROWS; r++)
            for (int c = 0; c < WIDTH; c++)
                grid[r][c] = new Cell();
    }

    public void printDebug() {
        System.out.println("+" + "-".repeat(WIDTH) + "+");
        for (int r = HIDDEN_ROWS; r < TOTAL_ROWS; r++) {
            System.out.print("|");
            for (int c = 0; c < WIDTH; c++)
                System.out.print(grid[r][c]);
            System.out.println("|");
        }
        System.out.println("+" + "-".repeat(WIDTH) + "+");
    }

    public Cell[][] getGrid()  { return grid; }
    public int getWidth()      { return WIDTH; }
    public int getHeight()     { return HEIGHT; }
}

