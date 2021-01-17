
import javax.sound.sampled.*;
import java.io.File;

public class Audio {
    public static void playFile(String filename) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filename));
            AudioFormat af = audioInputStream.getFormat();
            int size = (int) (af.getFrameSize() * audioInputStream.getFrameLength());
            byte[] audio = new byte[size];
            DataLine.Info info = new DataLine.Info(Clip.class, af, size);
            audioInputStream.read(audio, 0, size);

            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(af, audio, 0, size);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playAudioInputStream(AudioInputStream play) {
        try {
            AudioFormat af = play.getFormat();
            int size = (int) (af.getFrameSize() * play.getFrameLength());
            byte[] audio = new byte[size];
            DataLine.Info info = new DataLine.Info(Clip.class, af, size);
            play.read(audio, 0, size);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(af, audio, 0, size);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class CloseClipWhenDone implements LineListener {
        @Override
        public void update(LineEvent event) {
            if (event.getType().equals(LineEvent.Type.STOP)) {
                Line soundClip = event.getLine();
                soundClip.close();
                System.out.println("Done playing " + soundClip.toString());
            }
        }
    }
}