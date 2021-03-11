import java.util.Arrays;

/**
 * Works just as a StringBuilder, but every time you append to the LineBuilder, a new line is created.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.<br>
 * @see StringBuilder StringBuilder
 */
public class LineBuilder {
    private final StringBuilder stringBuilder = new StringBuilder();
    private String linebreakSymbol = "\n";
    private final static String LINEBREAK = "LBEOL";

    public LineBuilder(String[] string) {
        append(string);
    }

    public LineBuilder(Object value) {
        appendAny(value);
    }

    public LineBuilder() {
    }

    public void append(String string) {
        appendAny(string);
    }

    public void append(String[] string) {
        Arrays.stream(string).forEach(this::append);
    }

    public void append(int integer) {
        appendAny(integer);
    }

    public void appendAny(Object value) {
        if (stringBuilder.length() > 0) stringBuilder.append(LINEBREAK);
        stringBuilder.append(value.toString());
    }

    public String toString() {
        return stringBuilder.toString().replace(LINEBREAK, linebreakSymbol);
    }

    public String[] toLines() {
        return stringBuilder.toString().split(LINEBREAK);
    }

    public int length() {
        return stringBuilder.length();
    }

    public int lines() {
        if (stringBuilder.length() == 0) return 0;
        return 1 + countOccurrences(stringBuilder.toString(), LINEBREAK);
    }

    public void setLinebreakSymbol(String linebreakSymbol) {
        this.linebreakSymbol = linebreakSymbol;
    }

    private int countOccurrences(String text, String find) {
        return (text.length() - text.replace(find, "").length()) / find.length();
    }
}
