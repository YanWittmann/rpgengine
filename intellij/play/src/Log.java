
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public abstract class Log {
    private static ArrayList<String> log = new ArrayList<String>();
    private static boolean activated = false;
    private static int indent = 0;
    private static GuiLogger gui = null;

    public static void add(String text) {
        if (activated) {
            for (int i = 0; i < indent; i++) text = " " + text;
            System.out.println(text);
            log.add(text);
            gui.updateText();
        }
    }

    public static void add(Object output) {
        String text = "" + output;
        if (activated) {
            for (int i = 0; i < indent; i++) text = " " + text;
            System.out.println(text);
            log.add(text);
            gui.updateText();
        }
    }

    public static void dump(String filename) {
        if (filename.equals(""))
            filename = "../log (" + Manager.filename + ") (" + String.valueOf(new Timestamp(new Date().getTime())).replace(":", "-") + ").txt";
        else filename = "../" + filename + (filename.contains(".txt") ? "" : ".txt");
        add("Dumping log to: " + filename);
        String logLines[] = new String[log.size()];
        for (int i = 0; i < logLines.length; i++) logLines[i] = log.get(i);
        FileManager.writeToFile(filename, logLines);
    }

    public static void activate() {
        activated = true;
        if (gui == null) gui = new GuiLogger("Debugger - Logger", log);
    }

    public static void deactivate() {
        activated = false;
    }

    public static void setActive(boolean active) {
        activated = active;
        if (activated && gui == null) gui = new GuiLogger("Debugger - Logger", log);
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

    public static GuiLogger getGuiLogger() {
        return gui;
    }

    public static void debug(String text) {
        text = "D: " + text;
        System.out.println(text);
        log.add(text);
        if (activated) {
            try {
                gui.updateText();
            } catch (Exception e) {
            }
        }
    }
}
