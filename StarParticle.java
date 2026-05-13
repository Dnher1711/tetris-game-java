import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
public class SoundManager {
    private Clip moveClip, musicClip, rotateClip, breakClip, landClip;

    public SoundManager() {
        musicClip = loadClip("/music.wav");
        breakClip = loadClip ("/break (1).wav");
    }
    private Clip loadClip(String fileName) {
        try {
            InputStream is = SoundManager.class.getResourceAsStream(fileName);
            if (is == null) return null;
            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            return clip;
        } catch (Exception e) {
            System.err.println("Không tìm thấy: " + fileName);
            return null;
        }
    }