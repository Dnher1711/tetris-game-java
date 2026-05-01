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

    private static final int[][][] KICKS_JLSTZ = {
        // from 0°
        { {0,0}, {-1,0}, {-1, 1}, {0,-2}, {-1,-2} },
        // from 90°
        { {0,0}, { 1,0}, { 1,-1}, {0, 2}, { 1, 2} },
        // from 180°
        { {0,0}, { 1,0}, { 1, 1}, {0,-2}, { 1,-2} },
        // from 270°
        { {0,0}, {-1,0}, {-1,-1}, {0, 2}, {-1, 2} }
    };

    private static final int[][][] KICKS_I = {
        // from 0°
        { {0,0}, {-2,0}, { 1,0}, {-2,-1}, { 1, 2} },
        // from 90°
        { {0,0}, {-1,0}, { 2,0}, {-1, 2}, { 2,-1} },
        // from 180°
        { {0,0}, { 2,0}, {-1,0}, { 2, 1}, {-1,-2} },
        // from 270°
        { {0,0}, { 1,0}, {-2,0}, { 1,-2}, {-2, 1} }
    };
    public  static final int LOCK_DELAY_MS   = 500;
    private static final int MAX_LOCK_RESETS = 15;

    private TetrominoType type;
    private Point position; 
    private int rotation; 
    private int[][] shape;

    private boolean isOnGround;
    private long    lockTimer;
    private int     lockResetCount;
    private boolean lastMoveWasRotation;
    private int softDropDistance;

    public Tetromino(TetrominoType type) {
        this.type = type;
        this.rotation = 0;
        this.shape = ALL_SHAPES[type.ordinal()][0];

        int spawnX = (Board.WIDTH - shape[0].length) / 2;
        this.position = new Point(spawnX, -Board.HIDDEN_ROWS);
        this.isOnGround = false;
        this.lockTimer = 0;
        this.lockResetCount = 0;
        this.lastMoveWasRotation = false;
        this.softDropDistance = 0;
    }

    public boolean rotateCW(Board board) {
        int nextRot   = (rotation + 1) % 4;
        int[][] next  = ALL_SHAPES[type.ordinal()][nextRot];

        int[][] kicks = getKickData(rotation, true);
        for (int[] kick : kicks) {
            Point newPos = new Point(position.x + kick[0], position.y - kick[1]);
            if (board.isValidPosition(this, newPos, next)) {
                rotation = nextRot;
                shape = next;
                position = newPos;
                lastMoveWasRotation = true;
                resetLockDelayIfOnGround();
                return true;
            }
        }
        return false;
    }

    public boolean rotateCCW(Board board) {
        int nextRot  = (rotation + 3) % 4;
        int[][] next = ALL_SHAPES[type.ordinal()][nextRot];

        int[][] kicks = getKickData(nextRot, false);
        for (int[] kick : kicks) {
            Point newPos = new Point(position.x - kick[0], position.y + kick[1]);
            if (board.isValidPosition(this, newPos, next)) {
                rotation = nextRot;
                shape = next;
                position = newPos;
                lastMoveWasRotation = true;
                resetLockDelayIfOnGround();
                return true;
            }
        }
        return false;
    }

    private int[][] getKickData(int fromRot, boolean isCW) {
        if (type == TetrominoType.O) {
            return new int[][]{{0, 0}};
        }
        if (type == TetrominoType.I) {
            return KICKS_I[fromRot];
        }
        return KICKS_JLSTZ[fromRot];
    }

    public boolean move(int dx, int dy, Board board, boolean isSoftDrop) {
        Point newPos = new Point(position.x + dx, position.y + dy);
        if (board.isValidPosition(this, newPos, this.shape)) {
            position = newPos;
            lastMoveWasRotation = false;

            if (isSoftDrop && dy > 0) {
                softDropDistance += dy;
            }

            if (dx != 0 && dy == 0) {
                resetLockDelayIfOnGround();
            }
            updateGroundState(board);
            return true;
        }
        return false;
    }

    public boolean move(int dx, int dy, Board board) {
        return move(dx, dy, board, false);
    }

    public void updateGroundState(Board board) {
        boolean canFallFurther = board.isValidPosition(this, new Point(position.x, position.y + 1), shape);
        if (!canFallFurther) {
            if (!isOnGround) {
                isOnGround  = true;
                lockTimer   = System.currentTimeMillis();
            }
        } else {
            isOnGround = false;
        }
    }

    private void resetLockDelayIfOnGround() {
        if (isOnGround && lockResetCount < MAX_LOCK_RESETS) {
            lockTimer = System.currentTimeMillis();
            lockResetCount++;
        }
    }

    public boolean shouldLock(Board board) {
        if (!isOnGround) return false;

        boolean canFall = board.isValidPosition(this, new Point(position.x, position.y + 1), shape);
        if (canFall) {
            isOnGround = false;
            return false;
        }

        return (System.currentTimeMillis() - lockTimer) >= LOCK_DELAY_MS;
    }

    public void forceLock() {
        isOnGround = true;
        lockTimer  = 0;
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

    public TetrominoType getType() { return type; }
    public Point getPosition() { return position; }
    public int getRotation() { return rotation; }
    public int[][] getShape() { return shape; }
    public int getWidth() { return shape[0].length; }
    public int getHeight() { return shape.length; }
    public boolean isOnGround() { return isOnGround; }
    public boolean lastMoveWasRotation() { return lastMoveWasRotation; }
    public int getSoftDropDistance() { return softDropDistance; }
    public void resetSoftDropDistance() { softDropDistance = 0; }

    @Override
    public String toString() {
        return "Tetromino[" + type + " pos=" + position + " rot=" + rotation + "°" + (isOnGround ? " onGround" : "") + "]";
    }
}

