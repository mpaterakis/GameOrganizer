package Sound;

import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Manos Paterakis
 */
public class SoundPlayer {

    /**
     * Play a sound from a given file.
     *
     * @param soundType The type of sound to be played
     */
    public static synchronized void playSound(final SoundTypes soundType) {
        String soundFile = getFileName(soundType);
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            @Override
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(getClass().getResourceAsStream("/Files/Sounds/" + soundFile));
                    clip.open(inputStream);
                    clip.start();
                } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }
    
    /**
     * Get soundfile from enum.
     * 
     * @param soundType The type of sound to be played
     * @return String containing the filename of the sound
     */
    private static String getFileName(SoundTypes soundType) {
        switch(soundType) {
            case BASE_CLICK: {
                return "click-base.wav";
            }
            case LOW_CLICK: {
                return "click-low.wav";
            }
            case HIGH_CLICK: {
                return "click-high.wav";
            }
            case STARTUP: {
                return "startup.wav";
            }
            default: {
                return null;
            }
        }
    }

}
