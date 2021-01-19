
import java.util.ArrayList;

public class ProjectSettings {
    public static ArrayList<String> settings = new ArrayList<String>();

    public ProjectSettings(String[] fileInput) {
        settings.clear();
        for (int i = 0; i < fileInput.length; i++)
            if (fileInput[i].contains(":")) settings.add(fileInput[i]);
        addAllRequired();
    }

    private void addAllRequired() {
        addIfNotContain("name", Manager.filename);
        addIfNotContain("description", "[[gold:My great adventure]]");
        addIfNotContain("version", "1.0");
        addIfNotContain("author", "Yan Wittmann");
        addIfNotContain("image", "");
        addIfNotContain("autocomplete", "");
        addIfNotContain("objectFrameVariables", "weight,damage,range,value,health,hands,armor");
        addIfNotContain("showIntro", "true");
        addIfNotContain("password", "");
        addIfNotContain("debugMode", "false");
        addIfNotContain("debugModeForceable", "false");
    }

    private void addIfNotContain(String option, String value) {
        for (int i = 0; i < settings.size(); i++)
            if (settings.get(i).contains(option + ":")) return;
        settings.add(option + ":" + value);
    }

    public String getValue(String name) {
        for (int i = 0; i < settings.size(); i++) {
            if (settings.get(i).contains(name + ":"))
                if (!settings.get(i).replaceAll(":[^:]+", "").replace(":", "").equals("password")) {
                    return settings.get(i).replaceAll("[^:]+:(.+)", "$1");
                } else {
                    return Interpreter.lang("errorUnauthorizedAccessToVar");
                }
        }
        return "";
    }

    public void setValue(String name, String value) {
        for (int i = 0; i < settings.size(); i++)
            if (settings.get(i).contains(name + ":")) settings.set(i, name + ":" + value);
    }

    public void setValue(String name, int value) {
        for (int i = 0; i < settings.size(); i++)
            if (settings.get(i).contains(name + ":")) settings.set(i, name + ":" + value);
    }

    public static String[] getNames() {
        String names[] = new String[settings.size()];
        for (int i = 0; i < names.length; i++)
            names[i] = settings.get(i).replaceAll(":.+", "").replace(":", "");
        return names;
    }

    public String[] generateSaveString() {
        String[] dest = new String[settings.toArray().length];
        System.arraycopy(settings.toArray(), 0, dest, 0, settings.toArray().length);
        return dest;
    }
}
