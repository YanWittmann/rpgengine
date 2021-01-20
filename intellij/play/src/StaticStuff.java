
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class StaticStuff {
    public static Font pixelFont = null;
    public static ScriptEngineManager mgr;
    public static ScriptEngine engine;
    public static String lastInput = "";
    public static String projectName = "RPG Engine", adventureFileEnding = ".adv", adventureFileEndingNoDot = "adv", dataFileEnding = ".advdata", dataFileEndingNoDot = "advdata";
    private static Interpreter interpreter;
    private static Dimension screenSize;
    private static AffineTransform affinetransform = new AffineTransform();
    private static FontRenderContext frc = new FontRenderContext(affinetransform, true, true);

    public static boolean prepare(Interpreter pInterpreter) {
        try {
            interpreter = pInterpreter;
            screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            getPixelatedFont();
            mgr = new ScriptEngineManager();
            engine = mgr.getEngineByName("JavaScript");
            evaluateMathExpression("");
            commandYellow.add("set");
            commandYellow.add("print");
            commandYellow.add("go");
            commandYellow.add("execute");
            commandYellow.add("selector");
            commandYellow.add("expression");
            commandYellow.add("inv");
            commandYellow.add("inventory");
            commandYellow.add("tag");
            commandYellow.add("sleep");
            commandYellow.add("wait");
            commandYellow.add("pause");
            commandYellow.add("player");
            commandYellow.add("reload");
            commandYellow.add("image");
            commandYellow.add("audio");
            commandYellow.add("evaluate");
            commandYellow.add("log");
            commandYellow.add("clear");
            commandYellow.add("if");
            commandYellow.add("return");
            commandYellow.add("break");
            commandYellow.add("continue");
            String files[] = FileManager.getFilesWithEnding("res/txt/basecolors/", StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    baseColors.add(new ColorObject(FileManager.readFile("res/txt/basecolors/" + files[i])));
                } catch (Exception e) {
                    error("Color '" + files[i] + "' contains invalid data.\nDSA Engine will exit:\n" + e);
                    System.exit(109);
                }
            }
            ArrayList<Color> c = new ArrayList<Color>();
            for (ColorObject co : baseColors) c.add(co.color);
            colors.addAll(c);
            ArrayList<String> s = new ArrayList<String>();
            for (ColorObject co : baseColors) s.add(co.name);
            colorNames.addAll(s);
            s.clear();
            for (ColorObject co : baseColors) s.add(co.uid);
            colorUIDs.addAll(s);
            FindRealTextSize.init();
            return true;
        } catch (Exception e) {
            Popup.error("An error occurred", "Unable to setup StaticStuff:\n" + e);
            return false;
        }
    }

    public static Font getPixelatedFont() {
        if (pixelFont == null) {
            pixelFont = FileManager.getFont("res/fonts/Pixeled.ttf").deriveFont(Interpreter.getScaledValue(17f));
            GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
            genv.registerFont(pixelFont);
            fonts.put(Interpreter.getScaledValue(17f), pixelFont);
        }
        return pixelFont;
    }

    public static Font recalculatePixelatedFont() {
        pixelFont = pixelFont.deriveFont(Interpreter.getScaledValue(17f));
        fonts.clear();
        fonts.put(Interpreter.getScaledValue(17f), pixelFont);
        return pixelFont;
    }

    private static HashMap<Float, Font> fonts = new HashMap<>();

    public static Font getPixelatedFont(float size) {
        if (fonts.containsKey(size)) return fonts.get(size);
        if (pixelFont == null) getPixelatedFont();
        Font temp = pixelFont.deriveFont(Interpreter.getScaledValue(size));
        fonts.put(size, temp);
        return temp;
    }

    public static int getTextWidthWithFontRemoveFormatting(String text, Font font) {
        return (int) (font.getStringBounds(removeTextFormatting(text), frc).getWidth());
    }

    public static int getTextWidthWithFont(String text, Font font) {
        return (int) (font.getStringBounds(text, frc).getWidth());
    }

    public static int getTextHeightWithFont(String text, Font font) {
        return (int) (font.getStringBounds(text, frc).getHeight());
    }

    public static String removeTextFormatting(String s) {
        return s.replaceAll("\\[\\[[^:]+:([^\\]]+)\\]\\]", "$1").replaceAll("<[^>]+>", "");
    }

    public static int getLongestLineWidth(String[] lines, Font font) {
        int longest = 0, current;
        for (int i = 0; i < lines.length; i++) {
            current = getTextWidthWithFontRemoveFormatting(lines[i], font);
            if (current > longest) longest = current;
        }
        return longest;
    }

    public static int getLongestLineLength(String[] lines) {
        int longest = 0;
        for (int i = 0; i < lines.length; i++)
            if (lines[i].length() > longest) longest = removeTextFormatting(lines[i]).length();
        return longest;
    }

    public static int countOccurrences(String text, String find) {
        int count = 0, lastIndex = 0;
        while (lastIndex != -1) {
            lastIndex = text.indexOf(find, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += find.length();
            }
        }
        return count;
    }

    static PopupButtons popButtons;

    public static int openPopup(String text, String[] options) {
        Log.add("Open popup buttons '" + text + "'");
        popButtons = new PopupButtons(text, options);
        new Thread() {
            public void run() {
                popButtons.createComponents();
            }
        }.start();
        while (popButtons.selected == -1) Sleep.milliseconds(100);
        try {
            lastInput = popButtons.selected + "";
        } catch (Exception e) {
        }
        Log.add("Result: " + lastInput);
        return popButtons.selected;
    }

    static PopupText popText;

    public static void openPopup(String text) {
        Log.add("Open popup text '" + text + "'");
        popText = new PopupText(text);
        new Thread() {
            public void run() {
                popText.createComponents();
            }
        }.start();
        while (popText.selected == -1) Sleep.milliseconds(100);
    }

    static PopupTextInput popTextInput;

    public static String openPopup(String text, String pretext) {
        Log.add("Open popup text input '" + text + "'");
        popTextInput = new PopupTextInput(text, pretext);
        new Thread() {
            public void run() {
                popTextInput.createComponents();
            }
        }.start();
        while (popTextInput.selected == -1) Sleep.milliseconds(100);
        try {
            lastInput = popTextInput.result;
        } catch (Exception e) {
        }
        Log.add("Result: " + lastInput);
        return popTextInput.result;
    }

    static PopupDropDown popDropDown;

    public static String openPopup(String text, String options[], String preselected) {
        Log.add("Open popup text input '" + text + "'");
        popDropDown = new PopupDropDown(text, options, preselected);
        new Thread() {
            public void run() {
                popDropDown.createComponents();
            }
        }.start();
        while (popDropDown.selected == -1) Sleep.milliseconds(100);
        try {
            lastInput = popDropDown.result;
        } catch (Exception e) {
        }
        return popDropDown.result;
    }

    static PopupDice popDice;

    public static int rollDice(String text, int sides, int duration, boolean autoRoll) {
        Log.add("Open popup text input '" + text + "'");
        if (sides == 6 || sides == 20) {
            popDice = new PopupDice(text, sides, duration, autoRoll, sides);
            new Thread() {
                public void run() {
                    popDice.createComponents();
                }
            }.start();
            while (popDice.selected == -1) Sleep.milliseconds(100);
            Sleep.milliseconds(1500);
            Log.add("Result: " + popDice.selected);
            return popDice.selected;
        } else if (sides < 20) {
            popDice = new PopupDice(text, 6, duration, autoRoll, sides);
            new Thread() {
                public void run() {
                    popDice.createComponents();
                }
            }.start();
            while (popDice.selected == -1) Sleep.milliseconds(100);
            Sleep.milliseconds(1500);
            Log.add("Result: " + popDice.selected);
            return popDice.selected;
        } else {
            int result = randomNumber(0, sides);
            Log.add("Result: " + sides);
            return result;
        }
    }

    public static String waitForLineInput() {
        Log.add("Waiting for line input...");
        GuiMainConsole.waitForUserInputLastString = "waitingForInput";
        while (GuiMainConsole.waitForUserInputLastString.equals("waitingForInput")) Sleep.milliseconds(100);
        try {
            lastInput = GuiMainConsole.waitForUserInputLastString;
        } catch (Exception e) {
        }
        Log.add("Result: " + lastInput);
        return GuiMainConsole.waitForUserInputLastString;
    }

    public static void error(String text) {
        Log.add("Error: " + text);
        openPopup("[[red:" + text + "]]");
    }

    public static ArrayList<ColorObject> baseColors = new ArrayList<ColorObject>();
    private static ArrayList<Color> colors = new ArrayList<Color>();
    private static ArrayList<String> colorNames = new ArrayList<String>();
    private static ArrayList<String> colorUIDs = new ArrayList<String>();

    public static void prepareColors(ArrayList<Color> color, ArrayList<String> name, ArrayList<String> uid) {
        colors.clear();
        colorNames.clear();
        colorUIDs.clear();
        colors.addAll(color);
        colorNames.addAll(name);
        colorUIDs.addAll(uid);
    }

    public static Color getColor(String name) {
        if (Manager.ready)
            try {
                return Manager.getColorByName(name);
            } catch (Exception e) {
            }
        try {
            for (ColorObject co : baseColors) if (co.name.equals(name)) return co.color;
        } catch (Exception e) {
        }
        return new Color(191, 59, 178);
    }

    public static boolean getColorReady() {
        return Manager.ready;
    }

    public static String prepareString(String s) {
        s = "[[def_text_color_main:" + s.replace("ESCAPEDCURVEDBRACKETSOPEN", "{").replace("ESCAPEDCURVEDBRACKETSCLOSED", "}") + "]]";
        for (int i = 0; i < colors.size(); i++)
            s = s.replace("[[" + colorNames.get(i) + ":", "<font color=\"" + colorToHex(colors.get(i)) + "\">");
        for (int i = 0; i < colors.size(); i++)
            s = s.replace("[[" + colorUIDs.get(i) + ":", "<font color=\"" + colorToHex(colors.get(i)) + "\">");
        return s.replace("ä", "ae").replace("Ä", "AE").replace("ö", "oe").replace("Ö", "OE").replace("ü", "ue").replace("Ü", "UE").replace("]]", "</font>")
                .replaceAll("([^A-Za-z])elf", "$1" + Interpreter.lang("playerAttrelf")).replace("warrior", Interpreter.lang("playerAttrwarrior")).replaceAll("([^A-Za-z])mage", "$1" + Interpreter.lang("playerAttrmage"))
                .replace("novadi", Interpreter.lang("playerAttrnovadi")).replace("stray", Interpreter.lang("playerAttrstray")).replace("thorwaler", Interpreter.lang("playerAttrthorwaler"))
                .replace("dwarf", Interpreter.lang("playerAttrdwarf")).replace("courage", Interpreter.lang("playerAttrMU")).replace("wisdom", Interpreter.lang("playerAttrKL"))
                .replace("intuition", Interpreter.lang("playerAttrIN")).replace("charisma", Interpreter.lang("playerAttrCH")).replace("dexterity", Interpreter.lang("playerAttrFF"))
                .replace("agility", Interpreter.lang("playerAttrGE")).replace("strength", Interpreter.lang("playerAttrKK"));
    }

    private static ArrayList<String> commandYellow = new ArrayList<String>();
    private static ArrayList<String> customCommandYellow = null;

    public static String prepareStringForPlayer(String s) {
        s = removePrepareString(s);
        s = s.replaceAll("\\[([^\\[\\]\\ ]+)\\]", "[[[yellow:$1]]]").replaceAll("([a-zA-Z0-9]{16})", "[[dark-green:$1]]")
                .replaceAll("(-?[0-9]+\\.?(?:[0-9]+)?)", "[[dark-green:$1]]").replaceAll("'([^\\']+)'", "[[aqua:'$1']]").replaceAll("(_[a-zA-Z]+)", "[[yellow:$1]]")
                .replace("Ã¤", "ae").replace("Ã„", "AE").replace("Ã¶", "oe").replace("Ã–", "OE").replace("Ã¼", "ue").replace("Ãœ", "UE");
        if (s.contains(" "))
            for (int i = 0; i < commandYellow.size(); i++)
                s = s.replaceAll(commandYellow.get(i) + " (.+)", "[[yellow:" + commandYellow.get(i) + "]] $1");
        else
            for (int i = 0; i < commandYellow.size(); i++)
                s = s.replaceAll(commandYellow.get(i), "[[yellow:" + commandYellow.get(i) + "]]");
        if (s.contains(" ") && customCommandYellow != null)
            for (int i = 0; i < customCommandYellow.size(); i++)
                s = s.replaceAll(customCommandYellow.get(i) + " (.+)", "[[yellow:" + customCommandYellow.get(i) + "]] $1");
        else if (customCommandYellow != null)
            for (int i = 0; i < customCommandYellow.size(); i++)
                s = s.replaceAll(customCommandYellow.get(i), "[[yellow:" + customCommandYellow.get(i) + "]]");
        s = s.replace("dark-green", "dark_green");
        return prepareString(s);
    }

    public static void setCustomCommands(ArrayList<String> commands) {
        customCommandYellow = commands;
    }

    public static String removePrepareString(String s) {
        return s.replaceAll("<font color=\"#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})\">", "").replaceAll("</[A-Za-z]+>", "").replaceAll("<[A-Za-z]+>", "").replace("]]", "").replaceAll("\\[\\[[A-Za-z]+:", "");
    }

    public static String colorToHex(Color c) {
        return "#" + Integer.toHexString(c.getRGB()).substring(2);
    }

    public static String insertVariables(String text) {
        if (interpreter == null) return text;
        return interpreter.prepareStringReplaceVar(text);
    }

    public static String prepareStringWithLineLength(String text, int length) {
        String parts[] = text.split("<br>");
        text = "";
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].length() > length)
                parts[i] = parts[i].substring(0, length) + "<br>" + parts[i].substring(length, parts[i].length());
            if (i != parts.length) text += parts[i] + "<br>";
            else text += parts[i];
        }
        if (prepareStringWithLineLengthRecheckCheck(text, length)) return prepareStringWithLineLength(text, length);
        return text;
    }

    public static boolean prepareStringWithLineLengthRecheckCheck(String text, int length) {
        String parts[] = text.split("<br>");
        text = "";
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].length() > length) return true;
        }
        return false;
    }

    public static String[] replaceAllLines(String[] array, String find, String replace) {
        for (int i = 0; i < array.length; i++) array[i] = array[i].replace(find, replace);
        return array;
    }

    public static boolean isValidUID(String uid) {
        if (uid == null) return false;
        if (uid.equals("")) return false;
        if (uid.contains("#")) {
            error("Invalid UID: '" + uid + "'\nA UID may not be a selector.");
            return false;
        }
        if (uid.matches("[\\da-z]{16}")) return true;
        if (!uid.matches("[0-9a-z]{16}") && !(uid.length() == 16)) {
            error("Invalid UID: '" + uid + "'\nA UID has 16 alphanumberic characters.");
            return false;
        }
        return true;
    }

    public static boolean isValidUIDSilent(String uid) {
        if (uid == null) return false;
        if (uid.equals("")) return false;
        if (uid.contains("#")) return false;
        if (!uid.matches("[\\da-z]{16}")) return false;
        if (!uid.matches("[0-9a-z]{16}") && !(uid.length() == 16)) return false;
        return true;
    }

    public static int evaluateMathExpression(String expr) {
        try {
            return Integer.parseInt("" + engine.eval(expr));
        } catch (Exception e) {
            return -969657;
        }
    }

    public static int randomNumber(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    public static String replaceLastCharacterIfEquals(String text, String character) {
        if (text.endsWith(character))
            return text.substring(0, text.length() - 1);
        return text;
    }

    public static int evaluateRoll(String roll, boolean visible, boolean autoRoll) {
        roll = roll.replaceAll("(\\d)([+-])(\\d)", "$1 $2 $3");
        String originalRoll = roll;
        if (visible) {
            Log.add("Evaluating roll: " + roll);
            new GuiHoverText(roll);
            Sleep.milliseconds(600);
        }
        int value = 0;

        while (roll.length() > 0) { // 1W6 + 4 - 2W20
            String current = "none";
            String addRemove = "none";
            //find current value
            if (roll.matches("[^ +-]+ .+")) { // is first entry, there are more
                current = roll.replaceAll("([^ +-]+) .+", "$1");
                addRemove = "+";
                roll = roll.replaceAll("[^ +-]+ (.+)", "$1");
            } else if (roll.matches("[+-] [^ +-]+ .+")) { // is between entry, there are more
                current = roll.replaceAll("[+-] ([^ +-]+) .+", "$1");
                addRemove = roll.replaceAll("([+-]) [^ +-]+ .+", "$1");
                roll = roll.replaceAll("[+-] [^ +-]+ (.+)", "$1");
            } else if (roll.matches("[^ +-]+")) { // is last entry without +-
                current = roll.replaceAll("([^ +-]+)", "$1");
                addRemove = "+";
                roll = "";
            } else if (roll.matches("[+-] [^ +-]+")) { // is last entry with +-
                current = roll.replaceAll("[+-] ([^ +-]+)", "$1");
                addRemove = roll.replaceAll("([+-]) [^ +-]+", "$1");
                roll = "";
            }
            if (current.equals("none") || addRemove.equals("none")) return -1;
            //evaluate current part
            int currentValue;
            if (current.matches("[0-9]+[WD][0-9]+")) { // dice roll
                int sides = Integer.parseInt(current.replaceAll("[0-9]+[WD]", ""));
                int amount = Integer.parseInt(current.replaceAll("[WD][0-9]+", ""));
                for (int i = 0; i < amount; i++) {
                    if (visible)
                        currentValue = rollDice(Interpreter.lang("popupRollEvaluatePart", current), sides, 5, autoRoll);
                    else currentValue = randomNumber(1, sides);
                    if (addRemove.equals("+")) value += currentValue;
                    else value -= currentValue;
                }
            } else { // add or remove value
                currentValue = Integer.parseInt(current);
                if (addRemove.equals("+")) value += currentValue;
                else value -= currentValue;
            }
        }

        if (value < 0) value = 0;
        if (visible) {
            new GuiHoverText(Interpreter.lang("popupRollEvaluateResult", "" + value));
            Log.add("Result (" + originalRoll + "): " + value);
        }
        return value;
    }

    public static int estimatedRollValue(String roll) {
        int all = 0;
        for (int i = 0; i < 20; i++) all += evaluateRoll(roll, false, false);
        return (int) (all / 20);
    }

    public static long time() {
        return System.currentTimeMillis();
    }

    public static void sortArray(String[] arr) {
        Arrays.sort(arr);
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replacement);
    }

    private static double screenWidth = 0;
    private static double screenHeight = 0;

    public static int getScreenWidth() {
        if (screenWidth == 0) screenWidth = screenSize.getWidth();
        return (int) screenWidth;
    }

    public static int getScreenHeight() {
        if (screenHeight == 0) screenHeight = screenSize.getHeight();
        return (int) screenHeight;
    }

    public static int getRandomPopupMovement() {
        return randomNumber(-40, 40);
    }
}
