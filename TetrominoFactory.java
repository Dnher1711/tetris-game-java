import java.util.*;

public class TetrominoFactory {

    private Queue<TetrominoType> bag;

    public TetrominoFactory() {
        bag = new ArrayDeque<>();
        refillBag();
        refillBag(); 
    }

    public Tetromino next() {
        if (bag.size() < 7) refillBag();
        return new Tetromino(bag.poll());
    }

    public List<TetrominoType> peekNext(int count) {
        List<TetrominoType> preview = new ArrayList<>(bag);
        return preview.subList(0, Math.min(count, preview.size()));
    }

    public void refillBag() {
        List<TetrominoType> newBag = new ArrayList<>(Arrays.asList(TetrominoType.values()));
        Collections.shuffle(newBag);
        bag.addAll(newBag);
    }
}

