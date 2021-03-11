
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.ArrayList;

public class Audios {
    private ArrayList<String> uids = new ArrayList<String>();
    private ArrayList<String> filename = new ArrayList<String>();
    private ArrayList<byte[]> byteArrays = new ArrayList<byte[]>();
    String namespace;

    public Audios(String namespace) {
        this.namespace = namespace;
        uids.clear();
        filename.clear();
        byteArrays.clear();
    }

    public Audios(String namespace, String files[]) {
        this.namespace = namespace;
        int lastFile = 0, fileAmount = 0;
        uids.clear();
        filename.clear();
        byteArrays.clear();
        try {
            for (int i = 0; i < files.length; i++) {
                if (files[i].contains("++audio++")) {
                    lastFile = i;
                    files[i] = files[i].replace("++audio++", "");
                    uids.add(files[i].split("---")[0]);
                    filename.add(files[i].split("---")[1]);
                    byteArrays.add(FileManager.readFileToByteArray(Manager.pathExtension + "adventures/" + namespace + "/audio/" + uids.get(fileAmount) + ".wav"));
                    fileAmount++;
                }
            }
        } catch (Exception e) {
            Popup.error(StaticStuff.PROJECT_NAME, "Audio contains invalid data.\n" + e);
        }
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String generateSaveString() {
        for (int i = 0; i < uids.size(); i++) {
            try {
                FileManager.writeFileFromByteArray(Manager.pathExtension + "adventures/" + namespace + "/audio/" + uids.get(i) + ".wav", byteArrays.get(i));
            } catch (Exception e) {
                Popup.error(StaticStuff.PROJECT_NAME, "Audio '" + filename.get(i) + "' contains invalid data.\n" + e);
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
            str = str + "\n" + filename.get(i) + " --- " + uids.get(i);
        return str;
    }

    public boolean addAudio(String name, String path) {
        if (StaticStuff.isAudioFile(path))
            try {
                byteArrays.add(FileManager.readFileToByteArray(path));
                this.filename.add(name);
                StaticStuff.setLastCreatedUID(UID.generateUID());
                this.uids.add(StaticStuff.getLastCreatedUID());
                return true;
            } catch (Exception e) {
                Popup.error(StaticStuff.PROJECT_NAME, "Unable to get audio file:\n" + e);
                return false;
            }
        else {
            Popup.message(StaticStuff.PROJECT_NAME, "Invalid format.\nOnly .wav files are accepted.");
            return false;
        }
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
            Audio.playAudioInputStream(play);
            return true;
        } catch (Exception e) {
            Popup.error(StaticStuff.PROJECT_NAME, "Unable to play audio file:\n" + e);
            return true;
        }
    }

    public boolean audioExists(String uid) {
        return uids.contains(uid);
    }

    public String getAudioName(String uid) {
        return filename.get(uids.indexOf(uid));
    }

    public ArrayList<String> getUids() {
        return uids;
    }

    public int refactor(String find, String replace) {
        int occ = 0;
        occ += StaticStuff.refactorArrayList(find, replace, filename);
        occ += StaticStuff.refactorArrayList(find, replace, uids);
        return occ;
    }
}
