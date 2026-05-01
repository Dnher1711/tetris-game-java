import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameLoop {

    private static final int[] FALL_INTERVALS = {
        1000, 793, 618, 473, 355,     // Level  1 –  5
         263, 183, 116,  67,  33,     // Level  6 – 10
          17,  10,  10,  10,  10      // Level 11 – 15
    };

    private static final int SOFT_DROP_INTERVAL = 50;

    private static final int LOCK_DELAY = 500;

    private static final int MAX_LOCK_RESETS = 15;

    private static final int SPAWN_DELAY = 100;

    private static final int SOFT_DROP_SCORE = 1;

    private static final int HARD_DROP_SCORE = 2;

    private final Board            board;
    private final ScoreManager     scoreManager;
    private final TetrominoFactory factory;
    private final GameLoopListener listener;  
    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private Tetromino heldPiece;      
    private boolean   canHold = true;

    private boolean isRunning  = false;
    private boolean isPaused   = false;
    private boolean isGameOver = false;

    private boolean isSoftDropping = false;
    private boolean isLocking      = false; 
    private int     lockResetCount = 0; 

    private final Timer gravityTimer;  
    private final Timer lockTimer;  
    private final Timer spawnTimer;

    public GameLoop(Board board, ScoreManager scoreManager,
                    TetrominoFactory factory, GameLoopListener listener) {
        this.board        = board;
        this.scoreManager = scoreManager;
        this.factory      = factory;
        this.listener     = listener;

        this.currentPiece = factory.next();
        this.nextPiece    = factory.next();

        gravityTimer = new Timer(getFallInterval(), e -> tickGravity());
        gravityTimer.setRepeats(true);

        lockTimer = new Timer(LOCK_DELAY, e -> executeLock());
        lockTimer.setRepeats(false); 

        spawnTimer = new Timer(SPAWN_DELAY, e -> spawnNext());
        spawnTimer.setRepeats(false);
    }

    public void start() {
        isRunning  = false; 
        isPaused   = false;
        isGameOver = false;
        gravityTimer.start();
        isRunning = true;
        listener.onUpdate();
    }

    public void stop() {
        gravityTimer.stop();
        lockTimer.stop();
        spawnTimer.stop();
        isRunning = false;
    }

    public void onKeyPause() {
        if (isGameOver) return;
        isPaused = !isPaused;

        if (isPaused) {
            gravityTimer.stop();
            lockTimer.stop();
        } else {
            gravityTimer.start();
            if (isLocking) lockTimer.start();
        }
        listener.onUpdate();
    }

    public void reset() {
        stop();
        board.reset();
        scoreManager.reset();

        currentPiece = factory.next();
        nextPiece = factory.next();
        heldPiece = null;
        canHold = true;
        isLocking = false;
        lockResetCount = 0;
        isSoftDropping = false;
        isGameOver = false;

        start();
    }

    private void tickGravity() {
        if (isPaused || isGameOver) return;

        boolean fell = tryMoveDown();

        if (!fell && !isLocking) {
            startLockDelay();
        }

        listener.onUpdate();
    }

    private void startLockDelay() {
        isLocking = true;
        lockResetCount = 0;
        lockTimer.restart();
    }

    private void resetLockDelay() {
        if (!isLocking) return;

        if (lockResetCount >= MAX_LOCK_RESETS) {
            executeLock();
        } else {
            lockResetCount++;
            lockTimer.restart();
        }
    }

    private void executeLock() {
        lockTimer.stop();
        isLocking = false;
        lockResetCount = 0;

        if (tryMoveDown()) {
            return;
        }

        board.placeTetromino(currentPiece);
        int cleared = board.clearLines();
        if (cleared > 0) scoreManager.addLineClear(cleared);
        gravityTimer.setDelay(getFallInterval());
        if (board.isGameOver()) {
            isGameOver = true;
            gravityTimer.stop();
            listener.onGameOver();
            return;
        }

        canHold = true;
        gravityTimer.stop();
        spawnTimer.start();

        listener.onUpdate();
    }

    private void spawnNext() {
        currentPiece = nextPiece;
        nextPiece    = factory.next();
        isSoftDropping = false;

        gravityTimer.setDelay(getFallInterval());
        gravityTimer.start();

        listener.onUpdate();
    }

    public void onKeyLeft() {
        if (!canAct()) return;
        boolean moved = currentPiece.move(-1, 0, board);
        if (moved) resetLockDelay();
        listener.onUpdate();
    }

    public void onKeyRight() {
        if (!canAct()) return;
        boolean moved = currentPiece.move(1, 0, board);
        if (moved) resetLockDelay();
        listener.onUpdate();
    }

    public void onKeyRotateCW() {
        if (!canAct()) return;
        int rotBefore = currentPiece.getRotation();
        currentPiece.rotateCW(board);
        if (currentPiece.getRotation() != rotBefore) {
            resetLockDelay(); 
        }
        listener.onUpdate();
    }

    public void onKeyRotateCCW() {
        if (!canAct()) return;
        int rotBefore = currentPiece.getRotation();
        currentPiece.rotateCCW(board);
        if (currentPiece.getRotation() != rotBefore) {
            resetLockDelay();
        }
        listener.onUpdate();
    }

    public void onKeySoftDrop(boolean pressing) {
        if (!canAct()) return;
        isSoftDropping = pressing;
        gravityTimer.setDelay(pressing ? SOFT_DROP_INTERVAL : getFallInterval());

        if (pressing) {
            boolean fell = tryMoveDown();
            if (fell) {
                scoreManager.addSoftDrop(1);
                if (isLocking) resetLockDelay();
            } else if (!isLocking) {
                startLockDelay();
            }
            listener.onUpdate();
        }
    }

    public void onKeyHardDrop() {
        if (!canAct()) return;

        int ghostY   = board.getGhostY(currentPiece);
        int currentY = currentPiece.getPosition().y;
        int rowsFell = ghostY - currentY;

        while (currentPiece.move(0, 1, board)) { /* tiếp tục */ }

        if (rowsFell > 0) scoreManager.addHardDrop(rowsFell);

        lockTimer.stop();
        isLocking = false;
        executeLock();
    }

    public void onKeyHold() {
        if (!canAct() || !canHold) return;
        canHold = false;

        lockTimer.stop();
        isLocking = false;
        lockResetCount = 0;

        if (heldPiece == null) {
            heldPiece    = new Tetromino(currentPiece.getType());
            currentPiece = nextPiece;
            nextPiece    = factory.next();
        } else {
            Tetromino temp = new Tetromino(currentPiece.getType());
            currentPiece   = new Tetromino(heldPiece.getType());
            heldPiece      = temp;
        }

        listener.onUpdate();
    }

    private boolean canAct() {
        return isRunning && !isPaused && !isGameOver;
    }

    private int getFallInterval() {
        int idx = Math.min(scoreManager.getLevel() - 1, FALL_INTERVALS.length - 1);
        return isSoftDropping ? SOFT_DROP_INTERVAL : FALL_INTERVALS[idx];
    }

    public Tetromino getCurrentPiece() { return currentPiece; }
    public Tetromino getNextPiece()    { return nextPiece; }
    public Tetromino getHeldPiece()    { return heldPiece; }
    public boolean   isGameOver()      { return isGameOver; }
    public boolean   isPaused()        { return isPaused; }
    public boolean   isRunning()       { return isRunning; }
}