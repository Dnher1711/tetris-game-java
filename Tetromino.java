import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Tetromino {

    private static final int[][][][] ALL_SHAPES = {

        {
            { {0,0,0,0}, {1,1,1,1}, {0,0,0,0}, {0,0,0,0} }, // 0°
            { {0,0,1,0}, {0,0,1,0}, {0,0,1,0}, {0,0,1,0} }, // 90°
            { {0,0,0,0}, {0,0,0,0}, {1,1,1,1}, {0,0,0,0} }, // 180°
            { {0,1,0,0}, {0,1,0,0}, {0,1,0,0}, {0,1,0,0} }  // 270°
        },

        {
            { {1,1}, {1,1} },
            { {1,1}, {1,1} },
            { {1,1}, {1,1} },
            { {1,1}, {1,1} }
        },

        {
            { {0,1,0}, {1,1,1}, {0,0,0} },
            { {0,1,0}, {0,1,1}, {0,1,0} },
            { {0,0,0}, {1,1,1}, {0,1,0} },
            { {0,1,0}, {1,1,0}, {0,1,0} }
        },

        {
            { {0,1,1}, {1,1,0}, {0,0,0} },
            { {0,1,0}, {0,1,1}, {0,0,1} },
            { {0,0,0}, {0,1,1}, {1,1,0} },
            { {1,0,0}, {1,1,0}, {0,1,0} }
        },

        {
            { {1,1,0}, {0,1,1}, {0,0,0} },
            { {0,0,1}, {0,1,1}, {0,1,0} },
            { {0,0,0}, {1,1,0}, {0,1,1} },
            { {0,1,0}, {1,1,0}, {1,0,0} }
        },

        {
            { {1,0,0}, {1,1,1}, {0,0,0} },
            { {0,1,1}, {0,1,0}, {0,1,0} },
            { {0,0,0}, {1,1,1}, {0,0,1} },
            { {0,1,0}, {0,1,0}, {1,1,0} }
        },

        {
            { {0,0,1}, {1,1,1}, {0,0,0} },
            { {0,1,0}, {0,1,0}, {0,1,1} },
            { {0,0,0}, {1,1,1}, {1,0,0} },
            { {1,1,0}, {0,1,0}, {0,1,0} }
        }
    };

    private TetrominoType type;
    private Point         position; 
    private int           rotation; 
    private int[][]       shape;

    public Tetromino(TetrominoType type) {
        this.type     = type;
        this.rotation = 0;
        this.shape    = ALL_SHAPES[type.ordinal()][0];

        int spawnX = (Board.WIDTH - shape[0].length) / 2;
        this.position = new Point(spawnX, -Board.HIDDEN_ROWS);
    }

    public void rotateCW(Board board) {
        int nextRot   = (rotation + 1) % 4;
        int[][] next  = ALL_SHAPES[type.ordinal()][nextRot];

        int[] kicks = { 0, 1, -1, 2, -2 };
        for (int kick : kicks) {
            Point newPos = new Point(position.x + kick, position.y);
            if (board.isValidPosition(this, newPos, next)) {
                rotation = nextRot;
                shape    = next;
                position = newPos;
                return;
            }
        }
    }

    public void rotateCCW(Board board) {
        int nextRot  = (rotation + 3) % 4;
        int[][] next = ALL_SHAPES[type.ordinal()][nextRot];

        int[] kicks = { 0, -1, 1, -2, 2 };
        for (int kick : kicks) {
            Point newPos = new Point(position.x + kick, position.y);
            if (board.isValidPosition(this, newPos, next)) {
                rotation = nextRot;
                shape    = next;
                position = newPos;
                return;
            }
        }
    }

    public boolean move(int dx, int dy, Board board) {
        Point newPos = new Point(position.x + dx, position.y + dy);
        if (board.isValidPosition(this, newPos, this.shape)) {
            position = newPos;
            return true;
        }
        return false;
    }

    public List<Point> getCells() {
        List<Point> cells = new ArrayList<>();
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] == 1) {
                    cells.add(new Point(position.x + c, position.y + r));
                }
            }
        }
        return cells;
    }

    public Color getColor() {
        return TetrisColor.fromType(type).toAwtColor();
    }

    public TetrominoType getType()     { return type; }
    public Point         getPosition() { return position; }
    public int           getRotation() { return rotation; }
    public int[][]       getShape()    { return shape; }
    public int           getWidth()    { return shape[0].length; }
    public int           getHeight()   { return shape.length; }

    @Override
    public String toString() {
        return "Tetromino[" + type + " pos=" + position + " rot=" + rotation + "°]";
    }
}

