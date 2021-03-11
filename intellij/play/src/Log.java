
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public abstract class Log {
    private static final ArrayList<String> log = new ArrayList<>();
    private static boolean activated = false;
    private static int indent = 0;
    private static GuiLogger gui = null;

    public static void add(String text) {
        if (activated) {
            StringBuilder textBuilder = new StringBuilder(text);
            for (int i = 0; i < indent; i++) textBuilder.insert(0, " ");
            text = textBuilder.toString();
            System.out.println(text);
            log.add(text);
            gui.updateText();
        }
    }

    public static void add(Object output) {
        StringBuilder text = new StringBuilder("" + output);
        if (activated) {
            for (int i = 0; i < indent; i++) text.insert(0, " ");
            System.out.println(text);
            log.add(text.toString());
            gui.updateText();
        }
    }

    public static void dump(String filename) {
        if (filename.equals(""))
            filename = "../log (" + Manager.filename + ") (" + String.valueOf(new Timestamp(new Date().getTime())).replace(":", "-") + ").txt";
        else filename = "../" + filename + (filename.contains(".txt") ? "" : ".txt");
        add("Dumping log to: " + filename);
        String[] logLines = new String[log.size()];
        for (int i = 0; i < logLines.length; i++) logLines[i] = log.get(i);
        FileManager.writeToFile(filename, logLines);
    }

    public static void activate() {
        activated = true;
        if (gui == null && Interpreter.getSettingsValue("debugMode").equals("true")) gui = new GuiLogger("Debugger - Logger", log);
    }

    public static void deactivate() {
        activated = false;
    }

    public static void setActive(boolean active) {
        activated = active;
        if (activated && gui == null && Interpreter.getSettingsValue("debugMode").equals("true")) gui = new GuiLogger("Debugger - Logger", log);
    }

    public static boolean isActive() {
        return activated;
    }

    public static void addIndent() {
        indent++;
    }

    public static void removeIndent() {
        if (indent > 0) indent--;
    }

    public static void resetIndent() {
        indent = 0;
    }

    public static void debug(String text) {
        text = "D: " + text;
        System.out.println(text);
        log.add(text);
        if (activated) {
            try {
                gui.updateText();
            } catch (Exception ignored) {
            }
        }
    }
}
