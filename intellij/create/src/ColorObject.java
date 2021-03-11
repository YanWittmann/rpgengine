
import java.awt.*;

public class ColorObject extends Entity {
    int r, g, b;

    public ColorObject() {
        name = "New Color";
        description = "Description";
        r = StaticStuff.randomNumber(100, 250);
        g = StaticStuff.randomNumber(100, 250);
        b = StaticStuff.randomNumber(100, 250);
    }

    public ColorObject(String name, String desc, int r, int g, int b) {
        this.name = name;
        description = desc;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public ColorObject(String[] fileInput) {
        try {
            name = fileInput[0];
            description = fileInput[1];
            uid = fileInput[2];
            r = Integer.parseInt(fileInput[3]);
            g = Integer.parseInt(fileInput[4]);
            b = Integer.parseInt(fileInput[5]);
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
            Popup.error(StaticStuff.PROJECT_NAME, "Color '" + name + "' contains invalid data.");
        }
    }

    public String generateSaveString() {
        String str = "" + name + "\n" + description + "\n" + uid + "\n" + r + "\n" + g + "\n" + b + "\n";
        for (int i = 0; i < eventName.size(); i++)
            str = str + "\n++ev++" + eventName.get(i) + "---" + eventCode.get(i);
        for (int i = 0; i < tags.size(); i++)
            str = str + "\n++tag++" + tags.get(i);
        for (int i = 0; i < localVarName.size(); i++)
            str = str + "\n++variable++" + localVarUids.get(i) + "---" + localVarName.get(i) + "---" + localVarType.get(i) + "---" + localVarValue.get(i) + "\n";
        return str;
    }

    public String generateInformation() {
        String str = "Name: " + name + "\nDescription: " + description + "\nEvents: ";
        for (int i = 0; i < eventName.size(); i++)
            str = str + "\n   " + i + ": " + eventName.get(i);
        str = str + "\nTags:\n";
        for (int i = 0; i < tags.size(); i++)
            str = str + " " + i + ": " + tags.get(i) + ";";
        str = str + "\nLocal variables:\n";
        for (int i = 0; i < localVarName.size(); i++)
            str = str + " " + localVarUids.get(i) + " - " + localVarName.get(i) + " - " + localVarType.get(i) + " - " + localVarValue.get(i) + "\n";
        return str;
    }

    public int additionalRefactor(String find, String replace) {
        int occ = 0;
        occ += StaticStuff.countOccurrences(r + "", find);
        r = Integer.parseInt((r + "").replace(find, replace));
        occ += StaticStuff.countOccurrences(g + "", find);
        g = Integer.parseInt((g + "").replace(find, replace));
        occ += StaticStuff.countOccurrences(b + "", find);
        b = Integer.parseInt((b + "").replace(find, replace));
        return occ;
    }

    private void setColorFromEditor() {
        if (!ColorChooserDemo.visible) {
            ColorChooserDemo.openChooser();
            return;
        }
        Color current = ColorChooserDemo.getColor();
        r = current.getRed();
        g = current.getGreen();
        b = current.getBlue();
        //contentPane.setBackground(new Color(r,g,b));
    }

    private void setColorToEditor() {
        if (!ColorChooserDemo.visible) {
            ColorChooserDemo.openChooser();
            return;
        }
        ColorChooserDemo.setColor(new Color(r, g, b));
    }

    public void openEditor() {
        new GuiEntityEditor(this, new String[]{"Name", "Description", "Add event", "Edit event", "Remove event", "Edit tags", "Edit variables", "Set color to editor", "Get color from editor"}, "Color") {
            @Override
            public void extraSetup() {
                setBackgroundColor(new Color(r, g, b));
            }

            @Override
            public void buttonPressed(int index) {
                String str;
                int choice;
                switch (index) {
                    case 0:
                        str = Popup.input("New name:", name);
                        if (str == null) return;
                        if (str.equals("")) return;
                        name = str;
                        break;
                    case 1:
                        str = Popup.input("New description:", description);
                        if (str == null) return;
                        if (str.equals("")) return;
                        description = str;
                        break;
                    case 2:
                        str = Popup.input("Event name:", "");
                        if (str == null) return;
                        if (str.equals("")) return;
                        addEvent(str);
                        break;
                    case 3:
                        str = Popup.input("Choose an event by its ID:", "");
                        if (str == null) return;
                        if (str.equals("")) return;
                        new GuiActionEditor(getEntity(), Integer.parseInt(str));
                        break;
                    case 4:
                        str = Popup.input("Choose an event by its ID:", "");
                        if (str == null) return;
                        if (str.equals("")) return;
                        deleteEvent(Integer.parseInt(str));
                        break;
                    case 5:
                        choice = Popup.selectButton(StaticStuff.PROJECT_NAME, "What do you want to do?", new String[]{"Add tag", "Remove tag", "Edit tag"});
                        if (choice == 0)
                            getEntity().addTag(Popup.input("Tag name:", ""));
                        else if (choice == 1)
                            getEntity().deleteTag(Integer.parseInt(Popup.input("Tag index:", "")));
                        else if (choice == 2)
                            getEntity().editTag(Integer.parseInt(Popup.input("Tag index:", "")), Popup.input("Tag name:", ""));
                        break;
                    case 6:
                        choice = Popup.selectButton(StaticStuff.PROJECT_NAME, "What do you want to do?", new String[]{"Add variable", "Remove variable", "Edit variable"});
                        if (choice == 0) {
                            getEntity().addVariable(Popup.input("Variable name:", ""), true);
                        } else if (choice == 1)
                            getEntity().removeVariable(StaticStuff.autoDetectUID("Variable uid:"));
                        else if (choice == 2)
                            getEntity().openVariable(StaticStuff.autoDetectUID("Variable uid:"), true);
                        break;
                    case 7:
                        setColorToEditor();
                        break;
                    case 8:
                        if (!ColorChooserDemo.visible) {
                            ColorChooserDemo.openChooser();
                            return;
                        }
                        Color current = ColorChooserDemo.getColor();
                        r = current.getRed();
                        g = current.getGreen();
                        b = current.getBlue();
                        setBackgroundColor(new Color(r, g, b));
                        break;
                    default:
                        Popup.error(StaticStuff.PROJECT_NAME, "Invalid action\nButton " + index + " does not exist.");
                }
                update();
            }
        };
    }
}
