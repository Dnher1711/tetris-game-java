public class GameLogic {

    private static final int LINES_PER_LEVEL = 10;

    private static int getFallSpeed(int level) {
        return Math.max(100, 1000 - (level - 1) * 50);
    }

    private static final int[] LINE_SCORE_TABLE = { 0, 100, 300, 500, 800 };

    private static final int SOFT_DROP_BONUS = 1;

    private static final int HARD_DROP_BONUS = 2;

    private Board board;
    private Tetromino currentPiece;  
    private Tetromino nextPiece;  
    private TetrominoFactory factory;

    private int score;         
    private int level;          
    private int linesCleared;  
    private boolean gameOver;      

    private double  fallAccumulator;  
    private boolean started;          

    public GameLogic() {
        board   = new Board();
        factory = new TetrominoFactory();
        reset();
    }

    public void start() {
        reset();
        started = true;
    }

    public void reset() {
        board.reset();
        factory = new TetrominoFactory();
        score = 0;
        level = 1;
        linesCleared = 0;
        gameOver = false;
        fallAccumulator = 0;
        started = false;

        currentPiece = factory.next();
        nextPiece    = factory.next();

        currentPiece.updateGroundState(board);
    }

    public boolean checkCollision(Tetromino piece, int px, int py) {
        return board.isValidPosition(piece, new Point(px, py), piece.getShape());
    }

    public boolean checkCollision(Tetromino piece, int px, int py, int[][] shape) {
        return board.isValidPosition(piece, new Point(px, py), shape);
    }

    public boolean moveDown(boolean isSoftDrop) {
        if (gameOver) return false;

        boolean moved = currentPiece.move(0, 1, board, isSoftDrop);

        if (moved) {
            if (isSoftDrop) {
                score += SOFT_DROP_BONUS;
            }
            fallAccumulator = 0;
            return true;

        } else {
            return false;
        }
    }

    public boolean moveDown() {
        return moveDown(false);
    }

    public void moveLeft() {
        if (gameOver) return;
        currentPiece.move(-1, 0, board);
        currentPiece.updateGroundState(board);
    }

    public void moveRight() {
        if (gameOver) return;
        currentPiece.move(1, 0, board);
        currentPiece.updateGroundState(board);
    }

    public boolean rotate() {
        if (gameOver) return false;
        boolean success = currentPiece.rotateCW(board);
        if (success) {
            currentPiece.updateGroundState(board);
        }
        return success;
    }

    public boolean rotateCCW() {
        if (gameOver) return false;
        boolean success = currentPiece.rotateCCW(board);
        if (success) {
            currentPiece.updateGroundState(board);
        }
        return success;
    }

    public void hardDrop() {
        if (gameOver) return;

        int startY  = currentPiece.getPosition().y;
        int ghostY  = board.getGhostY(currentPiece);
        int dropped = ghostY - startY;

        currentPiece.move(0, dropped, board);

        score += dropped * HARD_DROP_BONUS;

        currentPiece.forceLock();
        lockPiece();
    }

    public void lockPiece() {
        if (gameOver) return;

        board.placeTetromino(currentPiece);

        int cleared = board.clearLines();

        updateScore(cleared);

        spawnNextPiece();
    }

    private void updateScore(int cleared) {
        if (cleared <= 0) return;

        linesCleared += cleared;
        score += LINE_SCORE_TABLE[Math.min(cleared, 4)] * level;
        level = (linesCleared / LINES_PER_LEVEL) + 1;
    }

    private void spawnNextPiece() {
        currentPiece = nextPiece;
        nextPiece    = factory.next();

        currentPiece.updateGroundState(board);

        if (!checkCollision(currentPiece,
                            currentPiece.getPosition().x,
                            currentPiece.getPosition().y)) {
            gameOver = true;
        }

        if (board.isGameOver()) {
            gameOver = true;
        }

        fallAccumulator = 0;
    }

    public void update(double deltaTimeMs) {
        if (!started || gameOver) return;

        fallAccumulator += deltaTimeMs;

        int fallSpeed = getFallSpeed(level);
        while (fallAccumulator >= fallSpeed) {
            fallAccumulator -= fallSpeed;

            if (!currentPiece.isOnGround()) {
                moveDown(false);
            }
        }

        if (currentPiece.shouldLock(board)) {
            lockPiece();
        }
    }

    public Board     getBoard()         { return board; }
    public Tetromino getCurrentPiece()  { return currentPiece; }
    public Tetromino getNextPiece()     { return nextPiece; }
    public int       getScore()         { return score; }
    public int       getLevel()         { return level; }
    public int       getLinesCleared()  { return linesCleared; }
    public boolean   isGameOver()       { return gameOver; }
    public boolean   isStarted()        { return started; }

    public int getCurrentFallSpeed() { return getFallSpeed(level); }
}
