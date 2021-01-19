
import java.awt.*;

public class ColorObject extends Entity {
    Color color;

    public ColorObject() {
        type = "Color";
        name = "New Color";
        description = "Description";
        color = new Color(StaticStuff.randomNumber(100, 250), StaticStuff.randomNumber(100, 250), StaticStuff.randomNumber(100, 250));
    }

    public ColorObject(String name, String desc) {
        type = "Color";
        this.name = name;
        description = desc;
    }

    public ColorObject(String[] fileInput) {
        type = "Color";
        try {
            name = fileInput[0];
            description = fileInput[1];
            uid = fileInput[2];
            color = new Color(Integer.parseInt(fileInput[3]), Integer.parseInt(fileInput[4]), Integer.parseInt(fileInput[5]));
            for (int i = 6; i < fileInput.length; i++) {
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
            StaticStuff.error("Color '" + name + "' contains invalid data.");
        }
    }

    public String generateSaveString() {
        StringBuilder str = new StringBuilder("" + name + "\n" + description + "\n" + uid + "\n" + color.getRed() + "\n" + color.getGreen() + "\n" + color.getBlue() + "\n");
        for (int i = 0; i < eventName.size(); i++)
            str.append("\n++ev++").append(eventName.get(i)).append("---").append(eventCode.get(i));
        for (String tag : tags) str.append("\n++tag++").append(tag);
        for (int i = 0; i < localVarName.size(); i++)
            str.append("\n").append("\n++variable++").append(localVarUids.get(i)).append("---").append(localVarName.get(i)).append("---").append(localVarType.get(i)).append("---").append(localVarValue.get(i));
        return str.toString();
    }
}
