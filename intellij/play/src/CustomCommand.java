
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomCommand extends Entity {
    public String syntaxRegex, parameters, syntaxName;
    private Pattern patt;

    public CustomCommand(String[] fileInput) {
        type = "CustomCommand";
        try {
            name = fileInput[0];
            description = fileInput[1];
            uid = fileInput[2];
            syntaxRegex = fileInput[3];
            syntaxName = syntaxRegex.split(" ")[0];
            patt = Pattern.compile(syntaxRegex);
            parameters = fileInput[4];
            for (int i = 5; i < fileInput.length; i++) {
                if (fileInput[i].contains("++ev++")) {
                    fileInput[i] = fileInput[i].replace("++ev++", "");
                    eventName.add(fileInput[i].split("---")[0]);
                    eventCode.add(fileInput[i].split("---")[1]);
                } else if (fileInput[i].contains("++tag++")) {
                    tags.add(fileInput[i].replace("++tag++", ""));
                } else if (fileInput[i].contains("++variable++")) {
                    fileInput[i] = fileInput[i].replace("++variable++", "");
                    localVarUids.add(fileInput[i].split("---")[0]);
                    localVarName.add(fileInput[i].split("---")[1]);
                    localVarType.add(fileInput[i].split("---")[2]);
                    localVarValue.add(fileInput[i].split("---")[3]);
                }
            }
        } catch (Exception e) {
            StaticStuff.error("CustomCommand '" + name + "' contains invalid data.");
        }
    }

    public String generateSaveString() {
        StringBuilder str = new StringBuilder("" + name + "\n" + description + "\n" + uid + "\n" + syntaxRegex + "\n" + parameters);
        for (int i = 0; i < eventName.size(); i++)
            str.append("\n++ev++").append(eventName.get(i)).append("---").append(eventCode.get(i));
        for (String tag : tags) str.append("\n++tag++").append(tag);
        for (int i = 0; i < localVarName.size(); i++)
            str.append("\n").append("\n++variable++").append(localVarUids.get(i)).append("---").append(localVarName.get(i)).append("---").append(localVarType.get(i)).append("---").append(localVarValue.get(i));
        return str.toString();
    }

    public boolean matches(String command) {
        Matcher m = patt.matcher(command);
        return m.find();
    }

    public String[] getArgs(String command) {
        Matcher m = patt.matcher(command);
        if (m.find()) {
            String args[] = new String[m.groupCount()];
            String argsNames[] = parameters.split(";");
            for (int i = 0; i < m.groupCount(); i++) {
                args[i] = argsNames[i] + ":" + m.group(i + 1);
            }
            return args;
        }
        return new String[]{};
    }

    public String getFirstWords() {
        if (syntaxRegex.length() == 0) return "malformed command";
        if (syntaxRegex.matches("[^(]+ ?\\(.+")) {
            return syntaxRegex.replaceAll("([^(]+) ?\\(.+", "$1");
        } else if (syntaxRegex.contains(" ")) {
            return syntaxRegex.replaceAll("([^ ]+) .*", "$1");
        } else return syntaxRegex;
    }

    public String toString() {
        return name + "; " + description + "; " + uid + "; " + syntaxRegex + "; " + parameters;
    }
}
