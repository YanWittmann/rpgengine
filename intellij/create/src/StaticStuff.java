
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class StaticStuff {
    public static String ee = "false", clipboardBefore = "";
    public static String projectName = "RPG Engine", adventureFileEnding = ".adv", adventureFileEndingNoDot = "adv", dataFileEnding = ".advdata", dataFileEndingNoDot = "advdata";

    public static boolean isValidUID(String uid) {
        if (uid == null) return false;
        if (uid.equals("")) return false;
        if (uid.matches("[\\da-z]{16}")) return true;
        if (!uid.matches("[0-9a-z]{16}") && !(uid.length() == 16)) {
            Popup.error(StaticStuff.projectName, "Invalid UID: '" + uid + "'\nA UID has 16 alphanumberic characters.");
            return false;
        }
        if (!uid.matches("[\\da-z]{16}")) return false;
        return true;
    }

    public static boolean isValidUIDSilent(String uid) {
        if (uid == null) return false;
        if (uid.equals("")) return false;
        if (uid.matches("[\\da-z]{16}")) return true;
        if (!uid.matches("[0-9a-z]{16}") && !(uid.length() == 16)) return false;
        return uid.matches("[\\da-z]{16}");
    }

    public static String autoDetectUID(String inputText) {
        String clip = "", uid = "";
        try {
            clip = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (Exception ignored) {
        }
        if (clip.matches("[0-9a-z]{16}") && clip.length() == 16)
            uid = Popup.input(inputText + "\nThis UID has been automatically detected:", clip);
        else uid = Popup.input(inputText, "");
        copyString(clipboardBefore);
        return uid;
    }

    public static String autoDetectUID() {
        ctrlC();
        String clip = "";
        try {
            clip = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (Exception ignored) {
        }
        copyString(clipboardBefore);
        return clip;
    }

    public static String getClipboard() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Have a nice day! :)";
    }

    private static Robot rob;

    public static void ctrlC() {
        try {
            if (rob == null) rob = new Robot();
            rob.keyPress(KeyEvent.VK_CONTROL);
            rob.keyPress(KeyEvent.VK_C);
            rob.delay(50);
            rob.keyRelease(KeyEvent.VK_CONTROL);
            rob.keyRelease(KeyEvent.VK_C);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyString(String text) {
        clipboardBefore = getClipboard();
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    public static String getWordAtIndex(String str, int index) {
        if (index == -1 || index > str.length()) return "";
        int minIndex = index;
        while (true) {
            minIndex--;
            if (minIndex <= -1) break;
            if (str.charAt(minIndex) == ' ' || str.charAt(minIndex) == '\n') break;
        }
        minIndex++;
        int maxIndex = index;
        while (true) {
            maxIndex++;
            if (maxIndex >= str.length()) break;
            if (str.charAt(maxIndex) == ' ' || str.charAt(maxIndex) == '\n') break;
        }

        return str.substring(minIndex, maxIndex);
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

    public static int refactorArrayList(String find, String replace, ArrayList<String> list) {
        int occ = 0;
        for (int i = 0; i < list.size(); i++) {
            occ += StaticStuff.countOccurrences(list.get(i), find);
            list.set(i, list.get(i).replace(find, replace));
        }
        return occ;
    }

    public static int randomNumber(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    public static String shuffle(String input) {
        ArrayList<Character> characters = new ArrayList<Character>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(input.length());
        while (characters.size() != 0) {
            int randPicker = (int) (Math.random() * characters.size());
            output.append(characters.remove(randPicker));
        }
        return output.toString();
    }

    public static String makeWithSpace(String input) {
        StringBuilder output = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            output.append(input.charAt(i)).append(" ");
        }
        return output.toString();
    }

    public static String generateRandomMessage() {
        return generateRandomMessageFromFile("res/txt/genericMessage" + StaticStuff.dataFileEnding);
    }

    public static String generateRandomMessageFromFile(String filename) {
        try {
            String[] input = FileManager.readFile(filename);
            return input[randomNumber(0, input.length - 1)];
        } catch (Exception e) {
            return "File does not exist: " + filename;
        }
    }

    public static String getWindowsUsername() {
        String user = "Me";
        try {
            user = System.getProperty("user.name");
        } catch (Exception ignored) {
        }
        return user;
    }

    public static boolean isImageFile(String path) {
        if (path.matches(".+\\.png")) return true;
        if (path.matches(".+\\.jpg")) return true;
        if (path.matches(".+\\.jpeg")) return true;
        if (path.matches(".+\\.bmp")) return true;
        if (path.matches(".+\\.gif")) return false;
        if (path.matches(".+\\.img")) return true;
        if (path.matches(".+\\.webp")) return true;
        return false;
    }

    public static boolean isAudioFile(String path) { //only wav is supported
        if (path.matches(".+\\.flac")) return false;
        if (path.matches(".+\\.m4a")) return false;
        if (path.matches(".+\\.mp3")) return false;
        if (path.matches(".+\\.mp4")) return false;
        return path.matches(".+\\.wav");
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replacement);
    }

    public static String[] appendArray(String[] arr, String app) {
        String[] ret = new String[arr.length + 1];
        System.arraycopy(arr, 0, ret, 0, arr.length);
        ret[arr.length] = app;
        return ret;
    }

    public static void printArray(Object[] arr) {
        System.out.println(Arrays.toString(arr));
    }

    public static String arrayToString(Object[] arr) {
        return Arrays.toString(arr);
    }

    public static void openURL(String url) {
        Desktop desktop = java.awt.Desktop.getDesktop();
        try {
            URI oURL = new URI(url);
            desktop.browse(oURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void mailto(String url) {
        try {
            Desktop desktop;
            if (Desktop.isDesktopSupported()
                    && (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.MAIL)) {
                URI mailto = new URI(url);
                desktop.mail(mailto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String colorScheme = "dark";
    private static final HashMap<String, Color> colors = new HashMap<>();

    public static void setColorScheme(String pColorScheme) {
        colorScheme = pColorScheme;
        updateColors();
    }

    public static void toggleColorScheme() {
        if (colorScheme.equals("dark")) colorScheme = "light";
        else colorScheme = "dark";
        updateColors();
    }

    public static void selectColorScheme() {
        int result = Popup.selectButton("Stylesheet selection", "What stylesheet do you want to use?", new String[]{"dark", "light", "custom"});
        if (result == 0) setColorScheme("dark");
        else if (result == 1) setColorScheme("light");
        else if (result == 2) {
            FileManager.makeDirectory("../../res/stylesheets/");
            if (!FileManager.fileExists("../../res/stylesheets/demo.style"))
                FileManager.writeToFile("../../res/stylesheets/demo.style", new String[]{"text_color:255,255,255", "text_background:13,26,48", "background:30,60,110",
                        "buttons:33,45,64", "menu_item_background:30,118,168", "menu_background:30,118,168", "error_red:107,47,36"});
            setColorScheme(Popup.dropDown("Stylesheet selection", "Select one of the stylesheets in res/stylesheets", FileManager.getFiles("../../res/stylesheets/")));
        }
    }

    public static String getColorScheme() {
        return colorScheme;
    }

    private static void updateColors() {
        if (colorScheme.equals("dark")) {
            colors.put("text_color", new Color(255, 255, 255));
            colors.put("text_background", new Color(10, 10, 10));
            colors.put("background", new Color(31, 31, 31));
            colors.put("buttons", new Color(34, 35, 36));
            colors.put("menu_item_background", new Color(31, 31, 31));
            colors.put("menu_background", new Color(22, 22, 22));
            colors.put("error_red", new Color(107, 47, 36));
        } else if (colorScheme.equals("light")) {
            colors.put("text_color", new Color(0, 0, 0));
            colors.put("text_background", new Color(255, 255, 255));
            colors.put("background", new Color(240, 240, 240));
            colors.put("buttons", new Color(214, 217, 223));
            colors.put("menu_item_background", new Color(240, 240, 240));
            colors.put("menu_background", new Color(255, 255, 255));
            colors.put("error_red", new Color(245, 120, 98));
        } else {
            if (FileManager.fileExists("../../res/stylesheets/" + colorScheme)) {
                String[] style = FileManager.readFile("../../res/stylesheets/" + colorScheme);
                for (String s : style) {
                    String[] split = s.split("[:, ]+");
                    if (split.length == 4)
                        colors.put(split[0], new Color(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3])));
                    else {
                        Popup.error("Stylesheet", "Stylesheet " + colorScheme + " contains invalid data:\n" + s);
                        toggleColorScheme();
                        return;
                    }
                }
            } else {
                Popup.error("Stylesheet", "Stylesheet does not exist: " + colorScheme);
                toggleColorScheme();
            }
        }
    }

    public static Color getColor(String name) {
        if (colors.containsKey(name)) return colors.get(name);
        else return new Color(235, 52, 210);
    }

    public static Color getRandomSaturatedColorForCreditsHover() {
        //check if clipboard contains color
        String clipboard = getClipboard();
        if (clipboard.matches("#?[\\da-f]{6}")) {
            return hex2Rgb(clipboard);
        } else if (clipboard.matches("\\d{1,3}[, ]{1,2}\\d{1,3}[, ]{1,2}\\d{1,3}")) {
            String[] split = clipboard.split("[, ]{1,2}");
            return new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }

        //otherwise generate saturated color
        int red, green, blue, main1 = randomNumber(0, 2), main0;
        do {
            main0 = randomNumber(0, 2);
        } while (main0 == main1);
        red = randomNumber(0, 254);
        green = randomNumber(0, 254);
        blue = randomNumber(0, 254);
        if (main1 == 0) red = randomNumber(240, 254);
        else if (main1 == 1) green = randomNumber(240, 254);
        else if (main1 == 2) blue = randomNumber(240, 254);
        if (main0 == 0) red = randomNumber(0, 10);
        else if (main0 == 1) green = randomNumber(0, 10);
        else if (main0 == 2) blue = randomNumber(0, 10);
        return new Color(red, green, blue);
    }

    public static Color hex2Rgb(String colorStr) {
        colorStr = colorStr.replace("#", "");
        return new Color(
                Integer.valueOf(colorStr.substring(0, 2), 16),
                Integer.valueOf(colorStr.substring(2, 4), 16),
                Integer.valueOf(colorStr.substring(4, 6), 16));
    }

    private static final Font baseFont = new Font("sansserif", 0, 12);

    public static Font getBaseFont() {
        return baseFont;
    }

    private static Dimension screenSize;
    private static double screenWidth = 0;
    private static double screenHeight = 0;

    public static int getScreenWidth() {
        if (screenWidth == 0) {
            screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            screenWidth = screenSize.getWidth();
        }
        return (int) screenWidth;
    }

    public static int getScreenHeight() {
        if (screenHeight == 0) {
            screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            screenHeight = screenSize.getHeight();
        }
        return (int) screenHeight;
    }
}
