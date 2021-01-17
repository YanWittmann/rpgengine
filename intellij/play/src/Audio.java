
import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;

public class Audio {
    private static final HashMap<Clip, String> uids = new HashMap<>();

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
            clip.addLineListener(new CloseClipWhenDone());
            uids.put(clip, "[FILE]" + filename);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String stopToPlay = "";
    public static boolean print = false;

    public static void playAudioInputStream(AudioInputStream play, String uid) {
        try {
            AudioFormat af = play.getFormat();
            int size = (int) (af.getFrameSize() * play.getFrameLength());
            byte[] audio = new byte[size];
            DataLine.Info info = new DataLine.Info(Clip.class, af, size);
            play.read(audio, 0, size);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(af, audio, 0, size);
            clip.addLineListener(new CloseClipWhenDone());
            uids.put(clip, uid);
            clip.start();
            new Thread(() -> {
                while (true) {
                    Sleep.milliseconds(200);
                    if (print) {
                        GuiTextarea.appendToOutput("Playing: [[aqua:" + uid + "]]");
                        Sleep.milliseconds(200);
                        print = false;
                    }
                    if (stopToPlay.equals(uid) || !clip.isActive()) {
                        clip.stop();
                        Sleep.milliseconds(200);
                        stopToPlay = "";
                        break;
                    }
                }
            }).start();
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
                Log.add("Done playing Clip " + soundClip.toString());
                Interpreter.audioHasStopped(uids.get(soundClip));
                uids.remove(soundClip);
            }
        }
    }
}