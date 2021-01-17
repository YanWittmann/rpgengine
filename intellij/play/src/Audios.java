
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.ArrayList;

public class Audios {
    private ArrayList<String> uids = new ArrayList<>();
    private ArrayList<String> filename = new ArrayList<>();
    private ArrayList<byte[]> byteArrays = new ArrayList<>();
    String namespace;

    public Audios(String namespace) {
        this.namespace = namespace;
    }

    public Audios(String namespace, String files[]) {
        this.namespace = namespace;
        int lastFile = 0, fileAmount = 0;
        try {
            for (int i = 0; i < files.length; i++) {
                if (files[i].contains("++audio++")) {
                    lastFile = i;
                    files[i] = files[i].replace("++audio++", "");
                    uids.add(files[i].split("---")[0]);
                    filename.add(files[i].split("---")[1]);
                    byteArrays.add(FileManager.readFileToByteArray(Manager.extraFilePath + "adventures/" + namespace + "/audio/" + uids.get(fileAmount) + ".wav"));
                    fileAmount++;
                }
            }
        } catch (Exception e) {
            StaticStuff.error("Audio contains invalid data.\n" + e);
        }
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String generateSaveString() {
        for (int i = 0; i < uids.size(); i++) {
            try {
                FileManager.writeFileFromByteArray(Manager.extraFilePath + "adventures/" + namespace + "/audio/" + uids.get(i) + ".wav", byteArrays.get(i));
            } catch (Exception e) {
                StaticStuff.error("Audio '" + filename.get(i) + "' contains invalid data.\n" + e);
            }
        }
        String str = "";
        for (int i = 0; i < uids.size(); i++)
            str = str + "\n++audio++" + uids.get(i) + "---" + filename.get(i);
        return str;
    }

    public String generateMenuString() {
        String str = uids.size() + " audio file(s):";
        for (int i = 0; i < uids.size(); i++)
            str = str + "\n" + filename.get(i) + "  " + uids.get(i);
        return str;
    }

    public void addAudio(String name, String path) {
        if (name == null || path == null) return;
        if (name.equals("") || path.equals("")) return;
        try {
            byteArrays.add(FileManager.readFileToByteArray(path));
        } catch (Exception e) {
            StaticStuff.error("Unable to get audio file:<br>" + e);
        }
        this.filename.add(name);
        this.uids.add(UID.generateUID());
    }

    public boolean deleteAudio(String uid) {
        int index = uids.indexOf(uid);
        if (index == -1) return false;
        uids.remove(index);
        filename.remove(index);
        byteArrays.remove(index);
        return true;
    }

    public boolean playAudio(String uid) {
        if (!StaticStuff.isValidUID(uid)) return false;
        if (!uids.contains(uid)) return false;
        try {
            int index = uids.indexOf(uid);
            FileManager.writeFileFromByteArray("res/audio/playAudio.wav", byteArrays.get(index));
            AudioInputStream play = (AudioSystem.getAudioInputStream(new File("res/audio/playAudio.wav")));
            Audio.playAudioInputStream(play, uid);
            return true;
        } catch (Exception e) {
            StaticStuff.error("Unable to play audio file:<br>" + e);
            return true;
        }
    }

    public boolean audioExists(String uidName) {
        return (uids.contains(uidName) || filename.contains(uidName));
    }

    public String getAudioName(String uid) {
        return filename.get(uids.indexOf(uid));
    }

    public String getAudioUID(String name) {
        return uids.get(filename.indexOf(name));
    }

    public String[] getUIDs() {
        String ret[] = new String[uids.size()];
        for (int i = 0; i < uids.size(); i++) ret[i] = uids.get(i);
        return ret;
    }
}
