
class ScoreManager {

    private static final int[] BASE_SCORES = {0, 100, 300, 500, 800};

    private static final int[] FALL_INTERVALS = {
        1000, 793, 618, 473, 355,   // Level  1 –  5
         263, 183, 116,  67,  33,   // Level  6 – 10
          17,  10,  10,  10,  10    // Level 11 – 15
    };

    private static final int LINES_PER_LEVEL = 10;

    private int score;
    private int level;
    private int lines;
    private int combo;

    public ScoreManager() { reset(); }

    public int calcScore(int linesCleared) {
        if (linesCleared <= 0 || linesCleared > 4) return 0;
        return BASE_SCORES[linesCleared] * level;
    }

    public void addLineClear(int linesCleared) {
        if (linesCleared <= 0){
             combo = 0;
             return;
        }
        int points = calcScore(linesCleared);
        if (combo > 0) {
            points += 50 * combo * level;
        }
        combo++;
        score += points;
        lines += linesCleared;
        level = Math.min((lines / LINES_PER_LEVEL) + 1, 15);
    }

    public void addSoftDrop(int rows) { score += rows; }

    public void addHardDrop(int rows) { score += rows * 2; }

    public int getFallSpeed() {
        int idx = Math.min(level - 1, FALL_INTERVALS.length - 1);
        return FALL_INTERVALS[idx];
    }

    public double getLevelProgress() {
        int linesThisLevel = lines - (level - 1) * LINES_PER_LEVEL;
        return Math.min((double) linesThisLevel / LINES_PER_LEVEL, 1.0);
    }

    public int getLinesUntilNextLevel(){
        return LINES_PER_LEVEL - (lines % LINES_PER_LEVEL);
    }

    public static String getLineClearName(int linesCleared) {
        switch (linesCleared) {
            case 1: return "Single";
            case 2: return "Double";
            case 3: return "Triple";
            case 4: return "Tetris!";
            default: return "";
        }
    }

    public void reset() { score = 0; level = 1; lines = 0;  combo = 0; }

    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getLines() { return lines; }
    public int getCombo() { return combo; }

    @Override
    public String toString() {
        return String.format("ScoreManager[score=%d, level=%d, lines=%d, combo=%d]",
                score, level, lines, combo);
    }
}