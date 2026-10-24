package ghost;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/** Small synthesized arcade cues; audio is optional and never blocks gameplay. */
public final class SoundEffects {
    private static final float SAMPLE_RATE = 22050f;

    private final Clip pellet = makeClip(new int[]{880}, 55);
    private final Clip powerFruit = makeClip(new int[]{523, 659, 784}, 90);
    private final Clip ghost = makeClip(new int[]{784, 1047}, 80);
    private final Clip extraLife = makeClip(new int[]{659, 784, 988}, 100);
    private final Clip levelClear = makeClip(new int[]{523, 659, 784, 1047}, 120);
    private final Clip lifeLost = makeClip(new int[]{392, 330, 262}, 130);

    public void playPellet() { play(pellet); }
    public void playPowerFruit() { play(powerFruit); }
    public void playGhost() { play(ghost); }
    public void playExtraLife() { play(extraLife); }
    public void playLevelClear() { play(levelClear); }
    public void playLifeLost() { play(lifeLost); }

    private Clip makeClip(int[] notes, int noteLengthMs) {
        try {
            int framesPerNote = Math.round(SAMPLE_RATE * noteLengthMs / 1000f);
            byte[] audio = new byte[framesPerNote * notes.length * 2];
            int frame = 0;
            for (int note : notes) {
                for (int i = 0; i < framesPerNote; i++, frame++) {
                    double envelope = 1.0 - (double) i / framesPerNote;
                    double wave = Math.sin(2.0 * Math.PI * note * i / SAMPLE_RATE);
                    short sample = (short) (wave * envelope * 6500);
                    audio[frame * 2] = (byte) sample;
                    audio[frame * 2 + 1] = (byte) (sample >> 8);
                }
            }
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            Clip clip = AudioSystem.getClip();
            clip.open(format, audio, 0, audio.length);
            return clip;
        } catch (Exception unavailableAudio) {
            return null;
        }
    }

    private void play(Clip clip) {
        if (clip == null) return;
        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }
}
