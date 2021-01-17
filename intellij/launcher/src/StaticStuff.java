
import java.awt.*;
import java.net.URI;

public class StaticStuff {
    public static Font pixelFont = null;

    public static boolean prepare() {
        try {
            getPixelatedFont();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Font getPixelatedFont() {
        if (pixelFont == null) {
            pixelFont = FileManager.getFont("files/res/fonts/Pixeled.ttf").deriveFont(17f);
            GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
            genv.registerFont(pixelFont);
        }
        return pixelFont;
    }

    public static int getLongestLineLength(String[] lines) {
        int longest = 0;
        for (int i = 0; i < lines.length; i++)
            if (lines[i].length() > longest) longest = lines[i].length();
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

    public static Color getColor(String name) {
        if (name.equals("background")) return new Color(0, 0, 0);
        else if (name.equals("white_border")) return new Color(255, 255, 255);
        else if (name.equals("white")) return new Color(250, 250, 250);
        return new Color(191, 59, 178);
    }

    static PopupButtons popButtons;

    public static int openPopup(String text, String[] options) {
        popButtons = new PopupButtons(text, options);
        new Thread() {
            public void run() {
                popButtons.createComponents();
            }
        }.start();
        while (popButtons.selected == -1) Sleep.milliseconds(100);
        return popButtons.selected;
    }

    static PopupText popText;

    public static void openPopup(String text) {
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
        popTextInput = new PopupTextInput(text, pretext);
        new Thread() {
            public void run() {
                popTextInput.createComponents();
            }
        }.start();
        while (popTextInput.selected == -1) Sleep.milliseconds(100);
        return popTextInput.result;
    }

    public static Color red = new Color(214, 54, 54), blue = new Color(52, 162, 217), green = new Color(104, 222, 53), orange = new Color(245, 180, 17), purple = new Color(186, 57, 237);
    public static Color yellow = new Color(255, 240, 31), aqua = new Color(36, 240, 226), pink = new Color(235, 26, 99), dark_green = new Color(67, 143, 66), white = new Color(250, 250, 250);
    public static Color black = new Color(33, 33, 33), gray = new Color(181, 181, 181), gold = new Color(230, 190, 48);

    public static String prepareString(String s) {
        return s.replace("[[red:", "<font color=\"" + colorToHex(red) + "\">").replace("[[blue:", "<font color=\"" + colorToHex(blue) + "\">").replace("[[green:", "<font color=\"" + colorToHex(green) + "\">")
                .replace("[[orange:", "<font color=\"" + colorToHex(orange) + "\">").replace("[[purple:", "<font color=\"" + colorToHex(purple) + "\">").replace("[[yellow:", "<font color=\"" + colorToHex(yellow) + "\">")
                .replace("[[aqua:", "<font color=\"" + colorToHex(aqua) + "\">").replace("[[pink:", "<font color=\"" + colorToHex(pink) + "\">").replace("[[dark_green:", "<font color=\"" + colorToHex(dark_green) + "\">")
                .replace("[[white:", "<font color=\"" + colorToHex(white) + "\">").replace("[[black:", "<font color=\"" + colorToHex(black) + "\">").replace("[[gray:", "<font color=\"" + colorToHex(gray) + "\">")
                .replace("[[gold:", "<font color=\"" + colorToHex(gold) + "\">").replace("ä", "ae").replace("Ä", "AE").replace("ö", "oe").replace("Ö", "OE").replace("ü", "ue").replace("Ü", "UE").replace("]]", "</font>")
                .replace("sbc", "]").replace("sbo", "[");
    }

    public static String colorToHex(Color c) {
        return "#" + Integer.toHexString(c.getRGB()).substring(2);
    }

    public static int randomNumber(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    public static String[] append(String base[], String append) {
        if (base == null) base = new String[]{};
        String ret[] = new String[base.length + 1];
        ret[0] = append;
        for (int i = 0; i < base.length; i++) {
            ret[i + 1] = base[i];
        }
        return ret;
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
}
